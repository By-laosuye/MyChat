package com.laosuye.mychat.common.commm.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("错误",t.getName(),e);
    }
}
