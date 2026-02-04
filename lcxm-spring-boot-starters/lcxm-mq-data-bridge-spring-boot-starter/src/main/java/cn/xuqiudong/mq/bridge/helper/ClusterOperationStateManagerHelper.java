package cn.xuqiudong.mq.bridge.helper;

import cn.xuqiudong.mq.bridge.enums.OperationEnum;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /**
     * 本地状态：消费/发送
     */
//    private static final Map<OperationEnum, AtomicBoolean> LOCAL_STATE_MAP = new ConcurrentHashMap<>();


    /**
     * 使用 redisson的看门狗机制获取分布式锁，防止任务长时间运行
     * 而不再使用 tryLock
     */
    public RLock lockWithWatchDog(OperationEnum operation) {
        RLock lock = redissonClient.getLock(operation.getRedisLockKey());
        try {
            boolean locked = lock.tryLock();
            return locked ? lock : null;
        } catch (Exception e) {
            LOGGER.error("获取分布式锁失败", e);
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
}