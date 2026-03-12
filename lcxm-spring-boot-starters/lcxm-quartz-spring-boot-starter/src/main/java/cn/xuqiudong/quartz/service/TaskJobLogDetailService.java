package cn.xuqiudong.quartz.service;

import cn.xuqiudong.basic.mybatisplus.service.BaseService;
import cn.xuqiudong.quartz.helper.JobUserHolder;
import cn.xuqiudong.quartz.mapper.TaskJobLogDetailMapper;
import cn.xuqiudong.quartz.model.TaskJobLog;
import cn.xuqiudong.quartz.model.TaskJobLogDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 功能: :定时任务执行记录明细 Service
 *
 * @author Vic.xu
 * @since 2025-01-20 10:17
 */
@Service
public class TaskJobLogDetailService extends BaseService<TaskJobLogDetailMapper, TaskJobLogDetail> {

    private static final int MAX_LENGTH = 2048;


    @Override
    protected boolean hasAttachment() {
        return false;
    }

    /**
     * 记录日志明细, 在新事务中执行
     * @param taskJobLog 日志主表
     * @param result 结果，可 考虑使用 TaskJobResult
     * @param resultNote 备注说明
     * @return TaskJobLogDetail
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public TaskJobLogDetail logDetail(TaskJobLog taskJobLog, String result, String resultNote){
        TaskJobLogDetail detail = new TaskJobLogDetail();
        detail.setTaskJobLogId(taskJobLog.getId());
        detail.setResult(result);
        resultNote = StringUtils.abbreviate(resultNote, MAX_LENGTH);
        detail.setResultNote(resultNote);
        Integer userId = JobUserHolder.getUserIdNotNull();
        detail.setCreateId(userId);
        detail.setUpdateId(userId);
        detail.setCreateTime(new Date());
        detail.setUpdateTime(new Date());
        save(detail);
        return detail;
    }

}
