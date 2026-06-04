package cn.xuqiudong.basic.quartz.autoconfigure;

import cn.xuqiudong.basic.quartz.entry.UnifyTaskEntry;
import cn.xuqiudong.basic.quartz.helper.CommonJobQuartzHelper;
import cn.xuqiudong.basic.quartz.job.AbstractTaskJob;
import cn.xuqiudong.basic.quartz.runner.TaskJobInitializationRunner;
import cn.xuqiudong.basic.quartz.service.TaskJobService;
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
 * <p>
 * 此处使用了 @MapperScan
 * 主项目也需要自行定义@MapperScan 扫描自己的Mapper
 *
 * @author Vic.xu
 * @since 2025-01-17 16:35
 */
@Configuration
@ConditionalOnExpression("${spring.quartz.enabled:true}")
@AutoConfigureAfter(QuartzAutoConfiguration.class)
@ComponentScan(basePackages = {"cn.xuqiudong.basic.quartz.service", "cn.xuqiudong.basic.quartz.controller"})
@MapperScan(basePackages = {"cn.xuqiudong.basic.quartz.mapper"})
public class LcxmQuartzAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LcxmQuartzAutoConfiguration.class);

    public LcxmQuartzAutoConfiguration() {
        LOGGER.info("init 自定义的定时器starter配置类");
    }

    @Bean
    @ConditionalOnMissingBean
    public UnifyTaskEntry unifyTaskEntry(List<AbstractTaskJob> taskJobList) {
        LOGGER.info("init 通用任务入口类 UnifyTaskEntry");
        return new UnifyTaskEntry(taskJobList);
    }

    @Bean
    @ConditionalOnMissingBean
    public CommonJobQuartzHelper commonJobQuartzHelper(Scheduler scheduler, UnifyTaskEntry unifyTaskEntry) {
        LOGGER.info("init 通用任务帮助类 CommonJobQuartzHelper");
        return new CommonJobQuartzHelper(scheduler, unifyTaskEntry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("${spring.quartz.task.init:true}")
    public TaskJobInitializationRunner taskJobInitializationRunner(CommonJobQuartzHelper commonJobQuartzHelper, TaskJobService taskJobService) {
        LOGGER.info("init 初始化定时任务到quartz runner: TaskJobInitializationRunner");
        return new TaskJobInitializationRunner(commonJobQuartzHelper, taskJobService);
    }


}
