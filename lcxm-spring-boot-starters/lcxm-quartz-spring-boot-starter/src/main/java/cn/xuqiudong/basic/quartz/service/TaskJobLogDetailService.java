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

    private static final int MAX_LENGTH = 2048;

    @Autowired
    private TaskJobLogDetailMapper mapper;


    /**
     * 根据id查询全部字段
     */
    public TaskJobLogDetail selectById(String id) {
        return mapper.selectByIdWithLob(id);
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
     * 记录日志明细, 在新事务中执行
     * 一般自行在业务处理中按需调用此方法
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
