package cn.xuqiudong.basic.core.code2text.resolver;

import cn.xuqiudong.basic.core.code2text.cache.Code2TextPreloadable;
import cn.xuqiudong.basic.core.code2text.cache.proxy.CachedResolverProxy;

import java.lang.annotation.Annotation;

/**
 * 描述:
 * 处理code 和text 的转换 的核心接口， 每个 Code2TextResolver 对应一个 Code2Text 的子注解
 * 如果支持预加载缓存数据，可实现  Code2TextPreloadable
 *
 * @author Vic.xu
 * @see Code2TextPreloadable
 * @since 2026-01-09 16:43
 */
public interface Code2TextResolver<A extends Annotation> {

    Class<A> annotationType();

    String codeToText(Object code);

    Object textToCode(String text);

    /**
     * 是否需要缓存，默认开启缓存, 开启的话，则会将Resolver包装为CachedResolverProxy
     * @see CachedResolverProxy
     */
    default boolean needCache() {
        return true;
    }


    /**
     * 是否支持多个code转换成text 或 多个text转换成code
     */
    default boolean supportMultiValue() {
        return true;
    }

    /**
     * 多个code转换成text时，code之间的分隔符
     */
    default String getSeparator() {
        return ",";
    }
}
