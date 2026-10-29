package cn.xuqiudong.basic.quartz.job;

import cn.xuqiudong.basic.quartz.constant.QuartzConstant;
import cn.xuqiudong.basic.quartz.entity.TaskJob;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.entry.UnifyTaskEntry;
import cn.xuqiudong.basic.quartz.enums.TaskJobLogPolicy;
import cn.xuqiudong.basic.quartz.enums.TaskJobResult;
import cn.xuqiudong.basic.quartz.helper.JobUserHolder;
import cn.xuqiudong.basic.quartz.model.TaskJobExecutionResult;
import cn.xuqiudong.basic.quartz.service.TaskJobLogService;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 这是一个通用的Job,在通过JobBuilder 构建Job实例的时候就可以使用 CommonJob
 * 通过通用Job进行任务转发，目的是为了减少job的数量
 *
 * @author Vic.xu
 * @since 2025-01-17 15:28
 */
//@PersistJobDataAfterExecution
// 禁止同一个 JobDetail 的多个实例同时执行。
@DisallowConcurrentExecution
public class CommonJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonJob.class);

    /**
     * 按任务编码转发到注册中心中的业务方法。
     */
    @Resource
    private UnifyTaskEntry unifyTaskEntry;

    /**
     * 在业务前创建主日志，并按结果完成或删除本次记录。
     */
    @Resource
    private TaskJobLogService taskJobLogService;

    /**
     * 调度入口：确认处理器与策略、创建主日志、执行任务并收尾，最终清理当前线程的用户。
     * 业务返回 failure 仅作为执行结果；实际异常交给 Quartz 感知。
     * 抛出的异常不设置立即重试或取消后续调度的标志。
     *
     * @param context Quartz 本次调度上下文
     * @throws JobExecutionException 任务执行、注册查找或日志处理异常
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        // JobKey 的名称对应任务注解中的唯一编码。
        String taskName = context.getJobDetail().getKey().getName();
        LOGGER.info("任务开始执行，任务CODE：{}", taskName);
        long start = System.nanoTime();
        try {
            JobDataMap jobDataMap = context.getMergedJobDataMap();
            // 获取当前任务携带的用户信息，主日志与业务明细使用同一执行用户。
            JobUserHolder.setJobUser(jobDataMap);
            TaskJobLogPolicy policy = unifyTaskEntry.getLogPolicy(taskName);
            // 当前任务对应的 TaskJob 在创建调度时放入 JobDataMap。
            TaskJob taskJob = (TaskJob) jobDataMap.get(QuartzConstant.TASK_JOB_HOLDER_KEY);
            // 先创建主日志并取得 ID，业务可在同一个方法内判断跳过并随时写入明细。
            // 此步骤失败时不调用业务，异常由外层上报，用户上下文仍会清理。
            TaskJobLog taskJobLog = taskJobLogService.startLog(taskJob);
            executeRegisteredTask(taskName, taskJobLog, policy, start);
        } catch (Exception e) {
            long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            LOGGER.error("任务处理异常，任务CODE：{}，耗时：{}ms", taskName, cost, e);
            if (e instanceof JobExecutionException jobException) {
                throw jobException;
            }
            throw new JobExecutionException("任务调度处理失败，任务CODE：" + taskName, e);
        } finally {
            // 清理不依赖日志写入是否成功，避免 Quartz 复用线程时遗留上一次执行用户。
            JobUserHolder.clearJobUser();
        }
    }

    /**
     * 调用业务得到结果，再统一收尾日志，最后传播实际异常。
     * 主动返回 failure 不产生执行异常；业务与日志同时失败时保留业务原始异常为主因。
     * 耗时从调度入口开始，覆盖主日志创建和业务调用，不含最终日志保存或删除。
     *
     * @param taskName   任务编码
     * @param taskJobLog 本次执行已创建的主日志
     * @param policy     注解声明的日志策略
     * @param start      调度入口的单调时钟起点，单位为纳秒
     * @throws JobExecutionException 业务执行或日志收尾异常
     */
    private void executeRegisteredTask(String taskName, TaskJobLog taskJobLog, TaskJobLogPolicy policy,
                                       long start) throws JobExecutionException {
        TaskJobExecutionResult result;
        JobExecutionException executionException = null;
        try {
            result = unifyTaskEntry.executeTask(taskName, taskJobLog);
        } catch (Exception e) {
            // 反射异常只是包装，主日志与 Quartz 应保留业务抛出的真正原因。
            Throwable cause = unwrapInvocationException(e);
            String message = "执行定时任务[" + taskJobLog.getTaskJobName() + "]失败，失败原因：" + cause.getMessage();
            result = TaskJobExecutionResult.failure(message);
            executionException = new JobExecutionException("任务业务执行失败，任务CODE：" + taskName, cause);
        }

        // 正常返回和实际异常共用一次耗时计算与日志收尾，避免重复更新主日志。
        long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        LOGGER.info("任务业务执行结束，任务CODE：{}，结果：{}，说明：{}，耗时：{}ms",
                taskName, result.getResult(), result.getMessage(), cost);
        try {
            finishLog(taskJobLog, result, policy, cost);
        } catch (Exception logException) {
            if (executionException == null) {
                executionException = new JobExecutionException("任务日志处理失败，业务结果：" + result.getResult()
                        + "，主日志ID：" + taskJobLog.getId(), logException);
            } else {
                // 双重失败时把日志异常附在业务原始异常上，外层统一打印完整异常链。
                Throwable cause = executionException.getCause();
                if (cause != logException) {
                    cause.addSuppressed(logException);
                }
            }
        }
        // 所有日志收尾完成后再通知 Quartz；不请求立即重试，也不取消后续调度。
        if (executionException != null) {
            throw executionException;
        }
    }

    /**
     * 按最终结果收尾：忽略跳过时原子删除本次主日志及明细，其余情况更新为已完成。
     * 此处失败仅报告日志处理异常，不改写已经确定的业务结果。
     *
     * @param taskJobLog 本次执行的主日志
     * @param result     非空的业务执行结果
     * @param policy     注解声明的日志策略
     * @param cost       主日志创建及业务调用的耗时，单位为毫秒
     */
    private void finishLog(TaskJobLog taskJobLog, TaskJobExecutionResult result, TaskJobLogPolicy policy,
                           long cost) {
        if (policy == TaskJobLogPolicy.IGNORE_SKIPPED && result.getResult() == TaskJobResult.SKIPPED) {
            // 调用 Spring 服务代理，在短事务中删除；不包围整个业务执行。
            taskJobLogService.delete(taskJobLog.getId());
        } else {
            taskJobLogService.endLog(taskJobLog, result.getResult(), result.getMessage(), cost);
        }
    }

    /**
     * 解包反射调用异常，保留任务方法实际抛出的异常及其堆栈。
     *
     * @param exception 框架捕获的异常
     * @return 最内层反射包装中的真实异常；非反射异常原样返回
     */
    private Throwable unwrapInvocationException(Exception exception) {
        Throwable cause = exception;
        while (cause instanceof InvocationTargetException invocationException
                && invocationException.getTargetException() != null) {
            cause = invocationException.getTargetException();
        }
        return cause;
    }
}
