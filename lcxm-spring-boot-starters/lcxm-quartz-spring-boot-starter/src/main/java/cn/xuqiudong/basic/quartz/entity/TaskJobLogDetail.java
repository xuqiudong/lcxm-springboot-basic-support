package cn.xuqiudong.basic.quartz.entity;

import cn.xuqiudong.basic.mybatisplus.entity.BaseMpEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务执行记录明细 实体类
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TaskJobLogDetail", description = "定时任务执行记录明细")
@TableName("t_task_job_log_detail")
public class TaskJobLogDetail extends BaseMpEntity<String> {

    /**
     * 执行记录id
     */
    @TableField(value = "task_job_log_id")
    @Schema(description = "执行记录id")
    private String taskJobLogId;

    /**
     * 执行结果
     */
    @TableField(value = "result")
    @Schema(description = "执行结果")
    private String result;

    /**
     * 结果描述
     */
    @TableField(value = "result_note")
    @Schema(description = "结果描述")
    private String resultNote;

    /**
     * 是否删除
     */
    @TableField(value = "deleted")
    @Schema(description = "是否删除")
    private Boolean deleted;


}
