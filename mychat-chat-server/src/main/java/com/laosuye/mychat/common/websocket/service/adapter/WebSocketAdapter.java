package com.laosuye.mychat.common.websocket.service.adapter;

import com.laosuye.mychat.common.commm.domain.enums.YesOrNoEnum;
import com.laosuye.mychat.common.user.domain.entity.User;
import com.laosuye.mychat.common.websocket.domain.enums.WSRespTypeEnum;
import com.laosuye.mychat.common.websocket.domain.vo.resp.WSBaseResp;
import com.laosuye.mychat.common.websocket.domain.vo.resp.WSBlack;
import com.laosuye.mychat.common.websocket.domain.vo.resp.WSLoginSuccess;
import com.laosuye.mychat.common.websocket.domain.vo.resp.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WebSocketAdapter {

    /**
     * 构建登录二维码的响应消息。
     *
     * 该方法用于根据微信小程序二维码票据（WxMpQrCodeTicket）生成一个包含登录URL的响应消息。
     * 主要用于在用户需要登录时，通过扫描二维码的方式进行登录，响应消息中包含的登录URL是用户完成登录的关键。
     *
     * @param wxMpQrCodeTicket 微信小程序二维码票据，包含了生成二维码的相关信息。
     * @return 包含登录URL的响应消息对象。
     */
    public static WSBaseResp<?> buildResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        // 创建一个空的响应消息对象
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        // 设置响应消息的类型为登录URL
        resp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        // 创建一个登录URL对象，并设置为响应消息的数据部分
        resp.setData(new WSLoginUrl(wxMpQrCodeTicket.getUrl()));
        // 返回设置好的响应消息
        return resp;
    }


    /**
     * 构建登录成功的响应对象。
     *
     * @param user 用户信息，包含用户名、头像等。
     * @param token 用户登录成功的令牌，用于后续请求验证身份。
     * @param power 用户是否具有管理员权限，用于区分普通用户和管理员。
     * @return 包含登录成功信息的响应对象。
     */
    public static WSBaseResp<?> buildResp(User user, String token, boolean power) {
        // 初始化响应对象，设置响应类型为登录成功。
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());

        // 构建登录成功详细信息对象，包括用户头像、姓名、令牌、用户ID和权限信息。
        WSLoginSuccess loginSuccess = WSLoginSuccess.builder().avatar(user.getAvatar())
                .name(user.getName())
                .token(token)
                .uid(user.getId())
                .power(power ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                .build();

        // 将登录成功详细信息设置到响应对象中。
        resp.setData(loginSuccess);

        // 返回构建完成的响应对象。
        return resp;
    }

    /**
     * 构建一个等待授权的响应对象。
     * <p>
     * 该方法用于生成一个特定类型的响应对象，表示用户登录扫描成功的状态，但还需要进一步的授权操作。
     * 返回的响应对象中不包含具体的数据，而是通过类型标识来告知调用方当前的状态。
     *
     * @return WSBaseResp<?> 返回一个泛型的响应对象，其中<?>表示响应的具体数据类型取决于调用方的处理。
     */
    public static WSBaseResp<?> buildWaitAuthorizeResp() {
        // 创建一个空的响应对象
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        // 设置响应的类型为登录扫描成功
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        // 返回设置好的响应对象
        return resp;
    }

    /**
     * 构建一个表示令牌无效的响应对象。
     *
     * 此方法用于生成一个特定类型的响应对象，该对象用于表示令牌（如登录令牌）无效的情况。这可能发生在用户登录信息过期或令牌被篡改的情况下。
     * 生成的响应对象包含一个特定类型的错误代码，用于标识令牌无效的问题。
     *
     * @return WSBaseResp<?> 一个封装了错误类型的响应对象，其中 ? 表示泛型类型，可根据实际需要进行具体化。
     */
    public static WSBaseResp<?> buildInvalidTokenResp() {
        // 创建一个空的响应对象
        WSBaseResp<WSLoginUrl> resp = new WSBaseResp<>();
        // 设置响应类型的错误代码，表示令牌无效
        resp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        // 返回设置好错误类型的响应对象
        return resp;
    }


    /**
     * 构建一个将用户加入黑名单的响应对象。
     * <p>
     * 该方法用于生成一个特定类型的响应对象，表示将某个用户添加到黑名单的操作结果。响应对象中包含了操作类型和具体的数据信息。
     *
     * @param user 被添加到黑名单的用户对象，需要从中获取用户ID。
     * @return 返回一个包含操作类型和黑名单数据的响应对象。
     */
    public static WSBaseResp<?> buildBlack(User user) {
        // 创建一个空的响应对象，用于后续填充数据。
        WSBaseResp<WSBlack> resp = new WSBaseResp<>();
        // 设置响应对象的操作类型为黑名单类型。
        resp.setType(WSRespTypeEnum.BLACK.getType());
        // 构建黑名单数据对象，包含用户的ID信息。
        WSBlack build = WSBlack
                .builder()
                .uid(user.getId())
                .build();
        // 将构建的黑名单数据对象设置到响应对象中。
        resp.setData(build);
        // 返回填充完毕的响应对象。
        return resp;
    }
}
