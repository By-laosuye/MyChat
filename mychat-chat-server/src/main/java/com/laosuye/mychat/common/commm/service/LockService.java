package com.laosuye.mychat.common.commm.service;

import com.laosuye.mychat.common.commm.exception.BusinessException;
import com.laosuye.mychat.common.commm.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁服务Redisson
 * @author 老苏叶
 */
@Service
public class LockService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     *
     * 执行带有分布式锁的业务逻辑
     * @param key 锁的key
     * @param waitTime 等待时间
     * @param timeUnit 时间单位
     * @param supplier 业务逻辑
     * @return 返回业务逻辑的结果
     * @param <T> 返回值类型
     */
    @SneakyThrows
    public <T> T executeWithLock(String key, int waitTime, TimeUnit timeUnit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        boolean success = lock.tryLock(waitTime, timeUnit);
        if (!success) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 执行带有分布式锁的业务逻辑
     * @param key 锁的key
     * @param supplier 业务逻辑
     * @return 返回业务逻辑的结果
     * @param <T> 返回值类型
     */
    @SneakyThrows
    public <T> T executeWithLock(String key, Supplier<T> supplier) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, supplier);
    }


    /**
     * 执行带有分布式锁的业务逻辑
     * @param key 锁的key
     * @param runnable 业务逻辑
     * @return  返回业务逻辑的结果
     * @param <T> 返回值类型
     */
    @SneakyThrows
    public <T> T executeWithLock(String key, Runnable runnable) {
        return executeWithLock(key, -1, TimeUnit.MILLISECONDS, () -> {
            runnable.run();
            return null;
        });
    }


    /**
     * 执行带有分布式锁的业务逻辑
     * @param <T> 类型
     */
    @FunctionalInterface
    public interface Supplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }


}
