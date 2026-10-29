package cn.xuqiudong.basic.quartz.enums;

/**
 * 描述:
 * 定时任务运行结果
 *
 * @author Vic.xu
 * @since 2025-01-20 10:38
 */
public enum TaskJobResult {
    /**
     * 实际业务执行成功。
     */
    SUCCESS,
    /**
     * 本次无需处理，例如已查询确认没有待处理数据；日志留存由 TaskJobLogPolicy 决定。
     */
    SKIPPED,
    /**
     * 任务主动返回失败或抛异常后用于记录日志的失败结论。
     */
    FAILURE
}
