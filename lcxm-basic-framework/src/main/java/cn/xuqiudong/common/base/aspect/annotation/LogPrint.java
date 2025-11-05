package cn.xuqiudong.common.base.aspect.annotation;

import java.lang.annotation.*;

/**
 * 描述: 日志切面标识，放在方法上的
 * @author Vic.xu
 * @since 2022-03-16 9:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface LogPrint {

    /**
     * 是否记录耗时 默认true
     */
    boolean time() default true;

    /**
     * 是否记录入参 默认false
     */
    boolean in() default false;


    /**
     * 是否记录出参 默认false
     */
    boolean out() default false;

}
