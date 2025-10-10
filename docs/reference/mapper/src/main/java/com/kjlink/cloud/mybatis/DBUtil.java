package com.kjlink.cloud.mybatis;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import com.kjlink.cloud.mybatis.impl.ModifyAttrsRecordProxyFactory;
import com.kjlink.cloud.mybatis.impl.UpdateWrapperBuilder;
import com.kjlink.cloud.mybatis.tsid.TsIdentifierGenerator;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-08-28
 */
public class DBUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DBUtil.class);
    private static Map<Class<?>, Object> crudMapperCache = new ConcurrentHashMap<>();

    /**
     * 创建一个实体类动态代理，当你调用setXXX方法时，自动构建UpdateWrapper，
     * 最主要的特点是支持set null值
     * <code><pre>
     *     User wrap = DBUtil.updateWrap(User.class);
     *     wrap.setId("123");
     *     wrap.setEmail("456");
     *     wrap.setFullName(null);
     *     mapper.update(wrap);
     * </pre></code>
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public static <T> T updateWrap(Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass为空");
        return ModifyAttrsRecordProxyFactory.getInstance().get(entityClass);
    }


    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    public static <T> T selectById(Class<T> entityClass, Serializable id) {
        Assert.notNull(entityClass, "entityClass为空");
        return mapper(entityClass).selectById(id);
    }

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
    public static <T> List<T> selectByIds(Class<T> entityClass, Collection<? extends Serializable> idList) {
        Assert.notNull(entityClass, "entityClass为空");
        return mapper(entityClass).selectByIds(idList);
    }

    /**
     * 根据ID 批量查询
     *
     * @param idArray
     * @param <T>
     * @return
     */
    public static <T> List<T> selectByIds(Class<T> entityClass, String[] idArray) {
        Assert.notNull(entityClass, "entityClass为空");
        return mapper(entityClass).selectByIds(idArray);
    }

    /**
     * 插入一条记录，如果实体类的id为空，mybatis会自动设置一个id
     *
     * @param entity 实体对象
     * @param <T>    实体类型
     */
    public static <T> int insert(T entity) {
        Assert.notNull(entity, "要插入的实体为空");
        Class<T> entityClass = getEntityClass(entity);
        return mapper(entityClass).insert(entity);
    }

    /**
     * 执行批量插入，性能比循环插入快，适合大批量导入数据
     *
     * @param entityList
     * @param <T>        实体类型
     * @return
     */
    public static <T> int insertBatch(Collection<T> entityList) {
        if (CollUtil.isEmpty(entityList)) {
            return 0;
        }
        Class<T> entityClass = getEntityClassByCollection(entityList);
        return mapper(entityClass).insertBatch(entityList);
    }

    /**
     * 保存实体(insert或update)<br>
     * update仅更新非空字段，修改人和修改时间总是更新
     *
     * @param entity
     * @return 受影响的行数
     */
    public static <T> int save(T entity) {
        Assert.notNull("entity", "entity为空");
        Class<T> entityClass = getEntityClass(entity);
        return mapper(entityClass).save(entity);
    }

    /**
     * 批量保存实体(insert或update)
     *
     * @param entityList
     * @return
     */
    public static <T> int saveBatch(Collection<T> entityList) {
        if (CollUtil.isEmpty(entityList)) {
            return 0;
        }
        Class<T> entityClass = getEntityClassByCollection(entityList);
        return mapper(entityClass).saveBatch(entityList);
    }

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    public static <T> int deleteById(Class<T> entityClass, String id) {
        Assert.notNull(entityClass, "entityClass为空");
        Assert.notNull(id, "id为空");
        return mapper(entityClass).deleteById(id);
    }

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param idArray 主键ID列表或实体列表
     */
    public static <T> int deleteByIds(Class<T> entityClass, String[] idArray) {
        Assert.notNull(entityClass, "entityClass为空");
        return mapper(entityClass).deleteByIds(idArray);
    }

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param entityClass
     * @param idCollection
     * @param <T>
     * @return
     */
    public static <T> int deleteByIds(Class<T> entityClass, Collection<? extends Serializable> idCollection) {
        Assert.notNull(entityClass, "entityClass为空");
        return mapper(entityClass).deleteByIds(idCollection);
    }

    /**
     * 当对象为普通实体时，update所有字段；
     * 当对象为{@linkplain #updateWrap(Class)}代理对象时，执行根据id update
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> int update(T entity) {
        Assert.notNull(entity, "entity为空");
        //代理实体
        if (entity instanceof UpdateWrapperBuilder<?> uwb) {
            Class entityClass = uwb.getEntityClass();
            return mapper(entityClass).update(entity);
        }
        //普通实体
        Class<T> entityClass = getEntityClass(entity);
        return mapper(entityClass).update(entity);
    }

    private static <T> Class<T> getEntityClass(T entity) {
        return (Class<T>) entity.getClass();
    }

    private static <T> Class<T> getEntityClassByCollection(Collection<T> entityList) {
        T first = CollUtil.getFirst(entityList);
        return getEntityClass(first);
    }


    /**
     * 根据实体类获取EntityMapper
     *
     * @param entityClass
     * @return
     */
    public static <T> CrudMapper<T> mapper(Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass为空");
        Object crudMapper = crudMapperCache.get(entityClass);
        if (crudMapper == null) {
            //第一次进入
            if (crudMapperCache.isEmpty()) {
                initCrudMapperCache();
                crudMapper = crudMapperCache.get(entityClass); //再次获取
            }
            if (crudMapper == null) {
                throw new MybatisPlusException(
                        StrUtil.format("找不到实体{}对应的CrudMapper", entityClass.getSimpleName()));
            }
        }
        return (CrudMapper<T>) crudMapper;
    }

    private static synchronized void initCrudMapperCache() {
        String[] names = SpringUtil.getBeanNamesForType(CrudMapper.class);
        for (String name : names) {
            Object bean = SpringUtil.getBean(name);
            try {
                Class<?> entityClass =
                        ReflectionKit.getSuperClassGenericType(bean.getClass(), CrudMapper.class, 0);
                crudMapperCache.put(entityClass, bean);
            } catch (Exception ex) {
                LOG.error("读取CrudMapper对应实体异常", ex);
            }
        }
    }

    /**
     * 复制分页结果为指定的类型
     *
     * @param pages
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> Page<T> copy(Page<?> pages, Class<T> targetClass) {
        Page<T> resultPage = new Page<>(pages.getCurrent(), pages.getSize(), pages.getTotal(), pages.searchCount());
        List<T> copyList = copy(pages.getRecords(), targetClass);
        resultPage.setRecords(copyList);
        return resultPage;
    }

    /**
     * 拷贝数据库实体为普通POJO
     *
     * @param originRecords
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> List<T> copy(@Nullable Collection<?> originRecords, Class<T> targetClass) {
        List<T> copyList = new ArrayList<>();
        if (originRecords != null) {
            for (Object record : originRecords) {
                copyList.add(copy(record, targetClass));
            }
        }
        return copyList;
    }

    /**
     * 复制数据库对象为普通POJO
     *
     * @param origin
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T copy(Object origin, Class<T> targetClass) {
        return BeanUtil.copyProperties(origin, targetClass);
    }

    /**
     * 截断长字符串，使得适合数据库字段长度限制
     *
     * @param str
     * @param maxBytes
     * @return
     */
    public static String truncateVarchar(String str, int maxBytes) {
        //utf-8编码最多占4个字节
        if (str == null || str.length() * 4 <= maxBytes) {
            return str;
        }
        Charset charset = StandardCharsets.UTF_8;
        byte[] sba = str.getBytes(charset);
        if (sba.length <= maxBytes) {
            return str;
        }
        //省略号
        String omit = "...";
        // Ensure truncation by having byte buffer = maxBytes
        int limitBytes = maxBytes - omit.getBytes(charset).length;
        ByteBuffer bb = ByteBuffer.wrap(sba, 0, limitBytes);
        CharBuffer cb = CharBuffer.allocate(limitBytes);
        // Ignore an incomplete character
        CharsetDecoder decoder = charset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        decoder.decode(bb, cb, true);
        decoder.flush(cb);
        cb.append(omit);
        return new String(cb.array(), 0, cb.position());
    }

    /**
     * 生成随机ID （雪花id）
     *
     * @return
     */
    public static long nextLongId() {
        return (long) TsIdentifierGenerator.INSTANCE.nextId(null);
    }

    /**
     * 生成随机ID （TSID）
     *
     * @return
     */
    public static String nextId() {
        return TsIdentifierGenerator.INSTANCE.nextUUID(null);
    }
}
