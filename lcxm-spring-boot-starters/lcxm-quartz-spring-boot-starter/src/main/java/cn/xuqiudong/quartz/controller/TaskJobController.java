package cn.xuqiudong.quartz.controller;

import cn.xuqiudong.basic.core.controller.BaseController;
import cn.xuqiudong.quartz.model.TaskJob;
import cn.xuqiudong.quartz.service.TaskJobService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能: :quartz任务表 控制层
 *
 * @author Vic.xu
 * @since 2025-01-20 10:17
 */
@RestController
@RequestMapping("/quartz/taskJob")
public class TaskJobController extends BaseController<TaskJobService, TaskJob> {

}
