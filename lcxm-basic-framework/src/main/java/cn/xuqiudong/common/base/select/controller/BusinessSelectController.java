package cn.xuqiudong.common.base.select.controller;

import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.common.base.model.SelectOption;
import cn.xuqiudong.common.base.select.service.BusinessSelectFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述:
 * 通用业务下拉框接口"
 *
 * @author Vic.xu
 * @since 2026-01-08 10:34
 */
@Tag(name = "Common:businessSelect", description = "通用业务下拉框接口")
@RestController
@RequestMapping("/common/businessSelect")
public class BusinessSelectController {

    private final BusinessSelectFacade businessSelectFacade;

    public BusinessSelectController(BusinessSelectFacade businessSelectFacade) {
        this.businessSelectFacade = businessSelectFacade;
    }

    /**
     * 获取指定业务类型的下拉选项
     *
     * @param type 枚举标识（通过实现 BusinessSelectService 接口的Bean注入， 注意不要重复）
     * @return 下拉选项列表
     */
    @Operation(summary = "获取指定业务类型的下拉选项")
    @GetMapping("/{type}")
    public BaseResponse<List<SelectOption>> getSelectOptions(@PathVariable String type) {
        List<SelectOption> options = businessSelectFacade.getSelectOptions(type);
        return BaseResponse.success(options);
    }

}
