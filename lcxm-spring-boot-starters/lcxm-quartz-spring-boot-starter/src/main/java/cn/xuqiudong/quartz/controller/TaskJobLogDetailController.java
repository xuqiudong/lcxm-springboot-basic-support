package cn.xuqiudong.quartz.controller;

import cn.xuqiudong.basic.core.controller.BaseController;
import cn.xuqiudong.quartz.model.TaskJobLogDetail;
import cn.xuqiudong.quartz.service.TaskJobLogDetailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能: :定时任务执行记录明细 控制层
 *
 * @author Vic.xu
 * @since 2025-01-20 10:17
 */
@RestController
@RequestMapping("/quartz/taskJobLogDetail")
public class TaskJobLogDetailController extends BaseController<TaskJobLogDetailService, TaskJobLogDetail> {

}
