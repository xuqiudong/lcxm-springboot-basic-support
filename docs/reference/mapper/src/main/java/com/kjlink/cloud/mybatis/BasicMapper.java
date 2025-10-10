package com.kjlink.cloud.mybatis;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

/**
 * 从mbp的BaseMapper拷贝，增加了两种update方法
 * 去掉了default方法，能增加Mapper启动速度
 * 业务系统中的Mapper不需要继承这个接口，本包会根据扫描到的实体类自动生成子类接口。
 *
 * @author Fulai
 * @since 2024-01-12
 */
public interface BasicMapper<T> extends Mapper<T> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    int insert(T entity);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    int deleteById(Serializable id);

    /**
     * 根据实体(ID)删除
     *
     * @param entity 实体对象
     * @since 3.4.4
     */
    int deleteById(T entity);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    int delete(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param idList 主键ID列表或实体列表(不能为 null 以及 empty)
     * @since 3.5.7
     */
    int deleteByIds(@Param(Constants.COLL) Collection<?> idList);

    /**
     * 根据 ID update 非null字段，空字符串正常更新<br>
     * 忽略全局配置项mybatis-plus.global-config.db-config.update-strategy<br>
     * 且无视字段&#64;TableField(updateStrategy)的任何策略<br>
     * 正常执行字段填充(例如修改人和修改时间字段）<br>
     *
     * @param entity 实体对象
     * @return
     */
    int updatePartial(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 ID update所有字段，包括null和空字符
     * 忽略全局配置项mybatis-plus.global-config.db-config.update-strategy<br>
     * 且无视字段&#64;TableField(updateStrategy)的任何策略<br>
     * 正常执行字段填充(例如修改人和修改时间字段）<br>
     *
     * @param entity 实体对象
     * @return
     */
    int updateAll(@Param(Constants.ENTITY) T entity);

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
    int update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> updateWrapper);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T selectById(Serializable id);

    /**
     * 根据id查询时包含Lob字段
     * Lob字段默认应使用@TableField(select = false）注解排除在一般查询语句中
     *
     * @param id
     * @return
     */
    T selectByIdWithLob(Serializable id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    List<T> selectByIds(@Param(Constants.COLL) Collection<? extends Serializable> idList);

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    Long selectCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @since 3.5.3.2
     */
    List<T> selectList(IPage<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     * <p>注意： 只返回第一个字段的值</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<Object> selectObjs(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    List<Map<String, Object>> selectMaps(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 使用全局默认策略：mybatis-plus.global-config.db-config.update-strategy
     *
     * @param entity
     * @return
     * @deprecated 计划删除
     */
    @Deprecated(forRemoval = true)
    int updateById(@Param(Constants.ENTITY) T entity);

    /**
     * @deprecated 改名
     */
    @Deprecated(forRemoval = true)
    default int updateByIdSelective(@Param(Constants.ENTITY) T entity) {
        return updatePartial(entity);
    }

    /**
     * @deprecated 改名
     */
    @Deprecated(forRemoval = true)
    default int updateByIdWhole(@Param(Constants.ENTITY) T entity) {
        return updateAll(entity);
    }
}
