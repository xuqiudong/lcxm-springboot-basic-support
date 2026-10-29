package cn.xuqiudong.basic.quartz.model;

import cn.xuqiudong.basic.quartz.enums.TaskJobResult;

import java.util.Objects;

/**
 * 结构化的任务执行结果；当前调用链仍使用原有 String 返回值，尚未接入此类型。
 *
 * @author Vic.xu
 * @since 2026-10-28
 */
public final class TaskJobExecutionResult {
    /**
     * 实际业务执行结论，只能是 SUCCESS 或 FAILURE。
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
