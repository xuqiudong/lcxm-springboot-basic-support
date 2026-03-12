package cn.xuqiudong.basic.framework.select;

import cn.xuqiudong.basic.core.model.SelectOption;

/**
 * 描述:
 *   标记枚举可转为下拉选项的接口
 * @see SelectOption
 * @author Vic.xu
 * @since 2025-11-13 17:03
 */
public interface EnumSelectable {

    /**
     * 获取下拉框的value（通常是枚举的编码/唯一标识）
     */
    String getValue();

    /**
     * 获取下拉框的text（通常是枚举的中文描述）
     */
    String getText();

    /**
     * 可选：排序（默认按枚举定义顺序，值越小越靠前）
     */
    default int getSort() {
        return 0;
    }
}
