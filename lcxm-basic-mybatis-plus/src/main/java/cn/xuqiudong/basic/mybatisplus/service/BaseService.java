package cn.xuqiudong.basic.mybatisplus.service;

import cn.xuqiudong.basic.core.lookup.Lookup;
import cn.xuqiudong.basic.mybatisplus.mapper.BaseMapper;
import cn.xuqiudong.basic.core.model.BaseEntity;
import cn.xuqiudong.basic.mybatisplus.model.PageInfo;
import cn.xuqiudong.basic.core.util.ListUtils;
import cn.xuqiudong.basic.framework.service.AttachmentStatusOperationServiceI;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service基类
 * @author VIC.xu
 * @param <M> 继承自BaseMapper的Mapper接口
 * @param <T> 当前service对应的实体
 * @since 2019-11-12
 */
public abstract class BaseService<M extends BaseMapper<T>, T extends BaseEntity> {

    /**
     * 批量插入一次最多插入多少数据
     */
    private static final int MAX_BATCH_SIZE = 1000;

    @Autowired(required = false)
    protected M mapper;

    @Autowired(required = false)
    private AttachmentStatusOperationServiceI attachmentStatusOperationService;


    /**
     * 当前实体是否包含附件
     * @return
     */
    protected abstract boolean hasAttachment();

    public void startPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
    }

    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    /** 查询列表 */
    public List<T> list(Lookup lookup) {
        List<T> datas = mapper.list(lookup);
        return datas;
    }

    /** 查询分页列表 */
    public PageInfo<T> page(Lookup lookup) {
        startPage(lookup.getPage(), lookup.getSize());
        List<T> datas = this.list(lookup);
        return PageInfo.instance(datas);
    }

    /** 根据主键id查询对象 */
    public T findById(int id) {
        T entity = mapper.findById(id);
        if (hasAttachment() && attachmentStatusOperationService != null) {
            //查询附件关系 并保存到实体中
            attachmentStatusOperationService.fillAttachmentInfo(entity);
        }
        return entity;
    }

    /** 插入对象 */
    public int insert(T entity) {
        //确保先生成id
        int num = mapper.insert(entity);
        if (hasAttachment() && attachmentStatusOperationService != null) {
            attachmentStatusOperationService.addAttachmentFromObj(entity);
        }
        return num;
    }

    /**
     * 批量插入
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

    /** 更新数据 */
    public int update(T entity) {
        if (hasAttachment() && attachmentStatusOperationService != null) {
            T old = findById(entity.getId());
            attachmentStatusOperationService.handleOldAndNowAttachment(old, entity);
        }
        return mapper.update(entity);
    }

    /**保存:根据id判断新增或更新实体*/
    public int save(T entity) {
        if (entity.getId() == null || entity.getId() <= 0) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    /**根据id删除记录*/
    public int delete(int id) {
        if (hasAttachment() && attachmentStatusOperationService != null) {
            attachmentStatusOperationService.deleteAttachmentFromObj(findById(id));
        }
        return this.delete(new int[]{id});
    }

    /** 批量删除 */
    public int delete(int[] ids) {
        if (hasAttachment() && attachmentStatusOperationService != null) {
            attachmentStatusOperationService.deleteAttachmentFromObj(findByIds(ids));
        }
        return mapper.delete(ids);
    }

    /** 批量获取 */
    public List<T> findByIds(@Param("ids") int[] ids) {
        return mapper.findByIds(ids);
    }

    /**
     * 查询列字段是否没有重复:
     * @param id: 如果不传 则判断表里的全部项,如果传了id,则排除当前id所对应的列
     * @param value 需要判断是否重复的列
     * @param column 列名称
     * @return
     */
    public boolean checkNotRepeat(Integer id, String value, String column) {
        return mapper.checkNotRepeat(id, value, column);
    }

}
