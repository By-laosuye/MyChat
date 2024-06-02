package com.laosuye.mychat.common.commm.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.laosuye.mychat.common.commm.domain.dto.RequestInfo;
import com.laosuye.mychat.common.commm.exception.HttpErrorEnum;
import com.laosuye.mychat.common.commm.util.RequestHolder;
import com.laosuye.mychat.common.user.domain.enums.BlackTypeEnum;
import com.laosuye.mychat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


/**
 * 黑名单拦截器
 */
@Component
public class BlackInterceptor implements HandlerInterceptor {

    @Autowired
    private UserCache userCache;

    /**
     * 在处理请求之前进行拦截，检查当前请求是否在黑名单中。
     * 如果请求的用户ID或IP在黑名单中，则拒绝访问并返回相应的错误信息。
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  处理请求的对象
     * @return 如果请求不在黑名单中，返回true，否则返回false
     * @throws Exception 如果处理过程中发生异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从用户缓存中获取黑名单映射
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        // 获取当前请求的信息
        RequestInfo requestInfo = RequestHolder.get();

        // 检查用户ID是否在黑名单中，如果是，则拒绝访问
        if (isBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()))) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        // 检查IP地址是否在黑名单中，如果是，则拒绝访问
        if (isBlackList(requestInfo.getIp(), blackMap.get(BlackTypeEnum.IP.getType()))) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }

        // 如果请求的用户ID和IP都不在黑名单中，允许访问
        return true;
    }


    /**
     * 检查对象是否在黑名单中。
     *
     * 此方法用于判断给定的对象是否存在于一个字符串集合（通常是一个黑名单）中。
     * 如果对象为null或者集合为空，则认为对象不在黑名单中。
     * 注意，对象是通过调用其toString方法的值来与集合中的字符串进行比较的。
     *
     * @param target 要检查的对象，可以是任何类型。
     * @param set 用于检查的字符串集合，通常是一个黑名单。
     * @return 如果对象在集合中，则返回true；否则返回false。
     */
    private boolean isBlackList(Object target, Set<String> set) {
        // 检查目标对象或集合是否为空，如果为空，则对象不在黑名单中
        if (Objects.isNull(target) || CollectionUtil.isEmpty(set)) {
            return false;
        }
        // 通过将目标对象转换为字符串并检查集合中是否包含该字符串来判断对象是否在黑名单中
        return set.contains(target.toString());
    }


}
