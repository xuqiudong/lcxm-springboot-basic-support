package cn.xuqiudong.basic.quartz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 * 标注一个方法是一个定时任务函数，
 * 此方法只能包含一个 JobHistory 参数
 * 且只可标注在public方法上;
 * 建议其他非定时任务函数定义为非public
 *
 * @author Vic.xu
 * @since 2025-01-17 14:59
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskJobFlag {

    /**
     * 任务唯一表示 code
     */
    String value();

    /**
     * 任务描述
     */
    String text() default "";

}