package cn.xuqiudong.basic.framework.code2text.support;

import cn.xuqiudong.basic.framework.code2text.annotation.Code2Text;
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
 *
 * @author Vic.xu
 * @since 2026-01-12 10:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = Code2TextSerializer.class)
@Code2Text
public @interface DemoCode2Text {

    @AliasFor(annotation = Code2Text.class, attribute = "textKey")
    String textKey() default "";

    @AliasFor(annotation = Code2Text.class, attribute = "suffix")
    String suffix() default "Demo";

    @AliasFor(annotation = Code2Text.class, attribute = "fallbackToRaw")
    boolean fallbackToRaw() default true;
}
