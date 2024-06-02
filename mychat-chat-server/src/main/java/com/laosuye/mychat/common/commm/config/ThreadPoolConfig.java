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
 * @author 老苏叶
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


    /**
     * 配置WebSocket任务执行器。
     * 使用@Bean注解将该方法的返回值注册为一个Spring Bean，命名为WS_EXECUTOR。
     * @return ThreadPoolTaskExecutor 实例，用于执行WebSocket相关的任务。
     */
    @Bean(WS_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor websocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 设置在关闭时等待所有任务完成，确保WebSocket连接被优雅地关闭
        // 线程池优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 配置核心线程池大小为16，保证至少有16个线程可用于处理任务
        executor.setCorePoolSize(16);

        // 配置最大线程池大小为16，限制线程池的扩展规模
        executor.setMaxPoolSize(16);

        // 配置任务队列容量为1000，用于存储等待执行的任务
        executor.setQueueCapacity(1000);

        // 设置线程名称前缀，便于识别WebSocket相关的线程
        executor.setThreadNamePrefix("websocket-executor-");

        // 设置拒绝策略为DiscardPolicy，当线程池和队列满时，新任务将被直接拒绝
        // 满了丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        // 使用自定义的线程工厂，确保线程的创建符合特定的需求
        executor.setThreadFactory(new MyThreadFactory(executor));

        // 初始化线程池，使其准备就绪
        executor.initialize();

        return executor;
    }


}