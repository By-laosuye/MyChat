package com.laosuye.mychat.common.commm.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 老苏叶
 *
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {


    /**
     * 线程铺货
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in Thread, 线程名：{}",t.getName(),e);
    }
}
