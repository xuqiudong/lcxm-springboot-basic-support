package cn.xuqiudong.basic.secureid.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * 标注controller中的某个方法 需要对 响应值中的id进行加密
 * 会被 SkipIdSecure 跳过
 *
 * @author Vic.xu
 * @since 2026-08-20 17:06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSecureId {
}
