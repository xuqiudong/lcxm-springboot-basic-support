package cn.xuqiudong.basic.quartz.query;

import cn.xuqiudong.basic.mybatisplus.annotation.QueryCondition;
import cn.xuqiudong.basic.mybatisplus.enums.QueryOperation;
import cn.xuqiudong.basic.mybatisplus.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务执行记录 分页查询参数
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TaskJobLogQuery", description = "定时任务执行记录")
public class TaskJobLogQuery extends PageQuery {

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    @QueryCondition(operation = QueryOperation.EQ)
    private Integer taskJobId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    @QueryCondition(operation = QueryOperation.LIKE)
    private String taskJobName;

    /**
     * 任务状态
     */
    @QueryCondition(operation = QueryOperation.EQ)
    @Schema(description = "任务状态")
    private String status;

    /**
     * 执行结果
     */
    @QueryCondition(operation = QueryOperation.LIKE)
    @Schema(description = "执行结果")
    private String result;

    /**
     * 结果描述
     */
    @Schema(description = "结果描述")
    @QueryCondition(operation = QueryOperation.LIKE)
    private String resultNote;

    /**
     * 是否删除
     */
    @QueryCondition(operation = QueryOperation.EQ)
    @Schema(description = "是否删除")
    private Boolean deleted;


}
