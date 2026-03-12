package cn.xuqiudong.basic.mybatisplus.helper;

import cn.xuqiudong.basic.mybatisplus.entity.BaseMpEntity;
import cn.xuqiudong.basic.mybatisplus.mapper.MpGenericMapper;
import cn.xuqiudong.basic.mybatisplus.query.OrderBy;
import cn.xuqiudong.basic.mybatisplus.util.WrapUtils;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * 描述:
 * mybatis-plus 帮助类
 *
 * @author Vic.xu
 * @see MpGenericMapper
 * @since 2025-10-28 17:57
 */
public class MpGenericMapperHelper<ID extends Serializable, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MpGenericMapperHelper.class);


    private static final int DEFAULT__BATCH_SIZE = 500;
    /**
     * 缓存 每个MpGenericMapper  对应一个 MpGenericMapperHelper
     */
    private static final Map<MpGenericMapper, MpGenericMapperHelper> HELPER_MAP = new IdentityHashMap<>();

    /**
     * 当前 MpGenericMapperHelper 对应的 MpGenericMapper
     */
    private final BaseMapper<T> mapper;

    private final Class<?> mapperClass;

    private Class<?> entityClass;

    private final Log log;

    private final TableInfo tableInfo;

    private final String keyProperty;

    private volatile SqlSessionFactory sqlSessionFactory;

    public MpGenericMapperHelper(MpGenericMapper<ID, T> mpGenericMapper) {
        this.mapper = mpGenericMapper;
        this.mapperClass = AopProxyUtils.proxiedUserInterfaces(mapper)[0];
        this.entityClass = ReflectionKit.getSuperClassGenericType(mapperClass, BaseMapper.class, 0);
        this.log = LogFactory.getLog(mapperClass);
        this.tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "无法获取 " + entityClass.getName() + " 的数据库表映射信息!");
        this.keyProperty = tableInfo.getKeyProperty();
        MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(mapper);
        this.sqlSessionFactory = MybatisUtils.getSqlSessionFactory(mybatisMapperProxy);
    }

    public static <ID extends Serializable, T> MpGenericMapperHelper<ID, T> getHelper(MpGenericMapper<ID, T> mpGenericMapper) {
        MpGenericMapperHelper<ID, T> helper = HELPER_MAP.get(mpGenericMapper);
        if (helper == null) {
            synchronized (MpGenericMapperHelper.class) {
                helper = new MpGenericMapperHelper<>(mpGenericMapper);
                HELPER_MAP.put(mpGenericMapper, helper);
            }
        }
        return helper;
    }

    public int save(T entity) {
        Assert.notNull(entity, "entity can not be null");
        Object id = findId(entity);
        // id 不为空 则更新
        if (StringUtils.checkValNotNull(id)) {
            int num = this.updateById(entity);
            if (num > 0) {
                return num;
            }
        }
        return mapper.insert(entity);
    }

    // 更新 TODO  考虑扩展  是更新非空字段还是 全部字段 包含空字段
    private int updateById(T entity) {
        return mapper.updateById(entity);
    }

    private Object findId(Object entity) {

        if (entity instanceof BaseMpEntity<?> mp) {
            return mp.getId();
        }
        return tableInfo.getPropertyValue(entity, keyProperty);
    }

    /**
     * 批量插入
     */
    public int insertBatch(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        String sql = getSqlStatement(SqlMethod.INSERT_ONE);
        SqlHelper.executeBatch(sqlSessionFactory, log, entityList, DEFAULT__BATCH_SIZE,
                (sqlSession, entity) -> {
                    // 此处返回 -2147482646
                    sqlSession.insert(sql, entity);

                });
        return entityList.size();
    }

    /**
     * 批量更新
     */
    public int updateBatch(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        String sql = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        SqlHelper.executeBatch(sqlSessionFactory, log, entityList, DEFAULT__BATCH_SIZE,
                (sqlSession, entity) -> updateSql(sqlSession, sql, entity));
        return entityList.size();
    }

    /**
     * 批量保存
     * 不额外判断id在数据库是否存在， 需要确保数据的正确性
     */
    public int saveBatch(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        String sql = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        // 只要id 为null 则新增， 否则就更新
        BiPredicate<SqlSession, T> predicate = (sqlSession, entity) -> {
            Object id = findId(entity);
            return StringUtils.checkValNull(id);
        };
        SqlHelper.saveOrUpdateBatch(sqlSessionFactory, mapperClass, log, entityList, DEFAULT__BATCH_SIZE
                , predicate,
                (sqlSession, entity) -> updateSql(sqlSession, sql, entity)
        );

        return entityList.size();
    }

    /**
     * 执行 SqlMethod.UPDATE_BY_ID 的更新逻辑
     * 如果直接使用entity作为参数：There is no getter for property named 'et' in XxxEntity
     *
     * @see com.baomidou.mybatisplus.core.injector.methods.UpdateById
     */
    private void updateSql(SqlSession sqlSession, String sql, T entity) {
        MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
        param.put(Constants.ENTITY, entity);
        sqlSession.update(sql, param);
    }


    /**
     * 获取mapperStatementId
     */
    private String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(mapperClass, sqlMethod);
    }

    /**
     * 查询单个结果
     */
    public T selectOne(List<T> ts) {
        if (CollectionUtils.isEmpty(ts)) {
            return null;
        }
        if (ts.size() == 1) {
            return ts.get(0);
        }
        throw new TooManyResultsException("期望查询一条记录, 但实际记录数为: " + ts.size());
    }


    /**
     * 根据查询条件查询第一条记录， 需要排序字段
     */
    public T selectFirstByWrapper(Wrapper<T> queryWrapper, OrderBy orderBy) {
        Page<T> page = Page.of(1, 1);
        page.setSearchCount(false);
        // 排序
        WrapUtils.setOrderBy(page, orderBy);
        return selectFirst(page, queryWrapper);
    }

    /**
     * 根据查询条件查询第一条记录
     */
    public T selectFirst(Page< T> page, Wrapper<T> queryWrapper) {
        List<T> ts = mapper.selectList(page, queryWrapper);
        if (CollectionUtils.isEmpty(ts)) {
            return null;
        }
        return ts.get(0);
    }
}
