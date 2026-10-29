package cn.xuqiudong.basic.quartz.model;

import cn.xuqiudong.basic.quartz.enums.TaskJobResult;

import java.util.Objects;

/**
 * 任务方法返回的执行结论及说明，框架据此完成主日志或按策略删除跳过记录。
 * 业务查询、跳过判断和处理在同一个任务方法中完成；实际异常直接抛出。
 *
 * @author Vic.xu
 * @since 2026-10-28
 */
public final class TaskJobExecutionResult {
    /**
     * 本次执行结论：SUCCESS、FAILURE 或 SKIPPED。
     */
    private final TaskJobResult result;

    /**
     * 供主日志记录的执行说明；允许为空。
     */
    private final String message;

    /**
     * 统一构造已确定结论的执行结果。
     *
     * @param result  执行结论，不能为空
     * @param message 执行说明
     * @throws NullPointerException 执行结论为空
     */
    private TaskJobExecutionResult(TaskJobResult result, String message) {
        this.result = Objects.requireNonNull(result, "result");
        this.message = message;
    }

    /**
     * 表示本次业务处理成功。
     *
     * @param message 成功说明
     * @return 实际执行成功的结果
     */
    public static TaskJobExecutionResult success(String message) {
        return new TaskJobExecutionResult(TaskJobResult.SUCCESS, message);
    }

    /**
     * 表示业务主动报告失败，不作为抛给 Quartz 的执行异常。
     *
     * @param message 失败说明
     * @return 业务执行失败的结果
     */
    public static TaskJobExecutionResult failure(String message) {
        return new TaskJobExecutionResult(TaskJobResult.FAILURE, message);
    }

    /**
     * 表示本次无需处理，例如查询后发现 IP 未变化；是否保留日志由任务注解决定。
     * 此结果只表达业务结论，不会回滚已发生的业务操作。
     *
     * @param message 跳过原因
     * @return 无需执行本次业务的结果
     */
    public static TaskJobExecutionResult skipped(String message) {
        return new TaskJobExecutionResult(TaskJobResult.SKIPPED, message);
    }

    /**
     * @return 任务执行结论
     */
    public TaskJobResult getResult() {
        return result;
    }

    /**
     * @return 执行说明，可能为空
     */
    public String getMessage() {
        return message;
    }
}
