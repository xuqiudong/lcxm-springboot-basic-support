package cn.xuqiudong.basic.framework.service;

import cn.xuqiudong.basic.core.model.BaseGenericEntity;

import java.util.List;

/**
 * 说明 :  附件状态处理service  适配BaseGenericService
 * 主要是实体 BaseEntity 修改为 BaseGenericEntity
 *
 * @author  Vic.xu
 * @since  2025-03-12
 */
public interface AttachmentStatusOperationGenericServiceI {

    /**
     * 新增对象中的全部附件
     * @param t object
     * @param <T> object class
     * @return if success
     */
    <T extends BaseGenericEntity> boolean addAttachmentFromObj(T t);

    /**
     * 删除对象中的全部附件
     * @param t  object
     * @param <T>Object class
     */
    <T extends BaseGenericEntity> void deleteAttachmentFromObj(T t);

    /**
     * 批量删除
     * @param ts list of include attachment object
     * @param <T> Object class
     */
    <T extends BaseGenericEntity> void deleteAttachmentFromObj(List<T> ts);

    /**
     * 分开对象中要删除和要新增的附件 需要 AttachmentFlag 注解
     * @param <T> Object class
     * @param old 原来对象
     * @param now 新的对象
     */
    public <T extends BaseGenericEntity> void handleOldAndNowAttachment(T old, T now);

    /**
     * 查询附件关系 并保存到实体中
     * @param entity Object
     */
    <T extends BaseGenericEntity> void fillAttachmentInfo(T entity);
}
