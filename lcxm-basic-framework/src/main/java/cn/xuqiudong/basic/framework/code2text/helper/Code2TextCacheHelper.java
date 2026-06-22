package cn.xuqiudong.basic.framework.code2text.helper;

import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextCacheEvictEvent;
import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextRedisEvictListener;
import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextSpringEvictListener;
import cn.xuqiudong.basic.framework.code2text.type.Code2TextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

/**
 * 描述:
 *   清除缓存，通过发布spring事件触发
 *   1. spring监听事件： 删除本地和redis
 *   2. 发布redis监听事件， 其他节点监听 到后删除本地缓存
 * @see
 * @author Vic.xu
 * @since 2026-01-15 19:05
 */
public class Code2TextCacheHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Code2TextCacheHelper.class);

    private static ApplicationEventPublisher publisher;

    public static void setPublisher(ApplicationEventPublisher p) {
        publisher = p;
    }

    public static void evict(Class<? extends Code2TextType> typeClass, String code) {
        publish(typeClass, code);
    }

    /**
     * 清除所有
     */
    public static void evictAll(Class<? extends Code2TextType> typeClass) {
        publish(typeClass, null);
    }

    /**
     *  清除缓存
     * @see Code2TextSpringEvictListener
     * @see Code2TextRedisEvictListener
     * @param code 为null 表示清除所有
     */
    private static void publish(Class<? extends Code2TextType> typeClass, String code) {
        Assert.isTrue(Code2TextType.class.isAssignableFrom(typeClass), typeClass.getName() + " is not a Code2TextType class");
        if (publisher == null) {
            LOGGER.error("Code2Text not initialized");
            return;
        }
        publisher.publishEvent(new Code2TextCacheEvictEvent(typeClass.getSimpleName(), code));
    }
}
