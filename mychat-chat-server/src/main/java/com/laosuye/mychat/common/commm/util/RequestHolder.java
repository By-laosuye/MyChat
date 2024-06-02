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

    /**
     * 将请求信息设置到线程局部变量中。
     *
     * 本方法用于将包含当前请求相关的信息的RequestInfo对象存储到线程局部变量中。
     * 这样做的目的是为了在同一个线程中随时可以访问到这个请求的信息，而不需要传递请求对象。
     * 对于多线程应用程序来说，这种方式可以有效地隔离不同线程之间的请求信息，确保线程安全。
     *
     * @param requestInfo 包含当前请求相关信息的对象。
     */
    public static void set(RequestInfo requestInfo) {
        THREAD_LOCAL.set(requestInfo);
    }

    /**
     * 从线程局部变量中获取RequestInfo对象。
     *
     * @return 返回当前线程绑定的RequestInfo对象，如果没有绑定，则返回null。
     */
    public static RequestInfo get() {
        // 从线程局部变量中获取RequestInfo实例
        return THREAD_LOCAL.get();
    }

    /**
     * 移除线程局部变量的当前值。
     *
     * 该方法的作用是清除当前线程中线程局部变量的值。线程局部变量是一种只能被同一个线程访问和修改的变量，
     * 它提供了一种隔离不同线程之间变量访问的方式。在某些情况下，需要主动清除线程局部变量，以避免它们
     * 持续占用资源或导致潜在的线程安全问题。
     *
     * @see java.lang.ThreadLocal#remove()
     */
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
