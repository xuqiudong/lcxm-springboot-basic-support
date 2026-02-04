package cn.xuqiudong.mq.bridge.facade;

import cn.xuqiudong.mq.bridge.enums.OperationEnum;
import cn.xuqiudong.mq.bridge.helper.ClusterOperationStateManagerHelper;
import cn.xuqiudong.mq.bridge.helper.DataBridgeGlobalConfigHelper;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-03-21 10:37
 */
public abstract class AbstractDataBridgeMessageFacade {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected final DataBridgeGlobalConfigHelper dataBridgeGlobalSwitchHelper;

    protected final ClusterOperationStateManagerHelper stateManager;

    public AbstractDataBridgeMessageFacade(DataBridgeGlobalConfigHelper dataBridgeGlobalSwitchHelper, ClusterOperationStateManagerHelper stateManager) {
        this.dataBridgeGlobalSwitchHelper = dataBridgeGlobalSwitchHelper;
        this.stateManager = stateManager;
    }


    /**
     * 操作前的前置判断：
     * 1. 本地状态和 Redis 状态
     * 2. 本地消息消费开关
     * 3. 分布式锁
     * @param operation 操作标识
     * @param switchKey 消费开关的key
     * @return 是否可以启动
     * @changelog  by Vic.xu 20260204 删除这些复杂的处理 可能造成不可知的问题，  使用简单错误的redisson锁即可
     */
    protected RLock  beforeOperation(OperationEnum operation, String switchKey) {
        // 检查本地状态和 Redis 状态  删除这些复杂的处理 可能造成不可知的问题，  使用简单错误的redisson锁即可
//        if (!stateManager.canStartOperation(operation)) {
//            LOGGER.warn("消息{}已启动，请勿重复启动", operation.getDescription());
//            return null;
//        }
        // 本地消息消费开关
        if (!dataBridgeGlobalSwitchHelper.isEnableInRedis(switchKey)) {
            LOGGER.warn("本地消息{}[{}]开关未打开，跳过处理", operation.getDescription(), switchKey);
            return null;
        }
        RLock lock = stateManager.lockWithWatchDog(operation);
        // 获取分布式锁
        if (lock == null) {
            LOGGER.warn("获取分布式锁失败，跳过本次{}", operation.getDescription());
            return null;
        }
        return lock;
    }

    /**
     * 操作后的后置处理： 保证和before 相反的顺序
     * 1. 释放分布式锁
     * 2. 清除redis状态
     * 3. 更新本地状态
     * @param operation 操作标识
     * @changelog  by Vic.xu 20260204 删除这些复杂的处理 可能造成不可知的问题，  使用简单错误的redisson锁即可
     */
    protected void afterOperation(OperationEnum operation, RLock lock) {
//            stateManager.clearRedisState(operation);
//            stateManager.updateLocalState(operation, false);
            stateManager.unlock(lock);

    }

}
