package cn.xuqiudong.basic.quartz.controller;

import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.query.TaskJobLogQuery;
import cn.xuqiudong.basic.quartz.service.TaskJobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 定时任务执行记录 Controller
 *
 * @author Vic.xu
 * @since 2026-05-28 15:51
 */
@Tag(name = "TaskJobLog", description = "定时任务执行记录")
@RequestMapping("/quartz/taskJobLog")
@RestController
public class TaskJobLogController {
    @Autowired
    private TaskJobLogService service;

    @Operation(summary = "分页查询")
    @PostMapping(value = "/page")
    public BaseResponse<PageInfo<TaskJobLog>> page(@RequestBody TaskJobLogQuery query) {
        PageInfo<TaskJobLog> page = service.page(query);
        return BaseResponse.success(page);
    }


    @Operation(summary = "详情", description = "根据id查询详情")
    @GetMapping(value = "/detail/{id}")
    public BaseResponse<TaskJobLog> detail(@PathVariable String id) {
        TaskJobLog entity = service.selectById(id);
        return entity == null ? BaseResponse.error("不存在的实体") : BaseResponse.success(entity);
    }


    @Operation(summary = "修改状态", description = "修改状态")
    @PostMapping(value = "/updateEnable/{id}")
    public BaseResponse<Boolean> updateEnable(@PathVariable String id, Boolean enable) {
        int updated = service.updateEnable(id, enable);
        return BaseResponse.success(updated == 1);
    }

    @Operation(summary = "删除", description = "根据id删除")
    @PostMapping(value = "/delete/{id}")
    public BaseResponse<Integer> delete(@PathVariable String id) {
        return BaseResponse.success(service.delete(id));
    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Operation(summary = "批量删除", description = "批量删除")
    @PostMapping(value = "/deleteBatch")
    public BaseResponse<Integer> delete(@RequestBody List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return BaseResponse.error("请选择要删除的记录");
        }
        return BaseResponse.success(service.delete(ids.toArray(new String[0])));
    }

}