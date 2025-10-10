package com.kjlink.cloud.mybatis;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.exceptions.TooManyResultsException;

import com.kjlink.cloud.mybatis.impl.GenericCrudMapperHelper;
import com.kjlink.cloud.mybatis.impl.WrapUtil;
import com.kjlink.cloud.mybatis.query.Column;
import com.kjlink.cloud.mybatis.query.ColumnUtil;
import com.kjlink.cloud.mybatis.query.OrderBy;
import com.kjlink.cloud.mybatis.query.PageQuery;
import com.kjlink.cloud.mybatis.query.QueryUtil;
import com.kjlink.cloud.mybatis.query.Where;

import static com.kjlink.cloud.mybatis.query.ColumnUtil.safeColumn;

/**
 * 通义单表增删改查Mapper
 *
 * @author Fulai
 * @since 2025-07-01
 */
public interface GenericCrudMapper<ID extends Serializable, T> extends BasicMapper<T> {

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
//    T selectById(ID id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    //List<T> selectByIds(Collection<ID> idList);

    /**
     * 根据ID 批量查询
     *
     * @param idArray
     * @return
     */
    default List<T> selectByIds(ID[] idArray) {
        if (ArrayUtil.isEmpty(idArray)) {
            return Collections.emptyList();
        }
        return this.selectByIds(CollUtil.newArrayList(idArray));
    }

    /**
     * 查询全部id字段
     *
     * @return
     */
    default List<ID> selectIdAll() {
        return helper().selectIdByWrapper(Wrappers.query());
    }

    /**
     * 仅查询id列
     *
     * @param column pojo的set方法lambda表达式，例如MyEntity:setName,避免写死字段名称
     * @param value  对应值
     * @return
     */
    default <A> List<ID> selectIdByColumn(Column<T, A> column, A value) {
        return this.selectIdByColumn(column, value, null, null, null, null);
    }

    /**
     * 根据where条件查询id
     *
     * @param where
     * @return
     */
    default List<ID> selectId(Where where) {
        QueryWrapper<T> queryWrapper = WrapUtil.createQueryWrapper(where);
        return helper().selectIdByWrapper(queryWrapper);
    }

    /**
     * 查询全部，不排序
     *
     * @return
     */
    default List<T> selectAll() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 查询全部并排序
     *
     * @param orderBy
     * @return
     */
    default List<T> selectAll(OrderBy orderBy) {
        QueryWrapper<T> wrapper = Wrappers.query();
        WrapUtil.setOrderBy(wrapper, orderBy);
        return this.selectList(wrapper);
    }

    /**
     * 使用wrapper进行查询
     *
     * @param wrapper
     * @return
     */
//    List<T> selectList(Wrapper<T> wrapper);

    /**
     * 使用QueryVo查询列表，一般用于数据导出场景。
     * 查询对象写法参见{@linkplain QueryUtil}的文档
     *
     * @param queryVo 内部调用QueryUtil生成Sql
     * @return
     */
    default List<T> selectList(Object queryVo) {
        Where where = Where.create(queryVo);
        return this.selectList(where);
    }

    /**
     * 根据where条件查询list，不排序、不分页
     *
     * @param where
     * @return
     */
    default List<T> selectList(Where where) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(where);
        return this.selectList((Wrapper<T>) wrapper);
    }

    /**
     * 根据where条件和order查询List
     *
     * @param where
     * @param orderBy
     * @return
     */
    default List<T> selectList(Where where, OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(where);
        WrapUtil.setOrderBy(wrapper, orderBy);
        return this.selectList(wrapper);
    }

    /**
     * 根据外键查询
     *
     * @deprecated 同selectByColumn
     */
    @Deprecated(forRemoval = true)
    default <A> List<T> selectByFk(Column<T, A> fkColumn, A fkValue) {
        return this.selectByColumn(fkColumn, fkValue);
    }

    /**
     * 根据一个列进行查询
     *
     * @param column pojo的get方法lambda表达式，例如MyEntity:getName,避免写死字段名称
     * @param value  总是=，null值生成IS NULL
     * @return
     */
    default <A> List<T> selectByColumn(Column<T, A> column, A value) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column, value);
        return this.selectList(wrapper);
    }

    /**
     * 根据两个列条件查询
     * null值生成IS NULL
     */
    default <A, B> List<T> selectByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2);
        return this.selectList(wrapper);
    }


    /**
     * 根据列进行查询并排序
     *
     * @param column  pojo的get方法lambda表达式，例如MyEntity:getName,避免写死字段名称
     * @param value   对应值
     * @param orderBy 排序字段
     * @return
     */
    default <A> List<T> selectByColumnOrderBy(Column<T, A> column, A value, OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column, value);
        WrapUtil.setOrderBy(wrapper, orderBy);
        return this.selectList(wrapper);
    }

    /**
     * 根据外键查询0或1个记录
     *
     * @deprecated 同selectOneByColumn
     */
    @Deprecated(forRemoval = true)
    default <A> T selectOneByFk(Column<T, A> fkColumn, A fkValue) {
        return this.selectOneByColumn(fkColumn, fkValue);
    }

    /**
     * 根据一个列进行查询，允许数据库存在0~1条记录，如果大于1个则抛异常
     *
     * @param column
     * @param value
     * @param <A>
     * @return
     */
    default <A> T selectOneByColumn(Column<T, A> column, A value) {
        List<T> list = this.selectByColumn(column, value);
        return helper().onlyOne(list);
    }

    /**
     * 根据Where条件查询一条记录。
     * 允许数据库存在0~1条记录，如果大于1个则抛异常
     *
     * @param where
     * @return
     */
    default T selectOne(Where where) {
        List<T> list = this.selectList(where);
        return helper().onlyOne(list);
    }

    /**
     * 根据一个列进行查询，选择第1个返回结果。内部进行了分页查询
     */
    default <A> T selectFirstByColumn(Column<T, A> column, A value, OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column, value);
        return helper().selectFirstByWrapper(wrapper, orderBy);
    }

    /**
     * 查询第一个返回结果,内部进行了分页查询
     *
     * @param where
     * @param orderBy
     * @return
     */
    default T selectFirst(Where where, OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(where);
        return helper().selectFirstByWrapper(wrapper, orderBy);
    }

    /**
     * 排序查询并获取第一个
     *
     * @param query
     * @return
     */
    default T selectFirst(PageQuery query) {
        QueryWrapper<T> wrapper = WrapUtil.build(query);
        Page<T> page = query.toPage();
        page.setSearchCount(false);
        page.setCurrent(1);
        page.setSize(1);
        List<T> list = this.selectList(page, wrapper);
        if (CollUtil.isNotEmpty(list)) {
            return list.getFirst();
        }
        return null;
    }

    /**
     * 执行 column in (?,?,?)查询语句
     *
     * @param column   pojo的set方法lambda表达式，例如MyEntity:setName,避免写死字段名称
     * @param inValues
     * @return
     */
    default <A> List<T> selectByColumnIn(Column<T, A> column, Collection<A> inValues) {
        if (CollUtil.isEmpty(inValues)) {
            return Collections.emptyList();
        }
        QueryWrapper<T> wrapper = WrapUtil.columnIn(column, inValues);
        return this.selectList(wrapper);
    }

    /**
     * 执行 column in (?,?,?)查询语句
     *
     * @param column   pojo的set方法lambda表达式，例如MyEntity:setName,避免写死字段名称
     * @param inValues 1或多个值
     * @return
     */
    default <A> List<T> selectByColumnIn(Column<T, A> column, A[] inValues) {
        if (ArrayUtil.isEmpty(inValues)) {
            return Collections.emptyList();
        }
        QueryWrapper<T> wrapper = WrapUtil.columnIn(column, CollUtil.newArrayList(inValues));
        return this.selectList(wrapper);
    }

    /**
     * 使用分页查询对象执行分页查询，一般用于前端表格查询。
     * 分页对象写法参见{@linkplain QueryUtil}的文档
     *
     * @param pageQuery 一般为PageQuery子类才有意义
     * @return
     */
    default Page<T> selectPage(PageQuery pageQuery) {
        Page<T> page = pageQuery.toPage();
        QueryWrapper<T> wrapper = WrapUtil.build(pageQuery);
        List<T> list = this.selectList(page, wrapper);
        page.setRecords(list);
        return page;
    }

    /**
     * 使用实体+分页配置进行分页查询 （不推荐）
     *
     * @param pageQuery 设置分页信息，一般为PageQuery类本身
     * @param entity    使用实体本身作为简单的查询条件
     * @return
     */
    default Page<T> selectPage(PageQuery pageQuery, T entity) {
        Page<T> page = pageQuery.toPage();
        QueryWrapper<T> wrapper = WrapUtil.build(entity);
        List<T> list = this.selectList(page, wrapper);
        page.setRecords(list);
        return page;
    }

    /**
     * 执行select count语句
     *
     * @param column 列名称
     * @param value  列值
     * @param <A>
     * @return
     */
    default <A> int selectCount(Column<T, A> column, A value) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column, value);
        return Math.toIntExact(this.selectCount(wrapper));
    }

    default int selectCount(Where where) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(where);
        return Math.toIntExact(this.selectCount(wrapper));
    }

    /**
     * 判断是否存在指定记录（select count > 0)
     *
     * @param column 列名称
     * @param value  列值
     * @param <A>
     * @return
     */
    default <A> boolean exists(Column<T, A> column, A value) {
        return selectCount(column, value) > 0;
    }

    default boolean exists(Where where) {
        return selectCount(where) > 0;
    }

    /**
     * 查询最近创建的记录。按创建日期倒排取第1个
     */
    default <A> T selectLatestCreate(Column<T, A> column, A value) {
        OrderBy orderBy = helper().getOrderByCreateTimeDesc();
        return this.selectFirstByColumn(column, value, orderBy);
    }

    //两列相等
    default <A, B> T selectLatestCreate(Column<T, A> column1, A value1, Column<T, B> column2, B value2) {
        OrderBy orderBy = helper().getOrderByCreateTimeDesc();
        return this.selectFirstByColumn(column1, value1, column2, value2, orderBy);
    }

    //三列相等
//    default <A, B, C> T selectLatestCreate(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
//            Column<T, C> column3,
//            C value3){
//        OrderBy orderBy = helper().getOrderByCreateTimeDesc();
//        return this.selectFirstByColumn(column1, value1, column2, value2, column3, value3, orderBy);
//    }

    default T selectLatestCreate(Where where) {
        OrderBy orderBy = helper().getOrderByCreateTimeDesc();
        return this.selectFirst(where, orderBy);
    }

    /**
     * 查询最近修改的记录。按修改日期倒排取第1个
     */
    default <A> T selectLatestUpdate(Column<T, A> column, A value) {
        OrderBy orderBy = helper().getOrderByUpdateTimeDesc();
        return this.selectFirstByColumn(column, value, orderBy);
    }

    //两列相等
    default <A, B> T selectLatestUpdate(Column<T, A> column1, A value1, Column<T, B> column2, B value2) {
        OrderBy orderBy = helper().getOrderByUpdateTimeDesc();
        return this.selectFirstByColumn(column1, value1, column2, value2, orderBy);
    }

    //三列相等
//    <A, B, C> T selectLatestUpdate(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
//            Column<T, C> column3,
//            C value3);

    default T selectLatestUpdate(Where where) {
        OrderBy orderBy = helper().getOrderByUpdateTimeDesc();
        return this.selectFirst(where, orderBy);
    }

    /**
     * select distinct(column)不带where
     */
    default <A> List<A> selectDistinct(Column<T, A> column) {
        QueryWrapper<T> query = Wrappers.query();
        query.select("distinct(" + ColumnUtil.safeColumn(column) + ")");
        return (List<A>) this.selectObjs(query);
    }

    /**
     * select distinct(column)带where
     */
    default <A> List<A> selectDistinct(Column<T, A> column, Where where) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(where);
        wrapper.select("distinct(" + ColumnUtil.safeColumn(column) + ")");
        return (List<A>) this.selectObjs(wrapper);
    }

    /**
     * select max(column)不带where
     */
    default <A extends Number> Optional<A> selectMax(Column<T, A> column) {
        Object val = helper().selectAggregation("max", column, null);
        return Optional.ofNullable((A) val);
    }

    /**
     * select max(column)带where
     */
    default <A extends Number> Optional<A> selectMax(Column<T, A> column, Where where) {
        Object val = helper().selectAggregation("max", column, where);
        return Optional.ofNullable((A) val);
    }

    /**
     * select min(column)不带where
     */
    default <A extends Number> Optional<A> selectMin(Column<T, A> column) {
        Object val = helper().selectAggregation("min", column, null);
        return Optional.ofNullable((A) val);
    }

    /**
     * select min(column)带where
     */
    default <A extends Number> Optional<A> selectMin(Column<T, A> column, Where where) {
        Object val = helper().selectAggregation("min", column, where);
        return Optional.ofNullable((A) val);
    }

    /**
     * 求和，不带where
     *
     * @param sumColumn 要求和的列
     * @param <A>
     * @return
     */
    default <A extends Number> Optional<BigDecimal> selectSum(Column<T, A> sumColumn) {
        Object val = helper().selectAggregation("sum", sumColumn, null);
        return Optional.ofNullable((BigDecimal) val);
    }

    default <A extends Number> Optional<BigDecimal> selectSum(Column<T, A> sumColumn, Where where) {
        Object val = helper().selectAggregation("sum", sumColumn, where);
        return Optional.ofNullable((BigDecimal) val);
    }

    default <A extends Number, B extends Number> Optional<BigDecimal[]> selectSum(Column<T, A> sumColumn1,
            Column<T, B> sumColumn2, Where where) {
        return helper().selectSum(sumColumn1, sumColumn2, where);
    }

    default <A extends Number, B extends Number, C extends Number> Optional<BigDecimal[]> selectSum(
            Column<T, A> sumColumn1, Column<T, B> sumColumn2, Column<T, C> sumColumn3, Where where) {
        return helper().selectSum(sumColumn1, sumColumn2, sumColumn3, where);
    }

    /**
     * 分组求和
     *
     * @param sumColumn1 要求和的列
     * @param where
     * @param groupBy    分组列
     * @param <A>
     * @return
     */
    default <G, A> Map<G, BigDecimal> selectSumGroupBy(Column<T, A> sumColumn1, Where where, Column<T, G> groupBy) {
        return helper().selectSumGroupBy(sumColumn1, where, groupBy);
    }

    /**
     * 根据where条件查询某一列数据
     *
     * @param column
     * @param where
     * @param <A>
     * @return
     */
    default <A> List<A> selectColumn(Column<T, A> column, Where where) {
        QueryWrapper<T> query = WrapUtil.createQueryWrapper(where);
        query.select(ColumnUtil.safeColumn(column));
        List<?> objects = this.selectObjs(query);
        return (List<A>) objects;
    }

    /**
     * 根据where条件查询第一行第一列的值。
     * 断言结果集只有0或1个
     *
     * @param column
     * @param where
     * @param <A>
     * @return
     */
    default <A> A selectSingle(Column<T, A> column, Where where) {
        List<A> list = this.selectColumn(column, where);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        if (list.size() == 1) {
            return list.getFirst();
        }
        throw new TooManyResultsException(StrUtil.format("期望的结果数为1，实际是{}", list.size()));
    }

    /**
     * 插入一条记录，如果实体类的id为空，mybatis会自动设置一个id
     *
     * @param entity 实体对象
     */
//    int insert(T entity);

    /**
     * 执行批量插入，性能比循环插入快，适合大批量导入数据
     *
     * @param entityList
     * @return
     */
    default int insertBatch(Collection<T> entityList) {
        return helper().insertBatch(entityList);
    }

    /**
     * 根据id进行批量update
     * 总是update所有字段，包括null和空字符串
     *
     * @param entityList
     * @return
     */
    default int updateBatch(Collection<T> entityList) {
        return helper().updateBatch(entityList, false);
    }

    /**
     * 根据id进行批量update，不更新值为null的字段
     * 只update非null字段（空字符串会更新）
     *
     * @param entityList
     * @return
     */
    default int updateBatchPartial(Collection<T> entityList) {
        return helper().updateBatch(entityList, false);
    }

    /**
     * 根据 ID 修改
     * 使用全局默认策略：mybatis-plus.global-config.db-config.update-strategy
     * 以及字段&#64;TableField(updateStrategy)的任何策略<br>
     *
     * @param entity 实体对象
     */
//    int updateById(T entity);

    /**
     * 执行update
     * 当参数为普通实体时，update所有字段，等同于{@linkplain #updateAll(Object)}；
     * 当参数为{@linkplain DBUtil#updateWrap(Class)}创建的代理对象时，则执行根据id进行update
     *
     * @param updateWrap 普通实体或{@linkplain DBUtil#updateWrap(Class)}创建的代理对象
     * @return
     */
    default int update(T updateWrap) {
        return helper().updateById(updateWrap);
    }

    /**
     * 使用{@linkplain DBUtil#updateWrap(Class)}创建字段更新处理器
     * 并使用Where限定条件
     */
    default int update(T updateWrap, Where where) {
        return helper().updateByWhere(updateWrap, where);
    }

    /**
     * 更新多个id的某一个字段
     */
    default <A> int updateColumnByIds(Collection<ID> idCollection, Column<T, A> set1, A value1) {
        return helper().updateColumnByIds(idCollection, set1, value1, null, null, null, null);
    }

    /**
     * 更新多个id的若干字段
     *
     * @param idCollection
     * @param updateWrap   字段更新处理器
     * @param <A>
     * @return
     */
    default <A> int updateColumnByIds(Collection<ID> idCollection, T updateWrap) {
        return helper().updateColumnByIds(idCollection, updateWrap);
    }

    /**
     * 更新多个id的某一个字段
     */
    default <A> int updateColumnByIds(ID[] idArray, Column<T, A> set1, A value1) {
        if (ArrayUtil.isEmpty(idArray)) {
            return 0;
        }
        return helper().updateColumnByIds(Arrays.asList(idArray), set1, value1, null, null, null, null);
    }

    /**
     * 更新多个id的若干字段
     *
     * @param idArray
     * @param updateWrap 字段更新处理器
     * @param <A>
     * @return
     */
    default <A> int updateColumnByIds(ID[] idArray, T updateWrap) {
        if (ArrayUtil.isEmpty(idArray)) {
            return 0;
        }
        return helper().updateColumnByIds(Arrays.asList(idArray), updateWrap);
    }

    /**
     * 使用Where条件更新列
     */
    default <A> int updateColumn(Column<T, A> set1, A value1, Where where) {
        return this.updateColumn(set1, value1, null, null, null, null, where);
    }

    /**
     * 保存实体(insert或update)，
     * 存在id时，先执行update，可选是否更新值为null的字段；
     * 如果update结果为0，仍然执行insert；
     * 总是update所有字段，包括null和空字符串
     *
     * @param entity
     * @return 受影响的行数
     */
    default int save(T entity) {
        return helper().save(entity, true);
    }

    /**
     * 保存实体(insert或update)，
     * 存在id时，先执行update，可选是否更新值为null的字段；
     * 如果update结果为0，仍然执行insert；
     * 仅update非null字段（空字符串会更新）
     *
     * @param entity
     * @return
     */
    default int savePartial(T entity) {
        return helper().save(entity, false);
    }

    /**
     * 批量保存实体(insert或update)
     * 这个方法会自己分片，因此调用的时候不需要手动分片了
     * 当存在id时，会额外执行一次select语句，因此性能不如updateBatch和insertBatch；
     * 总是update所有字段，包括null和空字符串
     *
     * @param entityList
     * @return
     */
    default int saveBatch(Collection<T> entityList) {
        return helper().saveBatch(entityList, true);
    }

    /**
     * 批量保存实体(insert或update)
     * 这个方法会自己分片，因此调用的时候不需要手动分片了
     * 当存在id时，会额外执行一次select语句，因此性能不如updateBatch和insertBatch；
     * 仅update非null字段（空字符串会更新）
     *
     * @param entityList
     * @return
     */
    default int saveBatchPartial(Collection<T> entityList) {
        return helper().saveBatch(entityList, false);
    }

    /**
     * 批量保存明细表，所有明细表记录会自动设置成相同的主表ID
     * 将根据masterId查询数据库记录，多余的记录将被删除，已存在的记录进行update，不存在的记录进行insert
     * 当执行update时，总是update所有字段
     *
     * @param newRecordList 明细表
     * @param setMasterId   明细表中设置主表id的set方法Lambda表达式，例如MyEntity::setMasterId
     * @param masterId      主表id，会自动设置到子表记录中
     * @return
     */
    default <M> int saveDetails(Collection<T> newRecordList, Column<T, M> setMasterId, M masterId) {
        return helper().saveDetails(newRecordList, setMasterId, masterId);
    }

    /**
     * 合并多条记录，包含新增、修改、删除
     *
     * @param newRecords   待保存的记录
     * @param entities     数据库已存在的记录
     * @param mergeHandler 高级合并处理器
     * @param <R>          要保存的临时对象类型
     * @return 受影响的行数
     */
    default <R> int mergeList(Collection<R> newRecords, Collection<T> entities, MergeHandler<T, R> mergeHandler) {
        return helper().mergeList(newRecords, entities, mergeHandler);
    }

    /**
     * 根据id进行批量删除
     *
     * @param entityList
     * @return
     */
    default int deleteBatch(Collection<T> entityList) {
        return helper().deleteBatch(entityList);
    }

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
//    int deleteById(ID id);

    /**
     * 根据实体(ID)删除
     *
     * @param entity 实体对象
     * @since 3.4.4
     */
//    int deleteById(T entity);

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param idArray 主键ID列表或实体列表
     */
    default int deleteByIds(ID[] idArray) {
        if (ArrayUtil.isEmpty(idArray)) {
            return 0;
        }
        return this.deleteByIds(Arrays.asList(idArray));
    }

//    int deleteByIds(Collection<ID> idCollection);

    /**
     * 根据外键进行删除（同deleteByColumn）
     *
     * @deprecated 使用deleteByColumn
     */
    @Deprecated(forRemoval = true)
    default <A> int deleteByFk(Column<T, A> fkColumn, A fkValue) {
        return this.deleteByColumn(fkColumn, fkValue);
    }

    /**
     * 根据一个或多个column进行删除，为安全起见禁止空条件
     *
     * @param column pojo的get方法lambda表达式，例如MyEntity:getName,避免写死字段名称
     * @param value  条件值总是=，null值生成IS NULL
     * @return
     */
    default <A> int deleteByColumn(Column<T, A> column, A value) {
        return this.deleteByColumn(column, value, null, null, null, null);
    }

    /**
     * 根据2列删除记录
     */
    default <A, B> int deleteByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2) {
        return this.deleteByColumn(column1, value1, column2, value2, null, null);
    }

    default <A> int deleteByColumnIn(Column<T, A> column, Collection<A> inValues) {
        if (CollUtil.isEmpty(inValues)) {
            return 0;
        }
        QueryWrapper<T> wrapper = WrapUtil.columnIn(column, inValues);
        return this.delete(wrapper);
    }

    default <A> int deleteByColumnIn(Column<T, A> column, A[] inValues) {
        if (ArrayUtil.isEmpty(inValues)) {
            return 0;
        }
        QueryWrapper<T> wrapper = WrapUtil.columnIn(column, Arrays.asList(inValues));
        return this.delete(wrapper);
    }

    /**
     * 使用where条件进行删除
     *
     * @param where
     * @return
     */
    default int delete(Where where) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(where);
        return this.delete(wrapper);
    }

    /**
     * 设置数据权限拦截sql，存储在ThreadLocal中，后续执行select语句时自动加过滤
     *
     * @param permissionSql
     */
    default GenericCrudMapper<ID, T> withPermission(String permissionSql) {
        helper().setPermission(permissionSql);
        return this;
    }

    private GenericCrudMapperHelper<ID, T> helper() {
        return GenericCrudMapperHelper.get(this);
    }

    /**
     * 根据两个列条件查询ID
     *
     * @deprecated 超过2个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B> List<ID> selectIdByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2) {
        return this.selectIdByColumn(column1, value1, column2, value2, null, null);
    }

    /**
     * 根据三个列条件查询ID
     *
     * @deprecated 超过2个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> List<ID> selectIdByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            Column<T, C> column3, C value3) {
        QueryWrapper<T> queryWrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2, column3, value3);
        return helper().selectIdByWrapper(queryWrapper);
    }

    /**
     * 根据三个列条件查询
     *
     * @deprecated 超过2个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> List<T> selectByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            Column<T, C> column3, C value3) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2, column3, value3);
        return this.selectList(wrapper);
    }


    /**
     * 根据两个列条件查询并排序
     *
     * @deprecated 超过2个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B> List<T> selectByColumnOrderBy(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2);
        WrapUtil.setOrderBy(wrapper, orderBy);
        return this.selectList(wrapper);
    }

    /**
     * 根据三个列条件查询并排序
     *
     * @deprecated 超过2个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> List<T> selectByColumnOrderBy(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            Column<T, C> column3, C value3, OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2, column3, value3);
        WrapUtil.setOrderBy(wrapper, orderBy);
        return this.selectList(wrapper);
    }


    /**
     * 根据两个列进行查询，允许数据库存在0~1条记录，如果大于1个则抛异常
     */
    default <A, B> T selectOneByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2) {
        List<T> list = this.selectByColumn(column1, value1, column2, value2);
        return helper().onlyOne(list);
    }

    /**
     * 根据三个列进行查询，允许数据库存在0~1条记录，如果大于1个则抛异常
     *
     * @deprecated 3个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> T selectOneByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            Column<T, C> column3, C value3) {
        List<T> list = this.selectByColumn(column1, value1, column2, value2, column3, value3);
        return helper().onlyOne(list);
    }


    /**
     * @deprecated 超过2个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B> T selectFirstByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2);
        return helper().selectFirstByWrapper(wrapper, orderBy);
    }

    /**
     * @deprecated 超过2个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> T selectFirstByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            Column<T, C> column3, C value3, OrderBy orderBy) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2, column3, value3);
        return helper().selectFirstByWrapper(wrapper, orderBy);
    }


    /**
     * 根据id进行批量update
     *
     * @param entityList
     * @param updateNullFields 是否更新值为null的字段
     * @return
     */
    @Deprecated(forRemoval = true)
    default int updateBatch(Collection<T> entityList, boolean updateNullFields) {
        return helper().updateBatch(entityList, updateNullFields);
    }

    /**
     * 根据 ID 修改对象
     *
     * @param entity
     * @param updateNullFields 是否更新值为null的字段。为true时更新null值字段；为false时不更新null字段
     * @return
     */
    @Deprecated(forRemoval = true)
    default int updateById(T entity, boolean updateNullFields) {
        return helper().updateById(entity, updateNullFields);
    }

    /**
     * 根据id更新某一个字段
     */
    default <A> int updateColumnById(ID id, Column<T, A> set1, A value1) {
        return helper().updateColumnById(id, set1, value1, null, null, null, null);
    }

    /**
     * 根据ID更新两列
     */
    default <A, B> int updateColumnById(ID id, Column<T, A> set1, A value1, Column<T, B> set2, B value2) {
        return this.updateColumnById(id, set1, value1, set2, value2, null, null);
    }

    /**
     * 根据ID更新三列
     *
     * @deprecated 使用{@link DBUtil#updateWrap(Class)}
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> int updateColumnById(ID id, Column<T, A> set1, A value1, Column<T, B> set2, B value2,
            Column<T, C> set3, C value3) {
        return helper().updateColumnById(id, set1, value1, set2, value2, set3, value3);
    }

    /**
     * 更新多个id的两个字段
     */
    default <A, B> int updateColumnByIds(Collection<ID> idCollection, Column<T, A> set1, A value1, Column<T, B> set2,
            B value2) {
        return this.updateColumnByIds(idCollection, set1, value1, set2, value2, null, null);
    }

    /**
     * 更新多个id的三个字段
     *
     * @deprecated 多列使用{@linkplain {@link DBUtil#updateWrap(Class)}}创建update语句
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> int updateColumnByIds(Collection<ID> idCollection, Column<T, A> set1, A value1, Column<T, B> set2,
            B value2, Column<T, C> set3, C value3) {
        return helper().updateColumnByIds(idCollection, set1, value1, set2, value2, set3, value3);
    }


    /**
     * 更新多个id的两个字段
     */
    default <A, B> int updateColumnByIds(ID[] idArray, Column<T, A> set1, A value1, Column<T, B> set2, B value2) {
        return this.updateColumnByIds(idArray, set1, value1, set2, value2, null, null);
    }

    /**
     * 更新多个id的三个字段
     *
     * @deprecated 多列使用{@linkplain {@link DBUtil#updateWrap(Class)}}创建update语句
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> int updateColumnByIds(ID[] idArray, Column<T, A> set1, A value1, Column<T, B> set2, B value2,
            Column<T, C> set3, C value3) {
        if (ArrayUtil.isEmpty(idArray)) {
            return 0;
        }
        return helper().updateColumnByIds(Arrays.asList(idArray), set1, value1, set2, value2, set3, value3);
    }


    /**
     * 根据外键更新三列
     *
     * @deprecated 多列使用{@linkplain {@link DBUtil#updateWrap(Class)}}创建update语句
     */
    @Deprecated(forRemoval = true)
    default <FK, A, B, C> int updateColumnByFk(Column<T, FK> fkColumn, FK fkValue, Column<T, A> set1, A value1,
            Column<T, B> set2, B value2, Column<T, C> set3, C value3) {
        UpdateWrapper<T> wrapper = WrapUtil.updateSet(set1, value1, set2, value2, set3, value3);
        wrapper.eq(safeColumn(fkColumn), fkValue);
        return this.update(null, wrapper);
    }


    /**
     * 使用Where条件更新列
     */
    default <A, B> int updateColumn(Column<T, A> set1, A value1, Column<T, B> set2, B value2, Where where) {
        return this.updateColumn(set1, value1, set2, value2, null, null, where);
    }

    /**
     * 使用Where条件更新列
     *
     * @deprecated 多列使用{@linkplain {@link DBUtil#updateWrap(Class)}}创建update语句
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> int updateColumn(Column<T, A> set1, A value1, Column<T, B> set2, B value2, Column<T, C> set3,
            C value3, Where where) {
        UpdateWrapper<T> wrapper = WrapUtil.updateSet(set1, value1, set2, value2, set3, value3);
        //复制条件
        where.apply(wrapper);
        return this.update(null, wrapper);
    }

    /**
     * 保存实体(insert或update)<br>
     * 存在id时，先执行update，可选是否更新值为null的字段；
     * 如果update结果为0，仍然执行insert；
     *
     * @param entity
     * @param updateNullFields 是否更新值为null的字段
     * @return 受影响的行数
     */
    @Deprecated(forRemoval = true)
    default int save(T entity, boolean updateNullFields) {
        return helper().save(entity, updateNullFields);
    }


    /**
     * 批量保存实体(insert或update)
     * 当id为空时，直接执行insert；
     * 存在id时，先查询一次数据库是否存在，如果存在就执行update，可选是否更新值为null的字段；
     * 因此性能不如insertBatch；
     * 这个方法会自己分片，因此调用的时候不需要手动分片了
     *
     * @param entityList
     * @param updateNullFields 是否更新值为null的字段
     * @return
     */
    @Deprecated(forRemoval = true)
    default int saveBatch(Collection<T> entityList, boolean updateNullFields) {
        return helper().saveBatch(entityList, updateNullFields);
    }

    /**
     * @deprecated 3个条件请使用where
     */
    @Deprecated(forRemoval = true)
    default <A, B, C> int deleteByColumn(Column<T, A> column1, A value1, Column<T, B> column2, B value2,
            Column<T, C> column3, C value3) {
        QueryWrapper<T> wrapper = WrapUtil.createQueryWrapper(column1, value1, column2, value2, column3, value3);
        return this.delete(wrapper);
    }

    /**
     * 根据外键更新一列
     *
     * @deprecated 多列使用{@linkplain {@link DBUtil#updateWrap(Class)}}创建update语句
     */
    @Deprecated(forRemoval = true)
    default <FK, A> int updateColumnByFk(Column<T, FK> fkColumn, FK fkValue, Column<T, A> set1, A value1) {
        return this.updateColumnByFk(fkColumn, fkValue, set1, value1, null, null, null, null);
    }

    /**
     * 根据外键更新两列
     *
     * @deprecated 多列使用{@linkplain {@link DBUtil#updateWrap(Class)}}创建update语句
     */
    @Deprecated(forRemoval = true)
    default <FK, A, B> int updateColumnByFk(Column<T, FK> fkColumn, FK fkValue, Column<T, A> set1, A value1,
            Column<T, B> set2, B value2) {
        return this.updateColumnByFk(fkColumn, fkValue, set1, value1, set2, value2, null, null);
    }

}
