package cn.xuqiudong.basic.core.select.function;

import cn.xuqiudong.basic.core.model.SelectOption;

import java.util.List;

/**
 * 描述:
 *  简化版快速注册业务下拉框服务
 *  <code>
 *    BusinessSelectFacade.register("default", () -> Collections.emptyList());
 *  </code>
 * @author Vic.xu
 * @since 2026-01-08 14:46
 */
@FunctionalInterface
public interface BusinessSelectHandler {
    /**
     * 获取下拉选项（无需定义type，注册时指定）
     */
    List<SelectOption> getSelectOptions();
}
