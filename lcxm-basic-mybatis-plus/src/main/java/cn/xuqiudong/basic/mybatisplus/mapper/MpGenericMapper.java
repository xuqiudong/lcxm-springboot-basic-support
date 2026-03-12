package cn.xuqiudong.basic.mybatisplus.mapper;

import cn.xuqiudong.basic.mybatisplus.helper.MpGenericMapperHelper;
import cn.xuqiudong.basic.mybatisplus.injector.SelectByIdWithLob;
import cn.xuqiudong.basic.mybatisplus.query.Column;
import cn.xuqiudong.basic.mybatisplus.query.MpQuery;
import cn.xuqiudong.basic.mybatisplus.query.OrderBy;
import cn.xuqiudong.basic.mybatisplus.query.PageQuery;
import cn.xuqiudong.basic.mybatisplus.util.ColumnUtils;
import cn.xuqiudong.basic.mybatisplus.util.WrapUtils;
import cn.xuqiudong.basic.mybatisplus.annotation.QueryCondition;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * 描述:
 * 继承自MybatisPlus的BaseMapper，并添加了泛型ID
 *
 * @author Vic.xu
 * @since 2025-10-27 14:01
 */
public interface MpGenericMapper<ID extends Serializable, T> extends BaseMapper<T> {

    /**
     * 根据id查询包含lob字段的记录
     * lob: @TableField(select = false)
     *
     * @see SelectByIdWithLob
     */
    T selectByIdWithLob(Serializable id);

    /**
     * 根据id批量删除
     */
    default int deleteByIds(ID[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return 0;
        }
        return this.deleteByIds(Arrays.asList(ids));
    }

    /**
     * 判断字段可用， 非重复
     */
    default boolean isValueAvailable(Serializable id, Object value, String column) {
        column = ColumnUtils.safeColumn(column);
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        //  如果id 非 null 则排除当前id所对应的列
        boolean empty = ObjectUtils.isEmpty(id);
        queryWrapper.ne(!empty, "id", id);
        return selectCount(queryWrapper) == 0L;
    }

    /**
     * 修改 enable 状态
     */
    default int updateEnable(Serializable id, Boolean enable) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("enabled", enable);
        return update(updateWrapper);
    }

    /**
     * 保存
     */
    default int save(T entity) {
        return helper().save(entity);
    }

    /**
     * 批量保存
     */
    default int insertBatch(Collection<T> entityList) {
        return helper().insertBatch(entityList);
    }

    /**
     * 批量更新
     */
    default int updateBatch(Collection<T> entityList) {
        return helper().updateBatch(entityList);
    }

    /**
     * 批量保存
     */
    default int saveBatch(Collection<T> entityList) {
        return helper().saveBatch(entityList);
    }


    /**
     * 获取helper
     */
    default MpGenericMapperHelper<ID, T> helper() {
        return MpGenericMapperHelper.getHelper(this);
    }


    /**
     * 分页查询
     *
     * @see QueryCondition
     */
    default Page<T> selectPage(PageQuery query) {
        Page<T> page = query.toPage();
        QueryWrapper<T> queryWrapper = WrapUtils.createWrapper(query);
        return selectPage(page, queryWrapper);
    }

    /**
     * 根据查询条件查询列表
     *
     * @see QueryCondition
     */
    default List<T> selectListByQuery(MpQuery query) {
        QueryWrapper<T> queryWrapper = WrapUtils.createWrapper(query);
        return this.selectList(queryWrapper);
    }

    /**
     * 根据查询条件查询列表 并排序
     */
    default List<T> selectListByQuery(MpQuery query, OrderBy orderBy) {
        QueryWrapper<T> queryWrapper = WrapUtils.createWrapper(query);
        WrapUtils.setOrderBy(queryWrapper, orderBy);
        return this.selectList(queryWrapper);
    }

    /**
     * 根据字段查询
     */
    default <R> T selectOneByColumn(Column<T, R> colum, R value) {
        QueryWrapper<T> wrapper = WrapUtils.createWrapper(colum, value);
        List<T> ts = this.selectList(wrapper);
        return helper().selectOne(ts);
    }

    /**
     * 根据查询条件查询第一条记录， 需要排序字段
     */
    default T selectFirstByWrapper(Wrapper<T> queryWrapper, OrderBy orderBy) {
        return helper().selectFirstByWrapper(queryWrapper, orderBy);
    }

    /**
     * 根据查询条件查询第一条记录
     */
    default T selectFirst(Page<T> page, Wrapper<T> queryWrapper) {
        return helper().selectFirst(page, queryWrapper);
    }

    /**
     * 根据查询条件查询第一条记录
     */
    default T selectFirst(MpQuery query, OrderBy orderBy) {
        QueryWrapper<T> queryWrapper = WrapUtils.createWrapper(query);
        return helper().selectFirstByWrapper(queryWrapper, orderBy);
    }

    /**
     * 根据查询条件查询第一条记录
     */
    default <R> T selectFirst(Column<T, R> column, R value, OrderBy orderBy) {
        QueryWrapper<T> queryWrapper = WrapUtils.createWrapper(column, value);
        return helper().selectFirstByWrapper(queryWrapper, orderBy);
    }

}
