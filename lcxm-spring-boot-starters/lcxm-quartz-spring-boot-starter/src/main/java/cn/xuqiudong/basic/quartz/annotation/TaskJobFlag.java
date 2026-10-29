package cn.xuqiudong.basic.quartz.annotation;

import cn.xuqiudong.basic.quartz.enums.TaskJobLogPolicy;
import cn.xuqiudong.basic.quartz.model.TaskJobExecutionResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 * 标注一个方法是一个定时任务函数，
 * 此方法必须是 public 非静态方法，接收一个 TaskJobLog 参数并返回非空的
 * {@link TaskJobExecutionResult}。
 * 建议其他非定时任务函数定义为非public
 *
 * @author Vic.xu
 * @since 2025-01-17 14:59
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskJobFlag {

    /**
     * @return 用于注册和触发的唯一任务编码
     */
    String value();

    /**
     * @return 任务描述，用于执行日志
     */
    String text() default "";

    /**
     * 决定是否保留跳过记录；主日志在执行前创建，忽略跳过时删除本次主日志及明细。
     *
     * @return 跳过日志策略，默认记录所有执行
     */
    TaskJobLogPolicy logPolicy() default TaskJobLogPolicy.ALL;

}
