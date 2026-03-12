package cn.xuqiudong.basic.mybatisplus.service;

import cn.xuqiudong.basic.core.exception.CommonException;
import cn.xuqiudong.basic.core.lookup.Lookup;
import cn.xuqiudong.basic.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述: 所有mapper的通用操作汇总：可以根据model class 获取对应的mapper，然后调用BaseMapper的通用数据库操作
 *  在需要的地方手动注册此bean到spring，比如@Bean
 * @author Vic.xu
 * @since 2023-10-16 14:55
 */
public class CommonBaseMapperService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonBaseMapperService.class);
    /**
     * 注入全部的BaseMapper 的子接口
     */
    @Autowired
    private List<BaseMapper<?>> mappers;

    private Map<Class<?>, BaseMapper<?>> mapperMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("all BaseMapper size is {}", mappers.size());
        mapperMap = new HashMap<>(16);
        for (BaseMapper<?> mapper : mappers) {
            Class<?> modelGeneric = getModelGenericFromMapper(mapper);
            if (mapperMap.containsKey(modelGeneric)) {
                LOGGER.warn("Spring中包含关于{}对象的BaseMapper超过一个，请注意检查", modelGeneric.getName());
                continue;
            }
            mapperMap.putIfAbsent(modelGeneric, mapper);
        }
        LOGGER.info("All BaseMapper no duplicate size is {}", mapperMap.size());
    }

    /**
     *  通过Mapper获取mapper上的model泛型
     * @param mapper spring里注入的mapper，实际上为 MapperProxy,需要通过它获取到真实的mapper接口类，然后获取mapper上的泛型
     * @see MapperProxy
     * @return model  generic
     */
    private Class<?> getModelGenericFromMapper(BaseMapper<?> mapper) {
        Type[] genericInterfaces = mapper.getClass().getGenericInterfaces();
        //MapperProxy 上的泛型记为mapper 接口类型
        Class<?> realMapperClass = (Class<?>) genericInterfaces[0];
        Type[] types = realMapperClass.getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[0];
        Type type = parameterizedType.getActualTypeArguments()[0];
        return (Class<?>) type;
    }

    @SuppressWarnings("unchecked")
    public <T> BaseMapper<T> getMapper(Class<T> modelClass) {
        if (!mapperMap.containsKey(modelClass)) {
            throw new CommonException("系统中不存在" + modelClass.getName() + "对象队形的Mapper!");
        }
        return (BaseMapper<T>) mapperMap.get(modelClass);
    }

    /* ****************** 重写BaseMapper中的相关方法***************************************/

    /**
     * 查询列表
     * @param lookup query condition
     * @return list
     */
    public <T> List<T> list(Lookup lookup, Class<T> modelClass) {
        return getMapper(modelClass).list(lookup);
    }

    /**
     * 根据主键id查询对象
     * @param id identity
     * @return object
     */
    public <T> T findById(@Param("id") int id, Class<T> modelClass) {
        return getMapper(modelClass).findById(id);
    }

    /**
     * 插入对象
     * @param entity  entity
     * @return number of record
     */
    public <T> int insert(T entity, Class<T> modelClass) {
        return getMapper(modelClass).insert(entity);
    }

    /**
     * 批量新增，可在service层限制每次插入的数量
     * @param list list of entity
     * @return number of record
     */
    public <T> int batchInsert(List<T> list, Class<T> modelClass) {
        return getMapper(modelClass).batchInsert(list);
    }

    /**
     * 更新数据
     * @param entity  entity
     * @return number of record
     */
    public <T> int update(T entity, Class<T> modelClass) {
        return getMapper(modelClass).update(entity);
    }

    /**
     * 批量删除
     * @param ids id array
     * @return number of record
     */
    public <T> int delete(int[] ids, Class<T> modelClass) {
        return getMapper(modelClass).delete(ids);
    }

    /**
     * 批量获取
     * @param ids id array
     * @return list
     */
    public <T> List<T> findByIds(int[] ids, Class<T> modelClass) {
        return getMapper(modelClass).findByIds(ids);
    }

    /**
     * 查询列字段是否没有重复:
     * @param id: 如果不传 则判断表里的全部项,如果传了id,则排除当前id所对应的列
     * @param value 需要判断是否重复的列
     * @param column 列名称
     * @return if repeat
     */
    public <T> boolean checkNotRepeat(Integer id, @Param("value") String value, @Param("column") String column, Class<T> modelClass) {
        return getMapper(modelClass).checkNotRepeat(id, value, column);
    }

}
