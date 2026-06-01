package cn.xuqiudong.basic.quartz.model.dto;

import cn.xuqiudong.basic.quartz.entity.TaskJob;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-05-29 11:33
 */
@Data
@Schema(name = "TaskJobBaseInfoDto", description = "任务基础信息")
public class TaskJobBaseInfoDto {

    @Schema(description = "任务id")
    private String id;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "任务备注")
    private String note;


    public TaskJob taskJob() {
        TaskJob taskJob = new TaskJob();
        taskJob.setId(id);
        taskJob.setName(name);
        taskJob.setNote(note);
        return taskJob;
    }
}
