package cn.xuqiudong.common.base.code2text.resolver;

import java.lang.annotation.Annotation;

/**
 * 描述:
 * 处理code 和text 的转换 的核心接口， 每个 Code2TextResolver 对应一个 Code2Text 的子注解
 * 如果支持预加载缓存数据，可实现  Code2TextPreloadable
 *
 * @author Vic.xu
 * @see cn.xuqiudong.common.base.code2text.cache.Code2TextPreloadable
 * @since 2026-01-09 16:43
 */
public interface Code2TextResolver<A extends Annotation> {

    Class<A> annotationType();

    String codeToText(Object code);

    Object textToCode(String text);

}
