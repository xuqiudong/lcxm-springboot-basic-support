package cn.xuqiudong.quartz.autoconfigure;

import cn.xuqiudong.quartz.entry.UnifyTaskEntry;
import cn.xuqiudong.quartz.helper.CommonJobQuartzHelper;
import cn.xuqiudong.quartz.job.AbstractTaskJob;
import cn.xuqiudong.quartz.runner.TaskJobInitializationRunner;
import cn.xuqiudong.quartz.service.TaskJobService;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 描述:
 * 自定义的定时器starter配置
 *
 * @author Vic.xu
 * @since 2025-01-17 16:35
 */
@Configuration
@ConditionalOnExpression("${spring.quartz.enabled:true}")
@AutoConfigureAfter(QuartzAutoConfiguration.class)
@ComponentScan(basePackages = {"cn.xuqiudong.quartz.service", "cn.xuqiudong.quartz.controller"})
@MapperScan(basePackages = {"cn.xuqiudong.quartz.mapper"})
public class LcxmQuartzAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LcxmQuartzAutoConfiguration.class);

    public LcxmQuartzAutoConfiguration() {
        LOGGER.info("init 自定义的定时器starter配置类");
    }

    @Bean
    @ConditionalOnMissingBean
    public UnifyTaskEntry unifyTaskEntry(List<AbstractTaskJob> taskJobList) {
        LOGGER.info("init 通益任务入口类 UnifyTaskEntry");
        return new UnifyTaskEntry(taskJobList);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommonJobQuartzHelper commonJobQuartzHelper(Scheduler scheduler) {
        LOGGER.info("init 通用任务帮助类 CommonJobQuartzHelper");
        return new CommonJobQuartzHelper(scheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("${spring.quartz.task.init:true}")
    public TaskJobInitializationRunner taskJobInitializationRunner(CommonJobQuartzHelper commonJobQuartzHelper, TaskJobService taskJobService) {
        LOGGER.info("init 初始化定时任务到quartz runner: TaskJobInitializationRunner");
        return new TaskJobInitializationRunner(commonJobQuartzHelper, taskJobService);
    }


}
