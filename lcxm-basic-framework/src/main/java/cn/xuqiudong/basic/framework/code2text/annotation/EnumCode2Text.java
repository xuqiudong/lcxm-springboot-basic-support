package cn.xuqiudong.basic.framework.code2text.annotation;

import cn.xuqiudong.basic.framework.code2text.core.Code2TextSerializer;
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
 * 枚举编码转文本专属注解
 * * 继承基注解，自动填充type="enum"，适配所有实现EnumSelectable接口的枚举
 * * 无注解时框架也会自动转换，加注解可自定义文本字段名/兜底策略
 *
 * @author Vic.xu
 * @since 2026-01-09 11:43
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
// 标记为Jackson内部注解，确保序列化器生效
@JacksonAnnotationsInside
// 绑定框架核心序列化器
@JsonSerialize(using = Code2TextSerializer.class)
@Code2Text
public @interface EnumCode2Text {

    @AliasFor(annotation = Code2Text.class, attribute = "textKey")
    String textKey() default "";

    @AliasFor(annotation = Code2Text.class, attribute = "suffix")
    String suffix() default "Text";

    @AliasFor(annotation = Code2Text.class, attribute = "fallbackToRaw")
    boolean fallbackToRaw() default true;
}