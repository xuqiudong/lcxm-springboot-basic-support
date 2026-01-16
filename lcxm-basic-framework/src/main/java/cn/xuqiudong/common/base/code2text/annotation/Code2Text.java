package cn.xuqiudong.common.base.code2text.annotation;

import cn.xuqiudong.common.base.code2text.core.Code2TextSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 *编码转文本核心基注解
 *  * 定义所有编码转文本场景的通用契约，不直接使用，由子注解继承
 *  * 所有的子注解 都需要通过 别名的方式 申明注解中的字段, 强制  ★ textKey、suffix、fallbackToRaw
 *  <code>
 *       @AliasFor(annotation = Code2Text.class, attribute = "textKey")
 *     String textKey() default "";
 *
 *     @AliasFor(annotation = Code2Text.class, attribute = "suffix")
 *     String suffix() default "Text";
 *
 *     @AliasFor(annotation = Code2Text.class, attribute = "fallbackToRaw")
 *     boolean fallbackToRaw() default true;
 *  </code>
 *  * 核心职责：声明“要追加文本字段”的核心规则，不关注实现细节
 *  *
 * @author Vic.xu
 * @since 2026-01-09 11:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
// 标记为Jackson内部注解，确保序列化器生效
@JacksonAnnotationsInside
// 绑定框架核心序列化器
@JsonSerialize(using = Code2TextSerializer.class)
public @interface Code2Text {

    /**
     * 自定义文本字段名（如指定"userName"则追加该字段）
     */
    String textKey() default "";

     /**
     * 未指定时textKey，框架自动拼接：原字段名 + "Text"（如status → statusText）
     */
    String suffix () default "Text";

    /**
     * 兜底策略：未找到文本时是否返回原值（如userId=9999 → 文本字段显示9999）
     * true：返回原值；false：返回null
     */
    boolean fallbackToRaw() default true;

}
