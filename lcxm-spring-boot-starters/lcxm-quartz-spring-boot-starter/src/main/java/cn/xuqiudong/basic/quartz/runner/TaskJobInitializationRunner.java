package cn.xuqiudong.basic.quartz.runner;

import cn.xuqiudong.basic.quartz.entity.TaskJob;
import cn.xuqiudong.basic.quartz.enums.QuartzStatusEnum;
import cn.xuqiudong.basic.quartz.helper.CommonJobQuartzHelper;
import cn.xuqiudong.basic.quartz.service.TaskJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 初始化定时任务到quartz
 *
 * @author Vic.xu
 * @since 2025-01-20 16:01
 */
public class TaskJobInitializationRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskJobInitializationRunner.class);

    private CommonJobQuartzHelper commonJobQuartzHelper;

    private TaskJobService taskJobService;

    public TaskJobInitializationRunner(CommonJobQuartzHelper commonJobQuartzHelper, TaskJobService taskJobService) {
        this.commonJobQuartzHelper = commonJobQuartzHelper;
        this.taskJobService = taskJobService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskJobs().forEach(taskJob -> {
            LOGGER.info("定时任务[{}] 初始化. code=[{}], group=[{}], cron=[{}]",
                    taskJob.getName(), taskJob.getTaskCode(), taskJob.getTaskGroup(), taskJob.getCron());
            commonJobQuartzHelper.createJob(taskJob);
        });
    }

    public List<TaskJob> taskJobs() {
        List<TaskJob> list = taskJobService.allEnable();
        return list.stream().filter(taskJob ->
                QuartzStatusEnum.REMOVE != taskJob.getStatus()
        ).collect(Collectors.toList());
    }
}
