package cn.xuqiudong.basic.quartz.entry;

import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.enums.TaskJobLogPolicy;
import cn.xuqiudong.basic.quartz.model.TaskJobExecutionResult;
import cn.xuqiudong.basic.quartz.model.TaskJobHandlerModel;

import java.util.Objects;

/**
 * 描述:
 * 统一的任务入口 <br />
 * <p>
 * 所以，这里使用策略模式加枚举的方式，减少JobFactory的膨胀，
 * 在新增任务的时候，只要使用抽象类即可。
 * </p>
 * 任务扫描和签名校验由注册中心负责，入口查找处理器并调用业务方法。
 * 不对整个任务开启统一事务，业务事务由任务自行声明；日志生命周期由 CommonJob 管理。
 *
 * @author Vic.xu
 * @since 2025-01-17 15:25
 */
public class UnifyTaskEntry {

    /**
     * 启动时收集的任务索引，按注解编码查找执行方法。
     */
    private final TaskHandlerRegistry registry;

    /**
     * @param registry 已完成任务扫描与校验的注册中心
     */
    public UnifyTaskEntry(TaskHandlerRegistry registry) {
        this.registry = registry;
    }

    /**
     * 根据任务编码调用单方法任务；准备数据、判断是否跳过与实际处理均在业务方法内完成。
     * 返回前，业务应确保子线程及其明细写入已结束且提交，并自行管理子线程用户上下文。
     *
     * @param taskName   任务编码
     * @param taskJobLog 当前任务主日志
     * @return 非空的业务执行结论及说明
     * @throws Exception             反射调用异常或业务异常的包装异常
     * @throws IllegalStateException 任务未注册
     * @throws NullPointerException  任务方法未返回执行结果
     */
    public TaskJobExecutionResult executeTask(String taskName, TaskJobLog taskJobLog) throws Exception {
        TaskJobExecutionResult result = requireHandler(taskName).invoke(taskJobLog);
        return Objects.requireNonNull(result, "任务[" + taskName + "]必须返回 TaskJobExecutionResult，不能为 null");
    }

    /**
     * 读取已注册任务的注解策略；未配置时使用注解默认值 ALL。
     * 在主日志创建前调用，同时确认处理器存在。
     *
     * @param taskName 任务编码
     * @return 本次任务的日志留存策略
     * @throws IllegalStateException 任务未注册
     */
    public TaskJobLogPolicy getLogPolicy(String taskName) {
        return requireHandler(taskName).getLogPolicy();
    }

    /**
     * 查找已经校验的处理器，避免把未注册任务当成成功执行。
     *
     * @param taskName 任务编码
     * @return 已注册的任务处理器
     * @throws IllegalStateException 任务未注册
     */
    private TaskJobHandlerModel requireHandler(String taskName) {
        TaskJobHandlerModel handler = registry.get(taskName);
        if (handler == null) {
            throw new IllegalStateException("没有[" + taskName + "]的处理函数!");
        }
        return handler;
    }

    /**
     * 判断任务是否存在。
     *
     * @param taskName 任务编码
     * @return 是否已注册
     */
    public boolean existTask(String taskName) {
        return registry.contains(taskName);
    }
}
