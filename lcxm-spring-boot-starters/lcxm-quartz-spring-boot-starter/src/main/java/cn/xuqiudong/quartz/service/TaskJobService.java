package cn.xuqiudong.quartz.service;

import cn.xuqiudong.basic.mybatisplus.service.BaseService;
import cn.xuqiudong.quartz.mapper.TaskJobMapper;
import cn.xuqiudong.quartz.model.TaskJob;
import org.springframework.stereotype.Service;

/**
 * 功能: :quartz任务表 Service
 *
 * @author Vic.xu
 * @since 2025-01-20 10:17
 */
@Service
public class TaskJobService extends BaseService<TaskJobMapper, TaskJob> {

    @Override
    protected boolean hasAttachment() {
        return false;
    }

}
