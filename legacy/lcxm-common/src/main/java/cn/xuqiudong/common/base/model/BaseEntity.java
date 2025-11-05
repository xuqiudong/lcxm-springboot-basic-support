package cn.xuqiudong.common.base.model;

import cn.xuqiudong.common.base.lookup.Lookup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础的实体 对应每个表都应该存在的属性
 *
 * @author VIC.xu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity extends Lookup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    protected Integer id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

    public Integer getId() {
        return id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public BaseEntity() {
    }

    public static BaseEntity enabledEntity() {
        return new BaseEntity().setEnable(true).setDelete(false);
    }

    public BaseEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public BaseEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public BaseEntity setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Boolean isDelete() {
        return delete;
    }

    public Boolean isEnable() {
        return enable;
    }

    public BaseEntity setDelete(Boolean delete) {
        this.delete = delete;
        return this;
    }

    public BaseEntity setEnable(Boolean enable) {
        this.enable = enable;
        return this;
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

}
