package cn.xuqiudong.basic.mybatisplus.entity;


import cn.xuqiudong.basic.framework.code2text.annotation.UserCode2Text;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 描述:
 * mybatis-plus  基础类;
 * ID: 建议数据库设置为bigint 或varchar(32)
 *
 * @author Vic.xu
 * @since 2025-09-10 10:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseMpEntity<ID  extends Serializable> extends IdEntity<ID> {

    /**
     * 创建人账号
     */
    @UserCode2Text
    @Schema(description = "创建人账号")
    @TableField(value = "create_by", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    protected String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    protected LocalDateTime createTime;

    /**
     * 最后更新人账号
     */
    @UserCode2Text
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    protected String updateBy;

    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;

    @Schema(description = "是否启用")
    @TableField(value = "enabled")
    protected Boolean enabled;


}
