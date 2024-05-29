package com.laosuye.mychat.common.commm.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.laosuye.mychat.common.commm.domain.dto.RequestInfo;
import com.laosuye.mychat.common.commm.util.RequestHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 收集信息的拦截器
 * @author 老苏叶
 */
@Component
public class CollectorInterceptor implements HandlerInterceptor {

    /**
     * 拦截器前置处理
     * @param request 请求
     * @param response 响应
     * @param handler 拦截
     * @return 是否通行
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long uid = Optional.ofNullable(request.getAttribute(TokenInterceptor.UID))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUid(uid);
        requestInfo.setIp(ServletUtil.getClientIP(request));
        RequestHolder.set(requestInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
