package cn.xuqiudong.basic.secureid.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * 某个字段是否需要加密 对应model中的一个外键id, 如 userId,projectId
 *
 * @author Vic.xu
 * @since 2026-08-20 15:28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecureId {

    /**
     * 是否需要加密 , 超过 65 长度则不加密
     */
    boolean value() default true;
}
