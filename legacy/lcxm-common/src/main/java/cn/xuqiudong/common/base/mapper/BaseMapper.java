package cn.xuqiudong.common.base.mapper;

import cn.xuqiudong.common.base.lookup.Lookup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Vic.xu
 * @param <T> object class
 */
public interface BaseMapper<T> {

    /**
     * 查询列表
     * @param lookup query condition
     * @return list
     */
    List<T> list(Lookup lookup);

    /**
     * 根据主键id查询对象
     * @param id identity
     * @return object
     */
    T findById(@Param("id") int id);

    /**
     * 插入对象
     * @param entity  entity
     * @return number of record
     */
    int insert(T entity);

    /**
     * 批量新增，可在service层限制每次插入的数量
     * @param list list of entity
     * @return number of record
     */
    int batchInsert(List<T> list);
    /**
     * 更新数据
     * @param entity  entity
     * @return number of record
     */
    int update(T entity);

    /**
     * 批量删除
     * @param ids id array
     * @return number of record
     */
    int delete(@Param("ids") int[] ids);

    /**
     * 批量获取
     * @param ids id array
     * @return list
     */
    List<T> findByIds(@Param("ids") int[] ids);

    /**
     * 查询列字段是否没有重复:
     * @param id: 如果不传 则判断表里的全部项,如果传了id,则排除当前id所对应的列
     * @param value 需要判断是否重复的列
     * @param column 列名称
     * @return if repeat
     */
    default boolean checkNotRepeat(@Param("id") Integer id, @Param("value") String value, @Param("column") String column) {
        return countByColumnValue(id, value, column) == 0L;
    }


    /**
     * 查询列指定字段的记录数
     * @param id: 如果不传 则判断表里的全部项,如果传了id,则排除当前id所对应的列
     * @param value 需要判断是否重复的列
     * @param column 列名称
     * @return if repeat
     */
    long countByColumnValue(@Param("id") Integer id, @Param("value") String value, @Param("column") String column);


    /**
     * 修改enable状态
     */
    default int updateEnable(@Param("id")Integer id, @Param("enable")Boolean enable){
        throw new UnsupportedOperationException("当前 Mapper.xml 未实现 updateEnable 方法");
    }

}