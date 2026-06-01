package cn.xuqiudong.basic.quartz.query;

import cn.xuqiudong.basic.mybatisplus.annotation.QueryCondition;
import cn.xuqiudong.basic.mybatisplus.enums.QueryOperation;
import cn.xuqiudong.basic.mybatisplus.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * quartz任务表 分页查询参数
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TaskJobQuery", description = "quartz任务表")
public class TaskJobQuery extends PageQuery {

    /**
     * 任务名
     */
    @Schema(description = "任务名")
    @QueryCondition(operation = QueryOperation.LIKE)
    private String name;

    /**
     * 任务code
     */
    @Schema(description = "任务code")
    @QueryCondition(operation = QueryOperation.LIKE)
    private String code;

    /**
     * 任务组
     */
    @Schema(description = "任务组")
    @QueryCondition(operation = QueryOperation.LIKE)
    private String group;

    /**
     * cron表达式
     */
    @QueryCondition(operation = QueryOperation.LIKE)
    @Schema(description = "cron表达式")
    private String cron;

    /**
     * 任务状态
     */
    @QueryCondition(operation = QueryOperation.EQ)
    @Schema(description = "任务状态")
    private String status;

    /**
     * 备注
     */
    @QueryCondition(operation = QueryOperation.LIKE)
    @Schema(description = "备注")
    private String note;

    /**
     * 是否删除
     */
    @QueryCondition(operation = QueryOperation.EQ)
    @Schema(description = "是否删除")
    private Boolean deleted;


}
