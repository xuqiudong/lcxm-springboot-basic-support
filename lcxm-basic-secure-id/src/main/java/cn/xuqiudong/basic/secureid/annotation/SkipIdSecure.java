package cn.xuqiudong.basic.secureid.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * 某个切面方法是否跳过id加密， 一般是controller中的某个request mapping
 * @author Vic.xu
 * @since 2026-08-20 15:26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipIdSecure {

    /**
     * 是否跳过id加密
     */
    boolean value() default true;
}
