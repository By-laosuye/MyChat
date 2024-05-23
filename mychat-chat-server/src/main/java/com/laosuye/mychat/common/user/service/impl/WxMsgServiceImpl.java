package com.laosuye.mychat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.laosuye.mychat.common.user.dao.UserDao;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.user.service.UserService;
import com.laosuye.mychat.common.user.service.WxMsgService;
import com.laosuye.mychat.common.user.service.adapter.TextBuilder;
import com.laosuye.mychat.common.user.service.adapter.UserAdapter;
import com.laosuye.mychat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WxMsgServiceImpl implements WxMsgService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Value("${wx.mp.callback}")
    private String callback;

    @Autowired
    private WebSocketService webSocketService;

    public static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    /**
     * openId和登陆code的映射关系map
     */
    private static final ConcurrentHashMap<String,Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();

    /**
     * 用户扫码登录成功
     * @param wxMpXmlMessage 微信消息
     * @param wxMpService 微信服务
     * @return
     */
    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage,WxMpService wxMpService) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)){
            return null;
        }
        //根据openId查询用户信息
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        //用户已经注册并授权
        if (registered && authorized){
            //走登录成功逻辑，通过code找到channel并通过channel推送消息
            webSocketService.scanLoginSuccess(code,user.getId());
            return null;
        }
        //用户未注册，就先注册
        if (!registered){
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }
        //推送连接让用户授权
        WAIT_AUTHORIZE_MAP.put(openId,code);
        webSocketService.waitAuthorize(code);
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击登录：<a href=\""+ authorizeUrl + "\">登录</a>",wxMpXmlMessage,wxMpService);
    }

    /**
     * 更新用户信息并用户授权
     * @param userInfo 用户信息
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        //更新用户信息
        if (StrUtil.isBlank(user.getAvatar())){
            fillUserInfo(user.getId(),userInfo);
        }
        //通过code找到用户channel，进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        webSocketService.scanLoginSuccess(code,user.getId());
    }

    /**
     * 更新用户信息
     * @param uid id
     * @param userInfo 用户信息
     */
    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(uid, userInfo);
        userDao.updateById(user);
    }

    /**
     * 获取事件key
     * @param wxMpXmlMessage 微信消息
     * @return key
     */
    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrscene_", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.info("getEventKey error eventKey:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }
}
