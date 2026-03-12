package cn.xuqiudong.quartz.service;

import cn.xuqiudong.basic.core.service.BaseService;
import cn.xuqiudong.quartz.enums.TaskJobLogStatus;
import cn.xuqiudong.quartz.enums.TaskJobResult;
import cn.xuqiudong.quartz.helper.JobUserHolder;
import cn.xuqiudong.quartz.mapper.TaskJobLogMapper;
import cn.xuqiudong.quartz.model.TaskJob;
import cn.xuqiudong.quartz.model.TaskJobLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 功能: :定时任务执行记录 Service
 *
 * @author Vic.xu
 * @since 2025-01-20 10:17
 */
@Service
public class TaskJobLogService extends BaseService<TaskJobLogMapper, TaskJobLog> {

    @Override
    protected boolean hasAttachment() {
        return false;
    }

    private static final int MAX_LENGTH = 2048;


    /**
     * 记录定时任务开始执行
     *
     * @param taskJob 定时任务
     * @return TaskJobLog
     */
    public TaskJobLog startLog(TaskJob taskJob) {
        TaskJobLog taskJobLog = new TaskJobLog();
        taskJobLog.setTaskJobId(taskJob.getId());
        taskJobLog.setTaskJobName(taskJob.getName());
        taskJobLog.setStatus(TaskJobLogStatus.RUNNING.name());
        Integer userId = JobUserHolder.getUserIdNotNull();
        taskJobLog.setExecutor(JobUserHolder.getUserNameNotNull());
        taskJobLog.setCreateId(userId);
        taskJobLog.setUpdateId(userId);
        save(taskJobLog);
        return taskJobLog;
    }

    /**
     * 记录定时任务执行结束
     *
     * @param taskJobLog 执行记录
     * @param result     结果
     * @param resultNote 结果说明
     */
    public TaskJobLog endLog(TaskJobLog taskJobLog, TaskJobResult result, String resultNote) {
        taskJobLog.setStatus(TaskJobLogStatus.FINISHED.name());
        taskJobLog.setResult(result.name());
        resultNote = StringUtils.abbreviate(resultNote, MAX_LENGTH);
        taskJobLog.setResultNote(resultNote);
        Integer userId = JobUserHolder.getUserIdNotNull();
        taskJobLog.setUpdateId(userId);
        update(taskJobLog);
        return taskJobLog;
    }


}
