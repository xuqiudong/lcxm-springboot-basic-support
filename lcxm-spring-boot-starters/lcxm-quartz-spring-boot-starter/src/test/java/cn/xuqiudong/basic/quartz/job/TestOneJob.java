package cn.xuqiudong.basic.quartz.job;

import cn.xuqiudong.basic.quartz.annotation.TaskJobFlag;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.enums.TaskJobResult;
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
public class TestOneJob extends AbstractTaskJob{

    @Resource
    private TaskJobLogDetailService taskJobLogDetailService;

    @TaskJobFlag(value = "test.one", text = "测试任务")
    public String someTask(TaskJobLog taskJobLog){
        LocalTime now = LocalTime.now();
        taskJobLogDetailService.logDetail(taskJobLog, TaskJobResult.SUCCESS.name(), "测试任务执行成功: " + now) ;
        return "someTask";
    }
}
