package com.laosuye.mychat.common.websocket.service;

import com.laosuye.mychat.common.websocket.domain.vo.resp.WSBaseResp;
import io.netty.channel.Channel;

public interface WebSocketService {

    /**
     * 保存channel到map中
     * @param channel
     */
    void connect(Channel channel);

    /**
     * 请求登陆二维码
     * @param channel
     */
    void handleLoginReq(Channel channel);

    /**
     * 断开连接
     * @param channel
     */
    void remove(Channel channel);

    /**
     * 扫码成功给channel推送消息
     * @param code 登陆码
     * @param id 用户id
     */
    void scanLoginSuccess(Integer code, Long id);


    /**
     * 发送待授权的消息
     * @param code 登陆码
     */
    void waitAuthorize(Integer code);

    /**
     * 用户扫码登陆
     * @param channel channel
     * @param token token
     */
    void authorize(Channel channel, String token);

    void sendMsgToAll(WSBaseResp<?> msg);
}
