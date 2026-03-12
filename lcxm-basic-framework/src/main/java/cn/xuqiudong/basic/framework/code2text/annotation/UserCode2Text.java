package cn.xuqiudong.basic.framework.code2text.annotation;

import cn.xuqiudong.basic.framework.code2text.cache.Code2TextPreloadable;
import cn.xuqiudong.basic.framework.code2text.core.Code2TextSerializer;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 *  用户id（名）转文本 核心注解
 * 框架默认提供此注解， 由具体的项目自身实现 Code2TextResolver 或Code2TextPreloadable
 * @see Code2TextPreloadable
 * @see Code2TextResolver
 * @author Vic.xu
 * @since 2026-02-09 16:42
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
// 标记为Jackson内部注解，确保序列化器生效
@JacksonAnnotationsInside
// 绑定框架核心序列化器
@JsonSerialize(using = Code2TextSerializer.class)
@Code2Text
public @interface UserCode2Text {

    @AliasFor(annotation = Code2Text.class, attribute = "textKey")
    String textKey() default "";

    @AliasFor(annotation = Code2Text.class, attribute = "suffix")
    String suffix() default "Name";

    @AliasFor(annotation = Code2Text.class, attribute = "fallbackToRaw")
    boolean fallbackToRaw() default true;
}
