package cn.xuqiudong.basic.quartz.job;

import cn.xuqiudong.basic.quartz.constant.QuartzConstant;
import cn.xuqiudong.basic.quartz.entity.TaskJob;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.entry.UnifyTaskEntry;
import cn.xuqiudong.basic.quartz.enums.TaskJobResult;
import cn.xuqiudong.basic.quartz.helper.JobUserHolder;
import cn.xuqiudong.basic.quartz.service.TaskJobLogService;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.InvocationTargetException;

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

    @Resource
    private UnifyTaskEntry unifyTaskEntry;

    @Resource
    private TaskJobLogService taskJobLogService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        JobKey key = context.getJobDetail().getKey();
        // 对应task  code
        String taskName = key.getName();
        LOGGER.info("任务开始执行，任务CODE：{}", taskName);
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        //获取当前任务携带的用户信息
        JobUserHolder.setJobUser(jobDataMap);
        //当前任务对应的 TaskJob， 在任务创建的时候设置而来
        TaskJob taskJob = (TaskJob) jobDataMap.get(QuartzConstant.TASK_JOB_HOLDER_KEY);
        //记录任务执行记录
        TaskJobLog taskJobLog = taskJobLogService.startLog(taskJob);
        TaskJobResult taskJobResult = TaskJobResult.SUCCESS;
        String result = null;
        long start = System.currentTimeMillis();
        try {
            // 定时任务业务逻辑
            result = unifyTaskEntry.executeTask(taskName, taskJobLog);
            LOGGER.info("任务执行完毕，任务名称：{}，结果：{}", taskName, result);
        } catch (Exception e) {
            Throwable throwable = e;
            // // 这是反射的内部的方法报的真正的异常信息
            if (e instanceof InvocationTargetException) {
                throwable = ((InvocationTargetException) e).getTargetException();
            }
            taskJobResult = TaskJobResult.FAILURE;
            result = "执行定时任务[" + taskJobLog.getTaskJobName() + "]失败，失败原因：" + throwable.getMessage();
            LOGGER.error("任务执行失败，任务名称：{}", taskName, throwable);
        } finally {
            long cost = System.currentTimeMillis() - start;
            //记录执行结果
            taskJobLogService.endLog(taskJobLog, taskJobResult, result, cost);
            JobUserHolder.clearJobUser();
        }
    }
}
