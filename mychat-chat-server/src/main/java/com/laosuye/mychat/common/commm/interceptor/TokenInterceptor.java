package com.laosuye.mychat.common.commm.interceptor;

import com.laosuye.mychat.common.commm.exception.HttpErrorEnum;
import com.laosuye.mychat.common.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

/**
 * Token拦截器
 * @author laosuye
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    /**
     * 请求头
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";
    /**
     * 请求头schema
     */
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String UID = "uid";

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)){
            //用户有登录状态
            request.setAttribute(UID,validUid);
        }else {
            //用户未登录
            boolean isPublicUri = isPublicUri(request);
            if (isPublicUri){
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }

        return true;
    }

    /**
     * 判断是否是public接口
     * @param request 请求
     * @return true/false
     */
    private boolean isPublicUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String[] split = requestUri.split("/");
        return split.length > 2 && "public".equals(split[3]);
    }

    /**
     * 获取token
     * @param request 请求
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(token)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replace(AUTHORIZATION_SCHEMA,""))
                .orElse(null);
    }
}
