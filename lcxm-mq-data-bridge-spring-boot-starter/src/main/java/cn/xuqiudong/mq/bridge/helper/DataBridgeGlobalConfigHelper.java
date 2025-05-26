package cn.xuqiudong.mq.bridge.helper;

import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.vo.DataBridgeEnableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static cn.xuqiudong.mq.bridge.constant.DataBridgeConstant.*;

/**
 * 描述: data bridge
 *  mq开关 状态工具类： 先初始化相关开关状态到redis中，并在后续设置和获取
 *   <ul>
 *    <li>1.1 启用/禁用 mq</li>
 *    <li>1.2 启用/禁用 消息发送</li>
 *    <li>1.3 启用/禁用 消息接收</li>
 *    <li>1.4 启用/禁用 消息消费</li>
 *  </ul>
 *
 * @author Vic.xu
 * @since 2025-02-27 10:43
 */
@Component
public class DataBridgeGlobalConfigHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeGlobalConfigHelper.class);


    private final RedisTemplate<String, String> redisTemplate;

    private final DataBridgeProperties properties;

    public DataBridgeGlobalConfigHelper(RedisTemplate<String, String> redisTemplate, DataBridgeProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    /**
     * 初始化全局开关状态
     */
    @PostConstruct
    public void init() {
        LOGGER.info("init data bridge global switch: mq enable={}, send enable = {}, receive enable = {}, consumer enbale = {}",
                properties.isMqEnabled(), properties.isSendEnabled(), properties.isReceiveEnabled(), properties.isConsumeEnabled());
        redisTemplate.opsForValue().set(REDIS_KEY_MQ_ENABLE, String.valueOf(properties.isMqEnabled()));
        redisTemplate.opsForValue().set(REDIS_KEY_RECEIVE_ENABLE, String.valueOf(properties.isReceiveEnabled()));
        redisTemplate.opsForValue().set(REDIS_KEY_SEND_ENABLE, String.valueOf(properties.isSendEnabled()));
        redisTemplate.opsForValue().set(REDIS_KEY_CONSUMER_ENABLE, String.valueOf(properties.isConsumeEnabled()));

    }


    /**
     * 是否启用MQ
     */
    public boolean isMqEnable() {
        return isEnableInRedis(REDIS_KEY_MQ_ENABLE);
    }

    /**
     * 是否启用发送
     */
    public boolean isSendEnable() {
        return isEnableInRedis(REDIS_KEY_SEND_ENABLE);
    }

    /**
     * 是否启用接收
     */
    public boolean isReceiveEnable() {
        return isEnableInRedis(REDIS_KEY_RECEIVE_ENABLE);
    }

    /**
     * 是否启用 消费 本地消息
     */
    public boolean isConsumerEnable() {
        return isEnableInRedis(REDIS_KEY_CONSUMER_ENABLE);
    }

    /**
     * 设置mq开关
     */
    public void setMqEnable(boolean enabled) {
        setEnableInRedis(REDIS_KEY_MQ_ENABLE, enabled);
    }

    /**
     * 设置发送mq 开关
     */
    public void setSendEnable(boolean enabled) {
        setEnableInRedis(REDIS_KEY_SEND_ENABLE, enabled);
    }

    /**
     * 设置接收mq 开关
     */
    public void setReceiveEnable(boolean enabled) {
        setEnableInRedis(REDIS_KEY_RECEIVE_ENABLE, enabled);
    }

    /**
     * 设置 消费 本地消息 开关
     */
    public void setConsumerEnable(boolean enabled) {
        setEnableInRedis(REDIS_KEY_CONSUMER_ENABLE, enabled);
    }

    /**
     * 获取全部开关状态
     */
    public DataBridgeEnableVO allStatus() {
        return new DataBridgeEnableVO(isReceiveEnable(), isMqEnable(), isSendEnable(), isConsumerEnable());
    }

    /**
     * 判断开关是否开启
     */
    public boolean isEnableInRedis(String key) {
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(key));
    }

    /**
     * 设置开关
     */
    public void setEnableInRedis(String key, boolean enabled) {
        redisTemplate.opsForValue().set(key, String.valueOf(enabled));
    }

    /**
     *  是否可以发送： mq开关 && 发送开关
     */
    public boolean couldSend() {
        boolean enabled = isMqEnable() && isSendEnable();
        if (!enabled) {
            LOGGER.info("全局阻塞或MQ未启用，跳过发送逻辑");
        }
        return enabled;
    }
    /**
     *  是否可以接收： mq开关 && 接收开关
     */
    public boolean couldReceive() {
        boolean enabled = isMqEnable() && isReceiveEnable();
        if (!enabled) {
            LOGGER.info("全局阻塞或者MQ未启用，不接受消息");
        }
        return enabled;
    }


}
