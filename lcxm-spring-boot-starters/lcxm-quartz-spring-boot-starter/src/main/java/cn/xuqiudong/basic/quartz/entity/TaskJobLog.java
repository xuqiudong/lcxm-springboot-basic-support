package cn.xuqiudong.basic.quartz.entity;

import cn.xuqiudong.basic.mybatisplus.entity.BaseMpEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务执行记录 实体类
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TaskJobLog", description = "定时任务执行记录")
@TableName("t_task_job_log")
public class TaskJobLog extends BaseMpEntity<String> {

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    @TableField(value = "task_job_id")
    private String taskJobId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    @TableField(value = "task_job_name")
    private String taskJobName;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态")
    @TableField(value = "`status`")
    private String status;

    /**
     * 任务用时(毫秒)
     */
    @Schema(description = "任务用时(毫秒)")
    @TableField(value = "cost_time")
    private Long costTime;

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
