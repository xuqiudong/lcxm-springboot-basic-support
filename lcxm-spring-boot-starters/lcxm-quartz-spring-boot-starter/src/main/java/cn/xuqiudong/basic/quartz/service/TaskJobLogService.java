package cn.xuqiudong.basic.quartz.service;

import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.mybatisplus.convert.PageConvert;
import cn.xuqiudong.basic.quartz.entity.TaskJob;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.enums.TaskJobLogStatus;
import cn.xuqiudong.basic.quartz.enums.TaskJobResult;
import cn.xuqiudong.basic.quartz.helper.JobUserHolder;
import cn.xuqiudong.basic.quartz.mapper.TaskJobLogDetailMapper;
import cn.xuqiudong.basic.quartz.mapper.TaskJobLogMapper;
import cn.xuqiudong.basic.quartz.query.TaskJobLogQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 定时任务执行记录 Service
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Service
public class TaskJobLogService {

    /**
     * 主日志持久化操作。
     */
    @Autowired
    private TaskJobLogMapper mapper;

    /**
     * 删除主日志时一并清理关联明细。
     */
    @Autowired
    private TaskJobLogDetailMapper detailMapper;


    /**
     * 根据id查询全部字段
     */
    public TaskJobLog selectById(String id) {
        return mapper.selectByIdWithLob(id);
    }

    /**
     * 分页查询
     */
    public PageInfo<TaskJobLog> page(TaskJobLogQuery query) {
        Assert.notNull(query, "query can not be null");
        Page<TaskJobLog> page = mapper.selectPage(query);
        return PageConvert.convert(page);
    }

    /**
     * 在同一个短事务内删除本次明细和主日志，也用于忽略跳过记录。
     * 必须通过 Spring 服务代理调用，任一删除失败时整体回滚。
     *
     * @param id 主日志 ID
     * @return 删除的主日志数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int delete(String id) {
        // 先删明细再删主日志，保证关联清理顺序。
        detailMapper.deleteBatchByJobLogIds(new String[]{id});
        return mapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public int delete(String[] ids) {
        int num = mapper.deleteByIds(ids);
        // 删除关联的详情
        detailMapper.deleteBatchByJobLogIds(ids);
        return num;
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
    public int saveBatch(List<TaskJobLog> entityList) {
        return mapper.saveBatch(entityList);
    }

    /**
     * 保存
     */
    public int save(TaskJobLog entity) {
        Assert.notNull(entity, "entity can not be null");
        return mapper.save(entity);
    }

    /**
     * 主日志执行说明的最大长度。
     */
    private static final int MAX_LENGTH = 2048;


    /**
     * 业务执行前创建 RUNNING 主日志，生成的 ID 供业务写入明细使用。
     *
     * @param taskJob 定时任务
     * @return 已保存并带有 ID 的本次主日志
     */
    public TaskJobLog startLog(TaskJob taskJob) {
        TaskJobLog taskJobLog = new TaskJobLog();
        taskJobLog.setTaskJobId(taskJob.getId());
        taskJobLog.setTaskJobName(taskJob.getName());
        taskJobLog.setStatus(TaskJobLogStatus.RUNNING.name());
        String userId = JobUserHolder.getUserIdNotNull();
        taskJobLog.setCreateBy(userId);
        taskJobLog.setUpdateBy(userId);
        save(taskJobLog);
        return taskJobLog;
    }

    /**
     * 将本次主日志更新为 FINISHED，保存业务结论、说明和耗时。
     * IGNORE_SKIPPED 策略下的跳过结果由调用方直接删除，不调用此方法。
     *
     * @param taskJobLog 执行记录
     * @param result     结果
     * @param resultNote 结果说明
     * @param costTime   主日志创建及业务调用耗时，单位为毫秒
     * @return 保存后的主日志
     */
    public TaskJobLog endLog(TaskJobLog taskJobLog, TaskJobResult result, String resultNote, Long costTime) {
        taskJobLog.setStatus(TaskJobLogStatus.FINISHED.name());
        taskJobLog.setResult(result.name());
        resultNote = StringUtils.abbreviate(resultNote, MAX_LENGTH);
        taskJobLog.setResultNote(resultNote);
        taskJobLog.setCostTime(costTime);
        String userId = JobUserHolder.getUserIdNotNull();
        taskJobLog.setUpdateBy(userId);
        save(taskJobLog);
        return taskJobLog;
    }
}
