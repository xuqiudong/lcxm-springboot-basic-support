package com.kjlink.cloud.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

import com.kjlink.cloud.i18n.I18nOrganization;
import com.kjlink.cloud.i18n.I18nUserName;

/**
 * 用于按部门隔离业务数据
 */
public class BaseBusinessEntity extends BaseEntity {
    /**
     * 负责人账号
     */
    @I18nUserName
    @Schema(description = "负责人账号")
    @TableField(value = "owner_by", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String ownerBy;

    /**
     * 所属部门id
     */
    @I18nOrganization
    @Schema(description = "所属部门id")
    @TableField(value = "organization_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String organizationId;

    public String getOwnerBy() {
        return ownerBy;
    }

    public void setOwnerBy(String ownerBy) {
        this.ownerBy = ownerBy;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
