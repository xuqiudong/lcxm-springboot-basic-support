package cn.xuqiudong.basic.quartz.query;

import cn.xuqiudong.basic.mybatisplus.annotation.QueryCondition;
import cn.xuqiudong.basic.mybatisplus.enums.QueryOperation;
import cn.xuqiudong.basic.mybatisplus.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务执行记录明细 分页查询参数
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TaskJobLogDetailQuery", description = "定时任务执行记录明细")
public class TaskJobLogDetailQuery extends PageQuery {

    /**
     * 执行记录id
     */
    @QueryCondition(operation = QueryOperation.EQ)
    @Schema(description = "执行记录id")
    private Integer taskJobLogId;

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
