package cn.xuqiudong.quartz.helper;

import cn.xuqiudong.common.base.vo.BooleanWithMsg;
import cn.xuqiudong.quartz.constant.QuartzConstant;
import cn.xuqiudong.quartz.enums.QuartzStatusEnum;
import cn.xuqiudong.quartz.job.CommonJob;
import cn.xuqiudong.quartz.model.TaskJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 * 基于CommonJob的定时任务的工具类
 *
 * @author Vic.xu
 * @since 2025-01-17 16:49
 */
public class CommonJobQuartzHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonJobQuartzHelper.class);

    private static final Class COMMON_JOB_CLASS = CommonJob.class;

    private Scheduler scheduler;

    public CommonJobQuartzHelper(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 创建通用的Job定时任务:基于CommonJob
     */
    public BooleanWithMsg createJob(TaskJob taskJob) {
        if (taskJob == null) {
            return BooleanWithMsg.fail("任务参数为空");
        }
        Map<String, Object> params = new HashMap<>();
        params.put(QuartzConstant.TASK_JOB_HOLDER_KEY, taskJob);
        return createJob(taskJob.getCode(), taskJob.getGroup(), COMMON_JOB_CLASS, taskJob.getCron(), taskJob.getStatus(), params);

    }

    /**
     * 创建通用的Job定时任务
     *
     * @param name   任务名称  是唯一标识
     * @param group  组
     * @param cron   cron 表达式
     * @param status 任务状态，若是暂停，则暂停
     * @param params 任务参数
     */
    public BooleanWithMsg createJob(String name, String group, String cron, QuartzStatusEnum status, Map<String, Object> params) {
        return createJob(name, group, COMMON_JOB_CLASS, cron, status, params);

    }

    /**
     * 创建定时任务
     *
     * @param name     任务名称  是唯一标识
     * @param group    组
     * @param jobClass job class
     * @param cron     cron 表达式
     * @param status   任务状态，若是暂停，则暂停
     * @param params   任务参数
     */
    public BooleanWithMsg createJob(String name, String group, Class<? extends Job> jobClass, String cron, QuartzStatusEnum status, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (status == null || status == QuartzStatusEnum.REMOVE) {
            LOGGER.error("错误的任务状态:｛｝", status);
            return BooleanWithMsg.fail("错误的任务状态:" + status);
        }
        try {
            if (existJob(name, group)) {
                LOGGER.info("{}， {} 定时任务已经存在", name, group);
                return BooleanWithMsg.fail("任务已存在");
            }
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name, group)
                    .usingJobData(new JobDataMap(params)).build();
            //基于表达式构建触发器
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, cronTrigger);

            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
            if (status == QuartzStatusEnum.PAUSE) {
                return pauseJob(name, group);
            }
            return BooleanWithMsg.success();
        } catch (Exception e) {
            LOGGER.error("创建定时任务失败", e);
            return BooleanWithMsg.fail("创建任务失败：" + e.getMessage());
        }

    }

    /**
     * 暂停任务
     */
    public BooleanWithMsg pauseJob(String name, String group) {
        try {
            JobKey jobKey = getJobKey(name, group);
            scheduler.pauseJob(jobKey);
            LOGGER.info("pause job: {} success", name);
            return BooleanWithMsg.success();
        } catch (Exception e) {
            LOGGER.error("暂停任务失败{}", e);
            return BooleanWithMsg.fail("暂停任务失败:" + e.getMessage());
        }
    }

    /**
     * 立刻执行任务
     */
    public BooleanWithMsg runJobNow(String name, String group, Map<String, Object> params) {
        try {
            if (!existJob(name, group)) {
                return BooleanWithMsg.fail("任务不存在");
            }
            JobKey jobKey = getJobKey(name, group);
            scheduler.triggerJob(jobKey, new JobDataMap(params));
            LOGGER.info("=========================runJob Now : {} success========================", name);
            return BooleanWithMsg.success();
        } catch (Exception e) {
            LOGGER.error("立即执行任务失败{}", e);
            return BooleanWithMsg.fail("立即执行任务失败:" + e.getMessage());
        }
    }

    /**
     * 修改任务时间
     */
    public BooleanWithMsg modifyJobTime(String name, String group, String cron) {
        try {
            TriggerKey triggerKey = getTriggerKey(name, group);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return BooleanWithMsg.fail("修改任务失败，任务不存在");
            }
            String oldCron = trigger.getCronExpression();
            if (!oldCron.equalsIgnoreCase(cron)) {
                CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
                CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                        .withSchedule(cronScheduleBuilder)
                        .build();
                scheduler.rescheduleJob(triggerKey, cronTrigger);
                scheduler.resumeTrigger(triggerKey);
            }
            return BooleanWithMsg.success();
        } catch (Exception e) {
            LOGGER.error("修改任务执行时间失败{}", e);
            return BooleanWithMsg.fail("修改任务失败:" + e.getMessage());
        }
    }

    /**
     * 恢复任务
     */
    public BooleanWithMsg resumeJob(String name, String group) {
        try {
            JobKey jobKey = getJobKey(name, group);
            scheduler.resumeJob(jobKey);
            LOGGER.info("=========================resume job: {} success========================", name);
            return BooleanWithMsg.success();
        } catch (Exception e) {
            LOGGER.error("恢复任务失败{}", e);
            return BooleanWithMsg.fail("恢复任务失败:" + e.getMessage());
        }
    }

    /**
     * 删除定时任务
     */
    public BooleanWithMsg deleteJob(String name, String group) {
        try {
            if (!existJob(name, group)) {
                return BooleanWithMsg.fail("任务不存在");
            }
            JobKey jobKey = getJobKey(name, group);
            boolean result = scheduler.deleteJob(jobKey);
            LOGGER.info("=========================remove job: {} {}========================", name, result);
            return BooleanWithMsg.success().setSuccess(result);
        } catch (Exception e) {
            LOGGER.error("删除任务失败{}", e);
            return BooleanWithMsg.fail("删除任务失败:" + e.getMessage());
        }
    }

    /**
     * 判断定时任务是否存在
     */
    public boolean existJob(String name, String group) {
        try {
            JobKey jobKey = getJobKey(name, group);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            return jobDetail != null;
        } catch (Exception e) {
            LOGGER.error("查询任务失败{}", e);
            return false;
        }
    }

    /**
     * 获取 JobKey
     */
    public static JobKey getJobKey(String name, String group) {
        return JobKey.jobKey(name, group);
    }

    /**
     * 获取 TriggerKey
     */
    public static TriggerKey getTriggerKey(String name, String group) {
        return TriggerKey.triggerKey(name, group);
    }


}
