package com.kjlink.cloud.mybatis.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;

import com.kjlink.cloud.dto.BeanUtil;
import com.kjlink.cloud.mybatis.BasicMapper;
import com.kjlink.cloud.mybatis.GenericCrudMapper;
import com.kjlink.cloud.mybatis.MergeHandler;
import com.kjlink.cloud.mybatis.dataperm.DataPermissionUtil;
import com.kjlink.cloud.mybatis.entity.BaseEntity;
import com.kjlink.cloud.mybatis.injector.UpdateAll;
import com.kjlink.cloud.mybatis.injector.UpdatePartial;
import com.kjlink.cloud.mybatis.query.Column;
import com.kjlink.cloud.mybatis.query.ColumnUtil;
import com.kjlink.cloud.mybatis.query.OrderBy;
import com.kjlink.cloud.mybatis.query.Where;

import static com.kjlink.cloud.mybatis.query.ColumnUtil.safeColumn;

/**
 * 将复杂的增删改查方法写在这里
 *
 * @author Fulai
 * @since 2025-07-01
 */
public class GenericCrudMapperHelper<ID extends Serializable, T> {
    private static final Logger LOG = LoggerFactory.getLogger(GenericCrudMapperHelper.class);
    private static final Map<GenericCrudMapper, GenericCrudMapperHelper> HELPER_MAP = new IdentityHashMap<>();
    private static final int DEFAULT_BATCH_SIZE = 500;
    private final BasicMapper<T> mapper;
    private final Class<?> mapperClass;
    private final Class<?> entityClass;
    private final Log log;
    private final TableInfo tableInfo;
    private final String keyProperty;
    private OrderBy orderByCreateTimeDesc;
    private OrderBy orderByUpdateTimeDesc;
    private static final String[] CREATE_TIME = {"createTime", "createdTime"};
    private static final String[] UPDATE_TIME = {"updateTime", "updatedTime"};
    private static final Object LOCK = new Object();

    private GenericCrudMapperHelper(GenericCrudMapper<ID, T> mapper) {
        this.mapper = mapper;
        this.mapperClass = AopProxyUtils.proxiedUserInterfaces(mapper)[0];
        this.entityClass = ReflectionKit.getSuperClassGenericType(mapperClass, BasicMapper.class, 0);
        this.log = LogFactory.getLog(mapperClass);

        this.tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. " + "because can not find cache of TableInfo for entity!");
        this.keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. " + "because can not find column for id from entity!");

        //创建时间和更新时间字段名称符合阿里java开发规范
        for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
            if (ArrayUtil.contains(CREATE_TIME, fieldInfo.getProperty())) {
                this.orderByCreateTimeDesc = OrderBy.desc(fieldInfo.getColumn());
            } else if (ArrayUtil.contains(UPDATE_TIME, fieldInfo.getProperty())) {
                this.orderByUpdateTimeDesc = OrderBy.desc(fieldInfo.getColumn());
            }
        }
    }

    public static <ID extends Serializable, T> GenericCrudMapperHelper<ID, T> get(GenericCrudMapper<ID, T> mapper) {
        GenericCrudMapperHelper helper = HELPER_MAP.get(mapper);
        if (helper == null) {
            synchronized (LOCK) {
                helper = HELPER_MAP.get(mapper);
                if (helper == null) {
                    helper = new GenericCrudMapperHelper(mapper);
                    HELPER_MAP.put(mapper, helper);
                }
            }
        }
        return helper;
    }

    public List<ID> selectIdByWrapper(QueryWrapper<T> wrapper) {
        String keyColumn = tableInfo.getKeyColumn();
        wrapper.select(keyColumn);
        List<?> obj = mapper.selectObjs(wrapper);
        return (List<ID>) obj;
    }

    public T onlyOne(List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        if (list.size() == 1) {
            return list.getFirst();
        }
        throw new TooManyResultsException("期望的记录数量为1，实际是:" + list.size());
    }

    public T selectFirstByWrapper(QueryWrapper<T> wrapper, OrderBy orderBy) {
        Page<T> page = new Page<>();
        page.setSearchCount(false);
        page.setCurrent(1);
        page.setSize(1);
        //设置排序信息
        if (orderBy != null) {
            List<OrderItem> orderItems = new LinkedList<>();
            Map<String, Boolean> columnOrders = orderBy.getColumnOrders();
            for (Map.Entry<String, Boolean> entry : columnOrders.entrySet()) {
                if (entry.getValue()) {
                    orderItems.add(OrderItem.asc(entry.getKey()));
                } else {
                    orderItems.add(OrderItem.desc(entry.getKey()));
                }
            }
            page.setOrders(orderItems);
        }
        return selectFirst(page, wrapper);
    }

    private T selectFirst(Page<T> page, QueryWrapper<T> wrapper) {
        List<T> list = mapper.selectList(page, wrapper);
        if (CollUtil.isNotEmpty(list)) {
            return list.getFirst();
        }
        return null;
    }

    public OrderBy getOrderByCreateTimeDesc() {
        Assert.notNull(orderByCreateTimeDesc, "当前实体不支持按创建时间排序");
        return orderByCreateTimeDesc;
    }

    public OrderBy getOrderByUpdateTimeDesc() {
        Assert.notNull(orderByUpdateTimeDesc, "当前实体不支持按更新时间排序");
        return orderByUpdateTimeDesc;
    }

    public Object selectAggregation(String op, Column<T, ?> column, Where where) {
        QueryWrapper<T> query = WrapUtil.createQueryWrapper(where);
        String col1 = op + "(" + ColumnUtil.safeColumn(column) + ")";
        query.select(col1);
        List<Object> objList = mapper.selectObjs(query);
        if (CollUtil.isEmpty(objList)) {
            return null;
        }
        return objList.getFirst();
    }

    public <A extends Number, B extends Number> Optional<BigDecimal[]> selectSum(Column<T, A> sumColumn1,
            Column<T, B> sumColumn2, Where where) {
        QueryWrapper<T> query = WrapUtil.createQueryWrapper(where);
        String col1 = "sum(" + ColumnUtil.safeColumn(sumColumn1) + ")";
        String col2 = "sum(" + ColumnUtil.safeColumn(sumColumn2) + ")";
        query.select(col1, col2);
        List<Map<String, Object>> objList = mapper.selectMaps(query);
        if (CollUtil.isEmpty(objList)) {
            return Optional.empty();
        }
        Map<String, Object> row = objList.getFirst();
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(new BigDecimal[]{(BigDecimal) row.get(col1), (BigDecimal) row.get(col2)});
    }

    public <A extends Number, B extends Number, C extends Number> Optional<BigDecimal[]> selectSum(
            Column<T, A> sumColumn1, Column<T, B> sumColumn2, Column<T, C> sumColumn3, Where where) {
        QueryWrapper<T> query = WrapUtil.createQueryWrapper(where);
        String col1 = "sum(" + ColumnUtil.safeColumn(sumColumn1) + ")";
        String col2 = "sum(" + ColumnUtil.safeColumn(sumColumn2) + ")";
        String col3 = "sum(" + ColumnUtil.safeColumn(sumColumn3) + ")";

        query.select(col1, col2, col3);
        List<Map<String, Object>> objList = mapper.selectMaps(query);
        if (CollUtil.isEmpty(objList)) {
            return Optional.empty();
        }
        Map<String, Object> row = objList.getFirst();
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(
                new BigDecimal[]{(BigDecimal) row.get(col1), (BigDecimal) row.get(col2), (BigDecimal) row.get(col3)});
    }

    public <G, A> Map<G, BigDecimal> selectSumGroupBy(Column<T, A> sumColumn1, Where where, Column<T, G> groupBy) {
        QueryWrapper<T> query = WrapUtil.createQueryWrapper(where);
        String groupCol = ColumnUtil.safeColumn(groupBy);
        String sumCol = "sum(" + ColumnUtil.safeColumn(sumColumn1) + ")";
        query.select(groupCol, sumCol);
        query.groupBy(groupCol);
        List<Map<String, Object>> rowList = mapper.selectMaps(query);
        Map<G, BigDecimal> resultMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rowList) {
            if (row != null) {
                resultMap.put((G) row.get(groupCol), (BigDecimal) row.get(sumCol));
            }
        }
        return resultMap;
    }


    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod 方法名
     * @return 命名id
     * @since 3.4.0
     */
    private String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(mapperClass, sqlMethod);
    }

    public int insertBatch(Collection<T> entityList) {
        if (CollUtil.isEmpty(entityList)) {
            return 0;
        }

        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        SqlSessionFactory factory = getSqlSessionFactory();
        SqlHelper.executeBatch(factory, log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            int ret = sqlSession.insert(sqlStatement, entity);
            if (ret < 1) {
                LOG.warn("insert result: {}", ret);
            }
        });

        return entityList.size();
    }

    private volatile SqlSessionFactory sqlSessionFactory;

    protected SqlSessionFactory getSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(mapper);
            this.sqlSessionFactory = MybatisUtils.getSqlSessionFactory(mybatisMapperProxy);
        }
        return this.sqlSessionFactory;
    }

    private String getUpdateByIdStatement(boolean updateNullFields) {
        if (updateNullFields) {
            return mapperClass.getName() + "." + UpdateAll.METHOD_NAME;
        }
        return mapperClass.getName() + "." + UpdatePartial.METHOD_NAME;
    }

    public int updateBatch(Collection<T> entityList, boolean updateNullFields) {
        if (CollUtil.isEmpty(entityList)) {
            return 0;
        }

        String sqlStatement = getUpdateByIdStatement(updateNullFields);
        SqlSessionFactory factory = getSqlSessionFactory();
        SqlHelper.executeBatch(factory, log, entityList, DEFAULT_BATCH_SIZE, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            int ret = sqlSession.update(sqlStatement, param);
            if (ret < 1) {
                LOG.warn("update result: {}", ret);
            }
        });
        return 0;
    }

    public <A, B, C> int updateColumnById(ID id, Column<T, A> set1, A value1, Column<T, B> set2, B value2,
            Column<T, C> set3, C value3) {
        cn.hutool.core.lang.Assert.notNull(id, "id为空");

        UpdateWrapper<T> wrapper = WrapUtil.updateSet(set1, value1, set2, value2, set3, value3);
        String keyColumn = tableInfo.getKeyColumn();
        wrapper.eq(keyColumn, id);
        return mapper.update(null, wrapper);
    }

    public <A, B, C> int updateColumnByIds(Collection<ID> idCollection, Column<T, A> set1, A value1, Column<T, B> set2,
            B value2, Column<T, C> set3, C value3) {
        if (CollUtil.isEmpty(idCollection)) {
            return 0;
        }
        UpdateWrapper<T> wrapper = WrapUtil.updateSet(set1, value1, set2, value2, set3, value3);
        String keyColumn = tableInfo.getKeyColumn();
        wrapper.in(keyColumn, idCollection);
        return mapper.update(null, wrapper);
    }

    private Object getId(Object entity) {
        //避免反射，提高性能
        if (entity instanceof BaseEntity) {
            return ((BaseEntity) entity).getId();
        }
        //tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty())
        return tableInfo.getPropertyValue(entity, keyProperty);
    }

    public int updateById(T entity, boolean updateNullFields) {
        cn.hutool.core.lang.Assert.notNull(entity, "要更新的实体为空");
        if (updateNullFields) {
            return mapper.updateAll(entity);
        }
        return mapper.updatePartial(entity);
    }

    public int save(T entity, boolean updateNullFields) {
        cn.hutool.core.lang.Assert.notNull("entity", "entity为空");

        Object idVal = getId(entity);
        //id非空，且不是空字符串
        if (StringUtils.checkValNotNull(idVal)) {
            //优先尝试update
            int ret = this.updateById(entity, updateNullFields);
            if (ret > 0) {
                return ret;
            }
        }
        //不存在时或update为0时，执行insert
        return mapper.insert(entity);
    }

    public int saveBatch(Collection<T> entityList, boolean updateNullFields) {
        if (CollUtil.isEmpty(entityList)) {
            return 0;
        }
        String selectStatement = getSqlStatement(SqlMethod.SELECT_BY_ID);
        String updateStatement = getUpdateByIdStatement(updateNullFields);
        SqlSessionFactory factory = getSqlSessionFactory();
        SqlHelper.saveOrUpdateBatch(factory, this.mapperClass, this.log, entityList, DEFAULT_BATCH_SIZE,
                (sqlSession, entity) -> {
                    Object idVal = getId(entity);
                    return StringUtils.checkValNull(idVal) ||
                           CollectionUtils.isEmpty(sqlSession.selectList(selectStatement, entity));
                }, (sqlSession, entity) -> {
                    MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    int ret = sqlSession.update(updateStatement, param);
                    if (ret < 1) {
                        LOG.warn("update result: {}", ret);
                    }
                });

        return entityList.size();
    }

    public int deleteBatch(Collection<T> entityList) {
        if (CollUtil.isEmpty(entityList)) {
            return 0;
        }
        List<Object> ids = CollUtil.map(entityList, this::getId, true);
        return mapper.deleteByIds(ids);
    }

    public <M> int saveDetails(Collection<T> newDetailList, Column<T, M> setMasterId, M masterId) {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        queryWrapper.eq(safeColumn(setMasterId), masterId);

        //要保存的记录为空，则直接删除数据库中的记录，无需合并操作。
        if (CollUtil.isEmpty(newDetailList)) {
            return mapper.delete(queryWrapper);
        }

        List<T> entities = mapper.selectList(queryWrapper);

        int ret = mergeList(newDetailList, entities, new MergeHandler<>() {

            @Override
            public boolean match(@NonNull T entity, @NonNull T record) {
                Object dbId = getId(entity);
                Object voId = getId(record);
                return ObjectUtil.equal(dbId, voId);
            }

            @Override
            public boolean update(@NonNull T entity, @NonNull T record) {
                setMasterId.accept(record, masterId);
                BeanUtil.copyProperties(record, entity);
                return true;
            }

            @Override
            public T create(@NonNull T record) {
                setMasterId.accept(record, masterId);
                return record;
            }
        });
        return ret;
    }

    public <R> int mergeList(Collection<R> newRecords, Collection<T> entities, MergeHandler<T, R> mergeHandler) {
        if (entities == null) {
            entities = Collections.emptyList();
        }

        //新集合为空，删除数据库全部记录
        if (CollUtil.isEmpty(newRecords)) {
            return CollUtil.isEmpty(entities) ? 0 : this.deleteBatch(entities);
        }

        //分组检查
        List<T> updateList = new LinkedList<>();
        List<T> insertList = new LinkedList<>();
        List<T> deleteList = new LinkedList<>();
        List<R> processedList = new ArrayList<>();
        //更新数据库记录（删除+修改）
        for (T entity : entities) {
            R matchRecord = null;
            for (R record : newRecords) {
                if (mergeHandler.match(entity, record)) {
                    matchRecord = record;
                    break;
                }
            }
            if (matchRecord == null) {
                //不存在的执行删除
                deleteList.add(entity);
            } else {
                if (mergeHandler.update(entity, matchRecord)) {
                    //有更新
                    updateList.add(entity);
                }
                //记录已经处理了
                processedList.add(matchRecord);
            }
        }

        //检查新增记录
        for (R record : newRecords) {
            boolean exists = false;
            for (R processed : processedList) {
                //这里可以使用==判断，因为是同一个对象
                if (processed == record) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                T newEntity = mergeHandler.create(record);
                if (newEntity != null) {
                    insertList.add(newEntity);
                }
            }
        }

        int total = 0;
        total += this.insertBatch(insertList);
        total += this.updateBatch(updateList, true);
        total += this.deleteBatch(deleteList);
        return total;
    }

    public void setPermission(String permissionSql) {
        DataPermissionUtil.enable(tableInfo.getTableName(), permissionSql);
    }

    public int updateById(T updateWrap) {
        if (updateWrap == null) {
            return 0;
        }
        //代理对象
        if (updateWrap instanceof UpdateWrapperBuilder uwb) {
            UpdateWrapper wrapper = uwb.getWrapper();
            //为安全起见，禁止不带where条件的update
            String whereSql = wrapper.getCustomSqlSegment();
            Assert.notEmpty(whereSql, "为安全起见禁止不带where条件的update语句");
            return mapper.update(null, wrapper);
        }
        //普通对象，执行全字段update
        return this.updateById(updateWrap, true);
    }

    public int updateColumnByIds(Collection<ID> idCollection, T updateWrap) {
        Assert.isTrue(updateWrap instanceof UpdateWrapperBuilder<?>, "updateWrap必须为字段更新处理器");
        UpdateWrapper wrapper = ((UpdateWrapperBuilder) updateWrap).getWrapper();
        wrapper.in(tableInfo.getKeyColumn(), idCollection);
        return mapper.update(null, wrapper);
    }

    public int updateByWhere(T updateWrap, Where where) {
        Assert.isTrue(updateWrap instanceof UpdateWrapperBuilder<?>, "updateWrap必须为字段更新处理器");
        UpdateWrapper<T> wrapper = ((UpdateWrapperBuilder<T>) updateWrap).getWrapper();
        where.apply(wrapper);
        return mapper.update(null, wrapper);
    }

}
