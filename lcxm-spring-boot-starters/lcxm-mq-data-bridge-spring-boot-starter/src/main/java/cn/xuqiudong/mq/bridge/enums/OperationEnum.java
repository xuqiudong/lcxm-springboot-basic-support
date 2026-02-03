package cn.xuqiudong.mq.bridge.enums;

import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;

/**
 * 描述:
 * 操作类型： 消费或者发送消息
 *
 * @author Vic.xu
 * @since 2025-03-21 9:32
 */

/**
 * 描述:
 * 操作类型： 消费或者发送消息
 *
 * @author Vic.xu
 * @since 2025-03-21 9:32
 */
public enum OperationEnum {

    CONSUME("consume", "消费"),
    SEND("send", "发送"),
    ;
    // 操作键（用于 Redis 键）
    private final String operationKey;
    // 操作描述
    private final String description;

    OperationEnum(String operationKey, String description) {
        this.operationKey = operationKey;
        this.description = description;
    }

    /**
     * 获取存于 Redis 的当前操作是否进行中的 key
     *
     * @return string 形如：data:bridge:send:running
     */
    public String getRedisStateKey() {
        return DataBridgeConstant.REDIS_KEY_PREFIX + this.operationKey + ":running";
    }

    /**
     * 获取 Redis 锁的 key
     *
     * @return string 形如：data:bridge:send:lock
     */
    public String getRedisLockKey() {
        return DataBridgeConstant.REDIS_KEY_PREFIX + this.operationKey + ":lock";
    }

    public String getOperationKey() {
        return operationKey;
    }

    public String getDescription() {
        return description;
    }
}