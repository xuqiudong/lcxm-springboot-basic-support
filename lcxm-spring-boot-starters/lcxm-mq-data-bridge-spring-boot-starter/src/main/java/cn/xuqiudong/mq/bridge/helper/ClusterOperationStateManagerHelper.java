package cn.xuqiudong.mq.bridge.helper;

import cn.xuqiudong.mq.bridge.enums.OperationEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述:
 * 同步操作辅助类: 保证消费消息和发送消息 集群下串行化处理
 *
 * @author Vic.xu
 * @since 2025-03-21 8:55
 */
@Component
public class ClusterOperationStateManagerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterOperationStateManagerHelper.class);

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 本地状态：消费/发送
     */
    private static final Map<OperationEnum, AtomicBoolean> LOCAL_STATE_MAP = new ConcurrentHashMap<>();

    static {
        // 初始化本地状态
        for (OperationEnum operation : OperationEnum.values()) {
            LOCAL_STATE_MAP.put(operation, new AtomicBoolean(false));
        }
    }

    /**
     * 检查本地状态和 Redis 状态
     *
     * @param operation 操作标识
     * @return 是否可以执行操作
     */
    public boolean canStartOperation(OperationEnum operation) {
        // 检查本地状态
        if (LOCAL_STATE_MAP.get(operation).get()) {
            return false;
        }
        // 检查 Redis 状态
        return Boolean.TRUE.equals(redisTemplate.opsForValue().
                setIfAbsent(operation.getRedisStateKey(), "true", 2, TimeUnit.MINUTES));
    }

    /**
     * 刷新 Redis 状态 TTL
     */
    public void refreshRedisState(OperationEnum operation) {
        try {
            redisTemplate.opsForValue().set(operation.getRedisStateKey(), "true", 2, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.warn("刷新Redis State TTL 失败", e);
        }

    }

    /**
     * 获取分布式锁: 不等待，获取不到立即返回 false，获取到锁则持有指定时间
     *
     * @param operation 操作标识
     * @param leaseTime 锁的持有时间
     * @param timeUnit  时间单位
     * @return 是否获取锁成功
     */
    public RLock tryLock(OperationEnum operation, long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(operation.getRedisLockKey());
        try {
            boolean b = lock.tryLock(0, leaseTime, timeUnit);
            return b ? lock : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 使用 redisson的看门狗机制获取分布式锁，防止任务长时间运行
     * 而不再使用 tryLock
     * @see #tryLock(OperationEnum, long, TimeUnit) tryLock
     */
    public RLock lockWithWatchDog(OperationEnum operation){
        RLock lock = redissonClient.getLock(operation.getRedisLockKey());
        try {
            lock.lock();
            return lock;
        } catch (Exception e) {
            LOGGER.error("获取分布式锁失败",  e);
            return null;
        }

    }

    /**
     * 释放分布式锁
     *
     * @param operation 操作标识
     */
    public void unlock(OperationEnum operation) {
        RLock lock = redissonClient.getLock(operation.getRedisLockKey());
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 释放一个具体的锁
     */
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        } else {
            LOGGER.warn("锁已经不在当前线程，可能已经过期");
        }
    }

    /**
     * 更新本地状态
     *
     * @param operation 操作标识
     * @param isStart   是否开始
     */
    public void updateLocalState(OperationEnum operation, boolean isStart) {
        LOCAL_STATE_MAP.get(operation).set(isStart);
    }

    /**
     * 清除 Redis 状态
     *
     * @param operation 操作标识
     */
    public void clearRedisState(OperationEnum operation) {
        try {
            redisTemplate.delete(operation.getRedisStateKey());
        } catch (Exception e) {
            LOGGER.error("清除 Redis 状态失败: {}", e.getMessage(), e);
        }
    }

}