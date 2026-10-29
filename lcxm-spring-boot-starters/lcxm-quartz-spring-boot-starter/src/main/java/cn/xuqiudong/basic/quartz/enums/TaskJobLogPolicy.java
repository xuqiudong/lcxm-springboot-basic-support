package cn.xuqiudong.basic.quartz.enums;

/**
 * 任务注解中跳过执行时的主日志策略。
 * 当前仅保留策略声明，尚未接入日志生命周期处理。
 *
 * @author Vic.xu
 * @since 2026-10-28
 */
public enum TaskJobLogPolicy {
    /**
     * 记录包括 SKIPPED 在内的所有执行结果，保持原有默认行为。
     */
    ALL,
    /**
     * 不保留 SKIPPED 执行记录；正常执行和异常仍保留。
     */
    SKIP_SKIPPED
}
