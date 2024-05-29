package com.laosuye.mychat.common.commm.util;

import com.laosuye.mychat.common.commm.domain.dto.RequestInfo;

/**
 * 请求上下文
 * @author laosuye
 */
public class RequestHolder {

    /**
     * 线程本地变量
     */
    private static final ThreadLocal<RequestInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo) {
        THREAD_LOCAL.set(requestInfo);
    }

    public static RequestInfo get(){
        return THREAD_LOCAL.get();
    }

    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
