package cn.xuqiudong.generator.demo.query;

import cn.xuqiudong.common.annotation.QueryCondition;
import cn.xuqiudong.common.enums.QueryOperation;
import cn.xuqiudong.common.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
* 测试生成 分页查询参数
*
* @author Vic.xu
* @since 2025-11-03 15:23
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name="GenerateQuery", description = "测试生成")
public class GenerateQuery extends PageQuery {

    /**
    *  名称
    */
    @QueryCondition(operation = QueryOperation.LIKE)
    @Schema(description = "名称")
    private String name;

    /**
    *  生日
    */
    @Schema(description = "生日")
    @QueryCondition(operation = QueryOperation.EQ)
    private LocalDate birthday;

    /**
    *  备注
    */
    @QueryCondition(operation = QueryOperation.LIKE)
    @Schema(description = "备注")
    private String note;

    /**
    *  类型
    */
    @QueryCondition(operation = QueryOperation.LIKE)
    @Schema(description = "类型")
    private String type;

    /**
    *  版本号
    */
    @Schema(description = "版本号")
    @QueryCondition(operation = QueryOperation.EQ)
    private Long version;

    /**
    *  文章
    */
    @QueryCondition(operation = QueryOperation.LIKE)
    @Schema(description = "文章")
    private String article;

    /**
    *  打开时间
    */
    @Schema(description = "打开时间")
    @QueryCondition(operation = QueryOperation.EQ)
    private LocalTime openTime;

    /**
    *  开始时间
    */
    @Schema(description = "开始时间")
    @QueryCondition(operation = QueryOperation.EQ)
    private LocalDateTime startTime;

    /**
    *  tag名称
    */
    @Schema(description = "tag名称")
    @QueryCondition(operation = QueryOperation.LIKE)
    private String tagName;


}
