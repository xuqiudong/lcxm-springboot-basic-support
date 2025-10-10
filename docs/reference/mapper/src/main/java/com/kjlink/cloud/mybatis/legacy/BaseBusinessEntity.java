package com.kjlink.cloud.mybatis.legacy;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用于按部门隔离业务数据
 * 兼容历史项目，不推荐继续使用，请尽快升级到String型id，方便适配各种国产数据库
 */
@Deprecated
public class BaseBusinessEntity extends BaseEntity {

    /**
     * 负责人账号
     */
    @Schema(description = "负责人账号")
    @TableField(value = "owner_by", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private String ownerBy;

    /**
     * 所属部门id
     */
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
