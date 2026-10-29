package cn.xuqiudong.basic.quartz.enums;

/**
 * 任务日志留存策略，由任务注解配置，依据 {@link TaskJobResult} 决定是否保留本次记录。
 * 主日志始终在业务执行前创建；忽略跳过时，在任务返回后删除本次主日志及明细。
 *
 * @author Vic.xu
 * @since 2026-10-28
 */
public enum TaskJobLogPolicy {
    /**
     * 保留所有执行记录，包括 {@link TaskJobResult#SKIPPED}，为默认策略。
     */
    ALL,
    /**
     * 忽略 {@link TaskJobResult#SKIPPED}：删除本次主日志及明细。
     * 成功、主动失败及实际异常仍保留，不影响应用日志输出。
     */
    IGNORE_SKIPPED
}
