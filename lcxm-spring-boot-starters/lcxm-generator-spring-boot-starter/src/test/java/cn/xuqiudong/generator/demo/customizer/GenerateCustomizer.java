package cn.xuqiudong.generator.demo.customizer;

import cn.xuqiudong.common.base.annotation.NoneColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 测试生成 测试自定义模板， 理论上主要是复用Entity 的相关字段
 *
 * @author Vic.xu
 * @since 2025-11-12 15:34
 */

@Data
@Schema(name = "GenerateCustomizer", description = "测试生成Customizer")
public class GenerateCustomizer {

    /**
     * 名称
     */
    @Schema(description = "名称")
    @NoneColumn
    private String name;

    /**
     * 生日
     */
    @Schema(description = "生日")
    @NoneColumn
    private LocalDate birthday;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @NoneColumn
    private String note;

    /**
     * 类型
     */
    @Schema(description = "类型")
    @NoneColumn
    private String type;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    @NoneColumn
    private Long version;

    /**
     * 文章
     */
    @Schema(description = "文章")
    @NoneColumn
    private String article;

    /**
     * 打开时间
     */
    @Schema(description = "打开时间")
    @NoneColumn
    private LocalTime openTime;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @NoneColumn
    private LocalDateTime startTime;

    /**
     * tag名称
     */
    @Schema(description = "tag名称")
    @NoneColumn
    private String tagName;


}
