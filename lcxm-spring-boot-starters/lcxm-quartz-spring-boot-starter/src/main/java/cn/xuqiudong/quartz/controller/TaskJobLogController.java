package cn.xuqiudong.quartz.controller;

import cn.xuqiudong.basic.core.controller.BaseController;
import cn.xuqiudong.quartz.model.TaskJobLog;
import cn.xuqiudong.quartz.service.TaskJobLogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能: :定时任务执行记录 控制层
 *
 * @author Vic.xu
 * @since 2025-01-20 10:17
 */
@RestController
@RequestMapping("/quartz/taskJobLog")
public class TaskJobLogController extends BaseController<TaskJobLogService, TaskJobLog> {

}
