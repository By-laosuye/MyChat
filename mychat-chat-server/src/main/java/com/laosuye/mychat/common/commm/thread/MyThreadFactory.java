package com.laosuye.mychat.common.commm.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * 自定义线程工厂
 * @author 老苏叶
 */
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {

    private ThreadFactory original;

    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();


    @Override
    public Thread newThread(Runnable r) {
        //执行spring线程自己创建的逻辑
        Thread thread = original.newThread(r);
        //额外装饰我们自己要的逻辑
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
