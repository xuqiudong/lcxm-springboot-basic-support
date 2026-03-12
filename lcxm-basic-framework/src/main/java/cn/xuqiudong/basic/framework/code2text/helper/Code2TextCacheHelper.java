package cn.xuqiudong.basic.framework.code2text.helper;

import cn.xuqiudong.basic.framework.code2text.annotation.Code2Text;
import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextCacheEvictEvent;
import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextSpringEvictListener;
import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextRedisEvictListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

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

    public static void evict(Class<? extends Annotation> annoType, String code) {
        publish(annoType, code);
    }

    /**
     * 清除所有
     */
    public static void evictAll(Class<? extends Annotation> annoType) {
        publish(annoType, null);
    }

    /**
     *  清除缓存
     * @see Code2TextSpringEvictListener
     * @see Code2TextRedisEvictListener
     * @param code 为null 表示清除所有
     */
    private static void publish(Class<? extends Annotation> annoType, String code) {
        Assert.isTrue(annoType.isAnnotationPresent(Code2Text.class), annoType.getName() + " is not a Code2Text annotation");
        if (publisher == null) {
            LOGGER.error("Code2Text not initialized");
            return;
        }
        publisher.publishEvent(new Code2TextCacheEvictEvent(annoType.getSimpleName(), code));
    }
}
