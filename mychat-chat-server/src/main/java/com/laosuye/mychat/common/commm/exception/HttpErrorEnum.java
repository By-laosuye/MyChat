package com.laosuye.mychat.common.commm.exception;

import cn.hutool.http.ContentType;
import com.google.common.base.Charsets;
import com.laosuye.mychat.common.commm.domain.vo.resp.ApiResult;
import com.laosuye.mychat.common.commm.util.JsonUtils;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**http请求错误枚举
 * @author 老苏叶
 */

@AllArgsConstructor
public enum HttpErrorEnum {

    ACCESS_DENIED(401,"登录失效请重新登录");

    /**
     * 请求code
     */
    private final Integer httpCode;
    /**
     * 描述
     */
    private final String desc;

    /**
     * 发送http错误
     * @param response response
     * @throws IOException exception
     */
    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(httpCode);
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        response.getWriter().write(JsonUtils.toStr(ApiResult.fail(httpCode,desc)));
    }
}
