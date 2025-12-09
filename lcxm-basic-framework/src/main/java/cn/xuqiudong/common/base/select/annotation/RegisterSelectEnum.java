package cn.xuqiudong.common.base.select.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 * 标记枚举需要被注册为下拉选项（供前端访问）, 枚举需要实现 EnumSelectable
 *
 * @author Vic.xu
 * @see cn.xuqiudong.common.base.select.EnumSelectable
 * @see cn.xuqiudong.common.base.model.SelectOption
 * @since 2025-11-13 17:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegisterSelectEnum {

    /**
     * 枚举的唯一标识（前端通过此标识访问，如"user_status"）
     * 默认为枚举的SimpleName
     */
    String value() default "";

    /**
     * 枚举的描述
     */
    String desc() default "";
}
