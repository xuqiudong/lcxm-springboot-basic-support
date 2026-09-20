package cn.xuqiudong.basic.mybatisplus.service;

import cn.xuqiudong.basic.core.lookup.Lookup;
import cn.xuqiudong.basic.core.model.BaseGenericEntity;
import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.mybatisplus.convert.PageConvert;
import cn.xuqiudong.basic.mybatisplus.mapper.BaseGenericMapper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;


/**
 * 描述: 支持主键泛型的Service；
 * 子类可以： UserService extends BaseGenericService<UserMapper, User, Integer>{}
 *
 * @param <M> mapper
 * @param <T> 实体 extends BaseGenericEntity
 * @param <K> 主键类型
 * @author Vic.xu
 * @since 2025-03-12 11:33
 */
public abstract class BaseGenericService<M extends BaseGenericMapper<T, K>, T extends BaseGenericEntity<K>,
        K extends Serializable> {
    /**
     * 批量插入一次最多插入多少数据
     */
    private static final int MAX_BATCH_SIZE = 1000;

    @Autowired(required = false)
    protected M mapper;

    public void startPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }

    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    /**
     * 查询列表
     */
    public List<T> list(Lookup lookup) {
        return mapper.list(lookup);
    }

    /**
     * 查询分页列表
     */
    public PageInfo<T> page(Lookup lookup) {
        startPage(lookup.getPage(), lookup.getSize());
        List<T> datas = this.list(lookup);
        return PageConvert.convert(datas);
    }

    /**
     * 根据主键id查询对象
     */
    public T findById(K id) {
        return mapper.findById(id);
    }

    /**
     * 插入对象
     */
    public int insert(T entity) {
        return mapper.insert(entity);
    }

    /**
     * 批量插入
     *
     * @param list list
     * @return size
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<T> list) {
        if (list == null) {
            return 0;
        }
        List<List<T>> partition = ListUtils.partition(list, MAX_BATCH_SIZE);
        for (List<T> ts : partition) {
            mapper.batchInsert(ts);
        }
        return list.size();
    }

    /**
     * 更新数据
     */
    public int update(T entity) {
        return mapper.update(entity);
    }

    /**
     * 保存:根据id判断新增或更新实体
     */
    public int save(T entity) {
        if (entity.isNew()) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    /**
     * 根据id删除记录
     */
    public int delete(K id) {
        // 安全地创建 K[] 类型的数组并传入 delete 方法
        @SuppressWarnings("unchecked")
        K[] ids = (K[]) Array.newInstance(id.getClass(), 1);
        ids[0] = id;
        return this.delete(ids);
    }

    /**
     * 修改enable状态
     */
    public int updateEnable(K id, boolean enable) {
        return mapper.updateEnable(id, enable);
    }

    /**
     * 批量删除
     */
    public int delete(K[] ids) {
        return mapper.delete(ids);
    }

    /**
     * 批量获取
     */
    public List<T> findByIds(K[] ids) {
        return mapper.findByIds(ids);
    }

    /**
     * 查询列字段是否没有重复
     *
     * @param id     如果不传则判断表里的全部项, 如果传了id,则排除当前id所对应的列
     * @param value  需要判断是否重复的列
     * @param column 列名称
     * @return 是否重复
     */
    public boolean checkNotRepeat(K id, String value, String column) {
        return mapper.checkNotRepeat(id, value, column);
    }
}
