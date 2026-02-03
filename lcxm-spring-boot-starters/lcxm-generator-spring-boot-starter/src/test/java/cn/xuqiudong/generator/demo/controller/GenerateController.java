package cn.xuqiudong.generator.demo.controller;

import cn.xuqiudong.generator.demo.entity.Generate;
import cn.xuqiudong.generator.demo.query.GenerateQuery;
import cn.xuqiudong.generator.demo.service.GenerateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.common.base.model.PageInfo;
import cn.xuqiudong.common.base.request.CheckNotRepeatRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
* 测试生成 Controller
*
* @author Vic.xu
* @since 2025-11-12 15:34
*/
@Tag(name = "Generate", description = "测试生成")
@RequestMapping("/demo/generate")
@RestController
public class GenerateController {
    @Autowired
    private GenerateService service;

    @Operation(summary = "分页查询")
    @PostMapping(value = "/page")
    public BaseResponse<PageInfo<Generate>> page(@RequestBody GenerateQuery query) {
        PageInfo<Generate> page = service.page(query);
        return BaseResponse.success(page);
    }


    @Operation(summary = "详情", description = "根据id查询详情")
    @GetMapping(value = "/detail/{id}")
    public BaseResponse<Generate> detail(@PathVariable String id) {
        Generate entity = service.selectById(id);
        return entity == null ? BaseResponse.error("不存在的实体") : BaseResponse.success(entity);
    }

    @Operation(summary = "保存", description = "id空则新增， id非空则修改")
    @PostMapping(value = "/save")
    public BaseResponse<Generate> save(@RequestBody Generate entity) {
        int num = service.save(entity);
        if (num <= 0) {
            return BaseResponse.error("保存失败");
        }
        return BaseResponse.success(service.selectById(entity.getId()));
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


    @Operation(summary = "检测字段是否可用", description = "检测字段是否可用")
    @PostMapping(value = "/check")
    public BaseResponse<?> checkAvailable(@RequestBody CheckNotRepeatRequest<String> repeatRequest) {
        boolean ok = service.isValueAvailable(repeatRequest.getId(), repeatRequest.getValue(), repeatRequest.getColumn());
        return BaseResponse.success(ok);
    }
}