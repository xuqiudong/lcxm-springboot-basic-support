package cn.xuqiudong.basic.quartz.service;

import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.core.model.SelectOption;
import cn.xuqiudong.basic.core.util.SelectOptionBuilder;
import cn.xuqiudong.basic.framework.select.service.BusinessSelectProvider;
import cn.xuqiudong.basic.mybatisplus.convert.PageConvert;
import cn.xuqiudong.basic.quartz.entity.TaskJob;
import cn.xuqiudong.basic.quartz.mapper.TaskJobMapper;
import cn.xuqiudong.basic.quartz.query.TaskJobQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * quartz任务表 Service
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Service
public class TaskJobService implements BusinessSelectProvider {


    @Autowired
    private TaskJobMapper mapper;


    /**
     * 根据id查询全部字段
     */
    public TaskJob selectById(String id) {
        return mapper.selectByIdWithLob(id);
    }

    /**
     * 分页查询
     */
    public PageInfo<TaskJob> page(TaskJobQuery query) {
        Assert.notNull(query, "query can not be null");
        Page<TaskJob> page = mapper.selectPage(query);
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
    public int saveBatch(List<TaskJob> entityList) {
        return mapper.saveBatch(entityList);
    }

    /**
     * 保存
     */
    public int save(TaskJob entity) {
        Assert.notNull(entity, "entity can not be null");
        return mapper.save(entity);
    }

    /**
     * 查询所有
     */
    public List<TaskJob> all() {
        LambdaQueryWrapper<TaskJob> query = Wrappers.lambdaQuery();
        query.eq(TaskJob::getEnabled, true);
        return mapper.selectList(query);
    }

    @Override
    public String selectType() {
        return "taskJobOptions";
    }

    @Override
    public List<SelectOption> getSelectOptions() {
        List<TaskJob> all = all();
        return SelectOptionBuilder.buildFlatSelect(all, TaskJob::getId, TaskJob::getName);
    }
}
