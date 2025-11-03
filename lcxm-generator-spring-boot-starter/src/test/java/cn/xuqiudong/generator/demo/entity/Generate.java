package cn.xuqiudong.generator.demo.entity;

import cn.xuqiudong.common.base.entity.BaseMpEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* 测试生成 实体类
*
* @author Vic.xu
* @since 2025-11-03 15:16
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name="Generate", description = "测试生成")
@TableName("test_generate")
public class Generate extends BaseMpEntity<String> {

    /**
    *  名称
    */
    @TableField(value = "`name`")
    @Schema(description = "名称")
    private String name;

    /**
    *  生日
    */
    @Schema(description = "生日")
    @TableField(value = "birthday")
    private LocalDate birthday;

    /**
    *  备注
    */
    @TableField(value = "note", select = false)
    @Schema(description = "备注")
    private String note;

    /**
    *  类型
    */
    @TableField(value = "`type`")
    @Schema(description = "类型")
    private String type;

    /**
    *  版本号
    */
    @TableField(value = "version")
    @Schema(description = "版本号")
    private Long version;

    /**
    *  文章
    */
    @TableField(value = "article", select = false)
    @Schema(description = "文章")
    private String article;

    /**
    *  打开时间
    */
    @Schema(description = "打开时间")
    @TableField(value = "open_time")
    private LocalTime openTime;

    /**
    *  开始时间
    */
    @Schema(description = "开始时间")
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
    *  tag名称
    */
    @Schema(description = "tag名称")
    @TableField(value = "tag_name")
    private String tagName;


}
