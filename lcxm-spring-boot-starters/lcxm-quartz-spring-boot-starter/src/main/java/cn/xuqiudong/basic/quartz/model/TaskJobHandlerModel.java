package cn.xuqiudong.basic.quartz.model;

import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.enums.TaskJobLogPolicy;
import cn.xuqiudong.basic.quartz.job.AbstractTaskJob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 描述已注册的定时任务及其执行方法。
 * 保存已校验的方法及注解上的跳过日志策略，供统一入口按任务编码执行。
 *
 * @author Vic.xu
 * @since 2025-01-17 15:46
 */
public class TaskJobHandlerModel {

    /**
     * Spring 管理的任务实例，执行时使用该实例以保留代理行为。
     */
    private final AbstractTaskJob jobInstance;

    /**
     * 从任务目标类获取的、已通过签名校验的处理方法。
     */
    private final Method method;

    /**
     * 任务注解声明的唯一编码，用于注册与查找。
     */
    private final String code;

    /**
     * 任务注解声明的描述，用于执行日志。
     */
    private final String text;

    /**
     * 注解声明的跳过日志策略。
     */
    private final TaskJobLogPolicy logPolicy;

    /**
     * 保存通过注册校验的任务方法及其跳过日志策略。
     *
     * @param jobInstance Spring 管理的任务实例
     * @param method      已校验的任务处理方法
     * @param code        任务编码
     * @param text        任务描述
     * @param logPolicy   注解声明的跳过日志策略
     */
    public TaskJobHandlerModel(AbstractTaskJob jobInstance, Method method, String code, String text,
                               TaskJobLogPolicy logPolicy) {
        this.jobInstance = jobInstance;
        this.method = method;
        this.code = code;
        this.text = text;
        this.logPolicy = logPolicy;
    }

    /**
     * 在 Spring 管理的任务实例上调用方法，保留代理及业务事务行为。
     * 业务异常仍由反射包装，交给 CommonJob 解包并记录原始原因。
     *
     * @param log 当前任务的主日志
     * @return 业务返回的执行结论及说明
     * @throws InvocationTargetException 任务方法抛出异常时的反射包装异常
     * @throws IllegalAccessException    无法访问任务方法
     */
    public TaskJobExecutionResult invoke(TaskJobLog log) throws InvocationTargetException, IllegalAccessException {
        return (TaskJobExecutionResult) method.invoke(jobInstance, log);
    }

    /**
     * @return 注册时声明的任务编码
     */
    public String getCode() {
        return code;
    }

    /**
     * @return 注册时声明的任务描述
     */
    public String getText() {
        return text;
    }

    /**
     * @return 注解声明的跳过日志策略
     */
    public TaskJobLogPolicy getLogPolicy() {
        return logPolicy;
    }
}
