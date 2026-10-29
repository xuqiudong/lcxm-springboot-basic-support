package cn.xuqiudong.basic.quartz.job;

import cn.xuqiudong.basic.quartz.annotation.TaskJobFlag;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.enums.TaskJobResult;
import cn.xuqiudong.basic.quartz.model.TaskJobExecutionResult;
import cn.xuqiudong.basic.quartz.service.TaskJobLogDetailService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-05-29 14:14
 */
@Component
public class TestOneJob extends AbstractTaskJob {

    /**
     * 业务按需写入关联主日志的明细。
     */
    @Resource
    private TaskJobLogDetailService taskJobLogDetailService;

    /**
     * 单方法任务示例：业务主动写入明细并返回结构化结果，默认保留所有日志。
     *
     * @param taskJobLog 当前执行的主日志
     * @return 本次执行结论及说明
     */
    @TaskJobFlag(value = "test.one", text = "测试任务")
    public TaskJobExecutionResult someTask(TaskJobLog taskJobLog) {
        LocalTime now = LocalTime.now();
        taskJobLogDetailService.logDetail(taskJobLog, TaskJobResult.SUCCESS.name(), "测试任务执行成功: " + now);
        return TaskJobExecutionResult.success("someTask");
    }
}
