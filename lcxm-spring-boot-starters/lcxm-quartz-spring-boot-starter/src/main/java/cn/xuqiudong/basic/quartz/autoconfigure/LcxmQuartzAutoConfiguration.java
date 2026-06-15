package cn.xuqiudong.basic.quartz.autoconfigure;

import cn.xuqiudong.basic.quartz.entry.UnifyTaskEntry;
import cn.xuqiudong.basic.quartz.helper.CommonJobQuartzHelper;
import cn.xuqiudong.basic.quartz.job.AbstractTaskJob;
import cn.xuqiudong.basic.quartz.runner.TaskJobInitializationRunner;
import cn.xuqiudong.basic.quartz.service.TaskJobService;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Scheduler;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.List;
import java.util.Map;

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
    public SpringBeanJobFactory springBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
        return new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                Object job = super.createJobInstance(bundle);
                // 注入 Spring Bean
                beanFactory.autowireBean(job);
                return job;
            }
        };
    }

    /**
     * 注入到SpringBoot创建的SchedulerFactoryBean中
     * 而不是注解注册 bean  SchedulerFactoryBean 不然需要标注为Primary，且可能丢失一些配置
     * @see QuartzAutoConfiguration#quartzScheduler(QuartzProperties, ObjectProvider, ObjectProvider, Map, ObjectProvider, ApplicationContext)
     */
    @Bean
    public SchedulerFactoryBeanCustomizer quartzCustomizer(SpringBeanJobFactory springBeanJobFactory) {
        return schedulerFactoryBean ->
                schedulerFactoryBean.setJobFactory(springBeanJobFactory);
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
