package com.laosuye.mychat.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.laosuye.mychat.common.commm.config.ThreadPoolConfig;
import com.laosuye.mychat.common.commm.event.UserOnlineEvent;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.IpInfo;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.domain.enums.RoleEnum;
import com.laosuye.mychat.common.user.service.IRoleService;
import com.laosuye.mychat.common.user.service.LoginService;
import com.laosuye.mychat.common.websocket.NettyUtil;
import com.laosuye.mychat.common.websocket.domain.dto.WSChannelExtraDTO;
import com.laosuye.mychat.common.websocket.domain.enums.WSRespTypeEnum;
import com.laosuye.mychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.laosuye.mychat.common.websocket.domain.vo.resp.WSLoginUrl;
import com.laosuye.mychat.common.websocket.service.WebSocketService;
import com.laosuye.mychat.common.websocket.service.adapter.WebSocketAdapter;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Data;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 专门管理websocket的service，包括推拉等
 */

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Lazy
    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IRoleService roleService;

    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 管理所有用户连接，包括登录状态和未登录状态
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 过期时间1小时
     */
    public static final Duration DURATION = Duration.ofHours(1);

    /**
     * 最大容量1000，超过就会淘汰
     */
    public static final int MAXIMUM_SIZE = 1000;

    /**
     * 临时保存登录code和channel的映射关系
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    /**
     * 保存channel到map中
     * @param channel 通道
     */
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 请求登陆二维码
     * @param channel
     */
    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //生成随机码
        Integer code = generateLoginCode(channel);
        //找微信申请带参数的二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        //把码推送给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    /**
     * channel 断开连接
     * @param channel
     */
    @Override
    public void remove(Channel channel) {
        //移除channel
        ONLINE_WS_MAP.remove(channel);
        //TODO 用户下线
    }


    /**
     * 扫码成功给channel推送消息
     * @param code 登陆码
     * @param uid 用户id
     */
    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        //确认连接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(uid);
        //移除code
        WAIT_LOGIN_MAP.invalidate(code);
        //调用登录模块获取token
        String token = loginService.login(uid);
        loginSuccess(channel, user, token);
    }

    /**
     * 发送待授权的消息
     * @param code 登陆码
     */
    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }


    /**
     * 用户扫码登陆
     * @param channel
     * @param token
     */
    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            User user = userDao.getById(validUid);
            loginSuccess(channel, user, token);
        } else {
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void sendMsgToAll(WSBaseResp<?> msg) {
        ONLINE_WS_MAP.forEach((channel, etx) -> {
            threadPoolTaskExecutor.execute(() -> {
                sendMsg(channel, msg);
            });
        });
    }

    /**
     * 登陆成功后绑定uid
     * @param channel 通道
     * @param user 用户
     * @param token token
     */
    private void loginSuccess(Channel channel, User user, String token) {
        //保存channel对应的uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        //推送成功的消息
        sendMsg(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        //用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
    }

    /**
     * 发送消息
     *
     * @param channel 通道
     * @param resp 返回的消息
     */
    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    /**
     * 生成随机码，并把channel放入到map中
     *
     * @param channel 通道
     * @return 随机码
     */
    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        return code;
    }
}
