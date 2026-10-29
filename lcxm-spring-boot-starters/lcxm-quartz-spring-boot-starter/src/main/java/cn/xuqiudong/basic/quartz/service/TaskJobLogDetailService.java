package cn.xuqiudong.basic.quartz.service;

import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.mybatisplus.convert.PageConvert;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.entity.TaskJobLogDetail;
import cn.xuqiudong.basic.quartz.helper.JobUserHolder;
import cn.xuqiudong.basic.quartz.mapper.TaskJobLogDetailMapper;
import cn.xuqiudong.basic.quartz.query.TaskJobLogDetailQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务执行记录明细 Service
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Service
public class TaskJobLogDetailService {

    /**
     * 单条明细说明的最大长度。
     */
    private static final int MAX_LENGTH = 2048;

    /**
     * 业务主动记录的任务明细持久化操作。
     */
    @Autowired
    private TaskJobLogDetailMapper mapper;


    /**
     * 根据id查询全部字段
     */
    public TaskJobLogDetail selectById(String id) {
        return mapper.selectByIdWithLob(id);
    }

    public List<TaskJobLogDetail> selectByLogId(String id) {
        return mapper.selectListByColumn(TaskJobLogDetail::getTaskJobLogId, id);
    }

    /**
     * 分页查询
     */
    public PageInfo<TaskJobLogDetail> page(TaskJobLogDetailQuery query) {
        Assert.notNull(query, "query can not be null");
        Page<TaskJobLogDetail> page = mapper.selectPage(query);
        return PageConvert.convert(page);
    }


    /**
     * 删除
     */
    public int delete(String id) {
        return mapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public int delete(String[] ids) {
        return mapper.deleteByIds(ids);
    }


    /**
     * 判断字段可用， 非重复
     */
    public boolean isValueAvailable(String id, Object value, String column) {
        return mapper.isValueAvailable(id, value, column);
    }

    /**
     * 修改 enable 状态
     */
    public int updateEnable(String id, Boolean enable) {
        return mapper.updateEnable(id, enable);
    }

    /**
     * 批量保存
     */
    public int saveBatch(List<TaskJobLogDetail> entityList) {
        return mapper.saveBatch(entityList);
    }

    /**
     * 保存
     */
    public int save(TaskJobLogDetail entity) {
        Assert.notNull(entity, "entity can not be null");
        return mapper.save(entity);
    }

    /**
     * 由业务按需记录关联主日志的明细，框架不缓存或自动收集。
     * REQUIRED 会加入已有事务，无事务时才新建；不保证业务回滚后仍保留该明细。
     * 多线程任务须自行设置和清理子线程的 JobUserHolder，并在任务返回前等待明细写入提交，
     * 避免跳过记录删除后仍有迟到的明细写入。
     *
     * @param taskJobLog 日志主表
     * @param result     结果，可 考虑使用 TaskJobResult
     * @param resultNote 备注说明
     * @return TaskJobLogDetail
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public TaskJobLogDetail logDetail(TaskJobLog taskJobLog, String result, String resultNote) {
        TaskJobLogDetail detail = new TaskJobLogDetail();
        detail.setTaskJobLogId(taskJobLog.getId());
        detail.setResult(result);
        resultNote = StringUtils.abbreviate(resultNote, MAX_LENGTH);
        detail.setResultNote(resultNote);
        String userId = JobUserHolder.getUserIdNotNull();
        detail.setCreateBy(userId);
        detail.setUpdateBy(userId);
        detail.setCreateTime(LocalDateTime.now());
        detail.setUpdateTime(LocalDateTime.now());
        save(detail);
        return detail;
    }


}
