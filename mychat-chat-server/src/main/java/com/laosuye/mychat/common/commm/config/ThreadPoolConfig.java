package com.laosuye.mychat.common.commm.config;

import com.laosuye.mychat.common.commm.thread.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {
    /**
     * 项目共用线程池
     */
    public static final String MYCHAT_EXECUTOR = "mychatExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";

    /**
     * 项目中使用了@Asyn注解就会返回我们自定义的线程池
     * @return 线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        return myChatExecutor();
    }

    @Bean(MYCHAT_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor myChatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //线程池优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //核心线程
        executor.setCorePoolSize(10);
        //最大线程
        executor.setMaxPoolSize(10);
        //等待队列
        executor.setQueueCapacity(200);
        //线程前缀
        executor.setThreadNamePrefix("mychat-executor-");
        //满了调用线程执行，认为重要任务已经完成
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }


    @Bean(WS_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor websocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setWaitForTasksToCompleteOnShutdown(true); //线程池优雅停机
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("websocket-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());//满了丢弃
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }

}