package cn.xuqiudong.basic.quartz.entity;

import cn.xuqiudong.basic.mybatisplus.entity.BaseMpEntity;
import cn.xuqiudong.basic.quartz.enums.QuartzStatusEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * quartz任务表 实体类
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TaskJob", description = "quartz任务表")
@TableName("t_task_job")
public class TaskJob extends BaseMpEntity<String> {

    /**
     * 任务名
     */
    @TableField(value = "`name`")
    @Schema(description = "任务名")
    private String name;

    /**
     * 任务code
     */
    @Schema(description = "任务code")
    @TableField(value = "`code`")
    private String code;

    /**
     * 任务组
     */
    @Schema(description = "任务组")
    @TableField(value = "`group`")
    private String group;

    /**
     * cron表达式
     */
    @TableField(value = "cron")
    @Schema(description = "cron表达式")
    private String cron;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态")
    @TableField(value = "`status`")
    private QuartzStatusEnum status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField(value = "note")
    private String note;

    /**
     * 是否删除
     */
    @TableField(value = "deleted")
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 初始状态应为移除
     */
    public TaskJob() {
        this.status = QuartzStatusEnum.REMOVE;
    }

    public String getStatusText() {
        return status == null ? "" : status.getText();
    }

}
