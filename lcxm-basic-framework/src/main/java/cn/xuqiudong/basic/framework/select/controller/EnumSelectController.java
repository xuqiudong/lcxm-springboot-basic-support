package cn.xuqiudong.basic.framework.select.controller;

import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.core.model.SelectOption;
import cn.xuqiudong.basic.framework.select.convert.EnumSelectConverter;
import cn.xuqiudong.basic.framework.select.registry.EnumSelectRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 通用枚举下拉框接口
 *
 * @author Vic.xu
 * @since 2025-11-13 17:23
 */
@Tag(name = "Common:enumSelect", description = "通用枚举下拉框接口")
@RestController
@RequestMapping("/common/enumSelect")
public class EnumSelectController {

    /**
     * 获取指定枚举的下拉选项
     *
     * @param enumKey 枚举标识（通过@RegisterEnum注解定义）
     * @return 下拉选项列表
     */
    @Operation(summary = "获取指定枚举的下拉选项")
    @GetMapping("/{enumKey}")
    public BaseResponse<List<SelectOption>> getSelectOptions(@PathVariable String enumKey) {
        Class<?> enumClass = EnumSelectRegistry.getEnumClass(enumKey);
        if (enumClass == null) {
            return BaseResponse.error("枚举[" + enumKey + "]未注册");
        }

        // 转换为下拉列表
        List<SelectOption> options = EnumSelectConverter.convert((Class) enumClass);
        return BaseResponse.success(options);
    }

    /**
     * 获取所有已注册的枚举标识（用于前端文档）
     */
    @Operation(summary = "获取所有已注册的枚举标识")
    @GetMapping("/registered")
    public BaseResponse<Map<String, String>> getRegisteredEnums() {
        return BaseResponse.success(EnumSelectRegistry.getRegisteredEnums());
    }

    @Operation(summary = "获取多个枚举下拉选项")
    @GetMapping("/multipleOptions")
    public BaseResponse<Map<String, List<SelectOption>>> getMultipleOptions(List<String> enumKeys) {
        return BaseResponse.success(EnumSelectRegistry.getMultipleOptions(enumKeys));

    }
}
