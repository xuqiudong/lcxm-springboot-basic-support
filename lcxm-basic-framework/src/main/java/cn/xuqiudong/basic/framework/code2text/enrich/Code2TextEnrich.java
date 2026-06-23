package cn.xuqiudong.basic.framework.code2text.enrich;

import cn.xuqiudong.basic.framework.code2text.type.Code2TextType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * 该注解直接填充后端的字段，
 *
 * @author Vic.xu
 * @see cn.xuqiudong.basic.framework.code2text.annotation.Code2Text
 * @since 2026-06-22 17:48
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Code2TextEnrich {

    /**
     * 编码转文本的类型, 通过它查找解析器 Code2TextResolver
     */
    Class<? extends Code2TextType> type();

    /**
     * 当前字段的值值来自哪个字段
     * eg: from="code"
     * eg: from="categoryId"
     */
    String from();

    /**
     * 未找到文本时是否返回原值（如userId=9999 → 文本字段显示9999）
     * true：返回原值；false：返回null
     */
    boolean fallbackToRaw() default true;

}
