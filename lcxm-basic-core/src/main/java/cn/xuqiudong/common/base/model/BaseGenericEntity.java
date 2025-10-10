package cn.xuqiudong.common.base.model;

import cn.xuqiudong.common.base.lookup.Lookup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 * 是对 BaseEntity 的扩展， 让id支持泛型，现有的 BaseEntity 不动
 *
 * @author Vic.xu
 * @since 2025-03-12 9:36
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseGenericEntity<K extends Serializable> extends Lookup implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    protected K id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

    /**
     * 是否删除
     */
    @JsonIgnore
    protected Boolean delete;

    /**
     * 版本控制
     */
    protected Integer version;

    /**
     * 创建人id
     */
    protected Integer createId;

    /**
     * 修改人id
     */
    protected Integer updateId;
    /**
     * 是否启用
     */
    protected Boolean enable;

    /**
     * 判断实体是否为新建（即主键为null或不合法）
     */
    @JsonIgnore
    public boolean isNew() {
        if (id == null) {
            return true;
        }
        if (id instanceof Number) {
            // 对于数字类型的主键，判断是否为0
            return ((Number) id).longValue() <= 0;
        }
        if (id instanceof String) {
            // 对于字符串类型的主键，判断是否为空
            return ((String) id).isEmpty();
        }
        // 如果主键类型不支持，返回true，表示未知情况
        return false;
    }

    public K getId() {
        return id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setId(K id) {
        this.id = id;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean isDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
    }

    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    public Boolean isEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
