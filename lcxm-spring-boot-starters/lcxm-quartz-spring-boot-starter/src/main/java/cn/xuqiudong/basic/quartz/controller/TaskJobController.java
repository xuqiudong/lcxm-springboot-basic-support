package cn.xuqiudong.basic.quartz.controller;

import cn.xuqiudong.basic.core.context.CurrentUserInfoContext;
import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.core.model.SelectOption;
import cn.xuqiudong.basic.core.request.CheckNotRepeatRequest;
import cn.xuqiudong.basic.core.vo.BooleanWithMsg;
import cn.xuqiudong.basic.quartz.entity.TaskJob;
import cn.xuqiudong.basic.quartz.enums.QuartzStatusEnum;
import cn.xuqiudong.basic.quartz.helper.CommonJobQuartzHelper;
import cn.xuqiudong.basic.quartz.helper.JobUserHolder;
import cn.xuqiudong.basic.quartz.model.dto.TaskJobBaseInfoDto;
import cn.xuqiudong.basic.quartz.query.TaskJobQuery;
import cn.xuqiudong.basic.quartz.service.TaskJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * quartz任务表 Controller
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Tag(name = "TaskJob", description = "quartz任务表")
@RequestMapping("/quartz/taskJob")
@RestController
public class TaskJobController {
    @Autowired
    private TaskJobService service;

    @Autowired
    private CommonJobQuartzHelper commonJobQuartzHelper;

    @Operation(summary = "分页查询")
    @PostMapping(value = "/page")
    public BaseResponse<PageInfo<TaskJob>> page(@RequestBody TaskJobQuery query) {
        PageInfo<TaskJob> page = service.page(query);
        return BaseResponse.success(page);
    }


    @Operation(summary = "详情", description = "根据id查询详情")
    @GetMapping(value = "/detail/{id}")
    public BaseResponse<TaskJob> detail(@PathVariable String id) {
        TaskJob entity = service.selectById(id);
        return entity == null ? BaseResponse.error("不存在的实体") : BaseResponse.success(entity);
    }

    @Operation(summary = "保存", description = "id空则新增， id非空则修改, 不可新增修改status状态")
    @PostMapping(value = "/save")
    public BaseResponse<TaskJob> save(@RequestBody TaskJob entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.setStatus(QuartzStatusEnum.REMOVE);
        } else {
            entity.setStatus(null);
        }
        int num = service.save(entity);
        if (num <= 0) {
            return BaseResponse.error("保存失败");
        }
        return BaseResponse.success(service.selectById(entity.getId()));
    }

    @Operation(summary = "修改状态", description = "修改状态, 禁用的任务项目启动时不再初始化")
    @PostMapping(value = "/updateEnable/{id}")
    public BaseResponse<Boolean> updateEnable(@PathVariable String id, Boolean enable) {
        int updated = service.updateEnable(id, enable);
        return BaseResponse.success(updated == 1);
    }

    @Operation(summary = "删除", description = "根据id删除")
    @PostMapping(value = "/delete/{id}")
    public BaseResponse<Integer> delete(@PathVariable String id) {
        TaskJob taskJob = service.selectById(id);
        if (taskJob == null) {
            return BaseResponse.error("任务不存在");
        }
        if (!isStatusValid(taskJob, QuartzStatusEnum.WORKING, QuartzStatusEnum.PAUSE)) {
            return BaseResponse.error("只可删除[REMOVE]状态的任务");
        }
        return BaseResponse.success(service.delete(id));
    }


    @Operation(summary = "检测字段是否可用", description = "检测字段是否可用")
    @PostMapping(value = "/check")
    public BaseResponse<?> checkAvailable(@RequestBody CheckNotRepeatRequest<String> repeatRequest) {
        boolean ok = service.isValueAvailable(repeatRequest.getId(), repeatRequest.getValue(), repeatRequest.getColumn());
        return BaseResponse.success(ok);
    }

    /**
     * 修改基本信息：  name  note
     */
    @Operation(summary = "修改基本信息:  name  note")
    @PostMapping(value = "/updateBaseInfo")
    public BaseResponse<TaskJob> updateBaseInfo(@RequestBody TaskJobBaseInfoDto baseInfo) {
        Assert.notNull(baseInfo, "baseInfo can not be null");
        TaskJob taskJob = baseInfo.taskJob();
        int updated = service.save(taskJob);
        return updated == 1 ? BaseResponse.success(taskJob) : BaseResponse.error("修改失败");
    }

    /**
     * 注册任务: remove的定时任务可以注册（创建job）
     */
    @Operation(summary = "注册任务: remove的定时任务可以注册（创建job）并设置为运行状态")
    @PostMapping(value = "/register/{id}")
    public BaseResponse<Boolean> register(@PathVariable String id) {
        TaskJob taskJob = service.selectById(id);
        if (taskJob == null) {
            return BaseResponse.error("任务不存在");
        }
        if (!isStatusValid(taskJob, QuartzStatusEnum.REMOVE)) {
            return BaseResponse.error("任务状态不合法");
        }
        taskJob.setStatus(QuartzStatusEnum.WORKING);
        BooleanWithMsg result = commonJobQuartzHelper.createJob(taskJob);
        if (result.isSuccess()) {
            updateStatus(id, QuartzStatusEnum.WORKING);
        }
        return toResponse(result);
    }

    /**
     * 运行一次:   ['WORKING', 'PAUSE'  ] 状态可以运行一次
     */
    @Operation(summary = "运行一次:   ['WORKING', 'PAUSE'  ] 状态可以运行一次")
    @PostMapping(value = "/runOnce/{id}")
    public BaseResponse<Boolean> runOnce(@PathVariable String id) {
        TaskJob taskJob = service.selectById(id);
        if (taskJob == null) {
            return BaseResponse.error("任务不存在");
        }
        if (!isStatusValid(taskJob, QuartzStatusEnum.WORKING, QuartzStatusEnum.PAUSE)) {
            return BaseResponse.error("任务状态不合法");
        }
        JobDataMap jobDataMap = JobUserHolder.buildJobDataMap(CurrentUserInfoContext.getUserId(), CurrentUserInfoContext.getUsername());
        BooleanWithMsg result = commonJobQuartzHelper.runJobNow(taskJob.getTaskCode(), taskJob.getTaskGroup(), jobDataMap);
        return toResponse(result);

    }

    /**
     * 暂停: WORKING 状态可以暂停
     */
    @Operation(summary = "暂停: WORKING 状态可以暂停")
    @PostMapping(value = "/pause/{id}")
    public BaseResponse<Boolean> pause(@PathVariable String id) {
        TaskJob taskJob = service.selectById(id);
        if (taskJob == null) {
            return BaseResponse.error("任务不存在");
        }
        if (!isStatusValid(taskJob, QuartzStatusEnum.WORKING)) {
            return BaseResponse.error("任务状态不合法");
        }
        updateStatus(id, QuartzStatusEnum.PAUSE);
        BooleanWithMsg result = commonJobQuartzHelper.pauseJob(taskJob.getTaskCode(), taskJob.getTaskGroup());
        return toResponse(result);
    }

    /**
     * 恢复: PAUSE 状态可以恢复
     */
    @Operation(summary = "恢复: PAUSE 状态可以恢复")
    @PostMapping(value = "/resume/{id}")
    public BaseResponse<Boolean> resume(@PathVariable String id) {
        TaskJob taskJob = service.selectById(id);
        if (taskJob == null) {
            return BaseResponse.error("任务不存在");
        }
        if (!isStatusValid(taskJob, QuartzStatusEnum.PAUSE)) {
            return BaseResponse.error("任务状态不合法");
        }
        updateStatus(id, QuartzStatusEnum.WORKING);
        BooleanWithMsg result = commonJobQuartzHelper.resumeJob(taskJob.getTaskCode(), taskJob.getTaskGroup());
        return toResponse(result);
    }

    /**
     * 移除任务: ['WORKING', 'PAUSE'] 状态可以移除
     */
    @Operation(summary = "移除任务: ['WORKING', 'PAUSE'] 状态可以移除")
    @PostMapping(value = "/remove/{id}")
    public BaseResponse<Boolean> remove(@PathVariable String id) {
        TaskJob taskJob = service.selectById(id);
        if (taskJob == null) {
            return BaseResponse.error("任务不存在");
        }
        if (!isStatusValid(taskJob, QuartzStatusEnum.WORKING, QuartzStatusEnum.PAUSE)) {
            return BaseResponse.error("任务状态不合法");
        }
        updateStatus(id, QuartzStatusEnum.REMOVE);
        BooleanWithMsg result = commonJobQuartzHelper.deleteJob(taskJob.getTaskCode(), taskJob.getTaskGroup());
        return toResponse(result);

    }

    /**
     * 修改运行频率:  ['WORKING', 'PAUSE'] 状态可以修改任务时间
     */
    @Operation(summary = "修改运行频率:  ['WORKING', 'PAUSE'] 状态可以任务cron")
    @PostMapping(value = "/modifyCron/{id}")
    public BaseResponse<Boolean> modifyCron(@PathVariable String id,
                                            @RequestParam
                                            @Parameter(description = "cron表达式") String cron) {
        TaskJob taskJob = service.selectById(id);
        if (taskJob == null) {
            return BaseResponse.error("任务不存在");
        }
        if (!isStatusValid(taskJob, QuartzStatusEnum.WORKING, QuartzStatusEnum.PAUSE)) {
            return BaseResponse.error("任务状态不合法");
        }
        BooleanWithMsg result = commonJobQuartzHelper.modifyJobTime(taskJob.getTaskCode(), taskJob.getTaskGroup(), cron);
        if (result.isSuccess()) {
            taskJob.setCron(cron);
            service.save(taskJob);
        }
        return toResponse(result);
    }


    /**
     * 定时任务下拉框选项
     */
    @Operation(summary = "定时任务下拉框选项")
    @GetMapping(value = "/selectOptions")
    public BaseResponse<List<SelectOption>> selectOptions() {
        return BaseResponse.success(service.getSelectOptions());
    }


    /**
     * 判断状态是否合法： 当前任务的状态是否属于 某些状态
     */
    private boolean isStatusValid(TaskJob taskJob, QuartzStatusEnum... validStatus) {
        for (QuartzStatusEnum status : validStatus) {
            if (taskJob.getStatus() == status) {
                return true;
            }
        }
        return false;
    }

    private boolean updateStatus(String id, QuartzStatusEnum statusEnum) {
        TaskJob taskJob = new TaskJob();
        taskJob.setId(id);
        taskJob.setStatus(statusEnum);
        return service.save(taskJob) > 0;
    }

    private BaseResponse<Boolean> toResponse(BooleanWithMsg msg) {
        BaseResponse<Boolean> response = BaseResponse.judge(msg.isSuccess());
        response.setMsg(msg.getMessage());
        return response;
    }

}