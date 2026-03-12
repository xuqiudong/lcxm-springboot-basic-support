package cn.xuqiudong.basic.trial.kjlink.warning.enums;

import cn.xuqiudong.basic.core.select.EnumSelectable;
import cn.xuqiudong.basic.core.select.annotation.RegisterSelectEnum;

/**
 * 描述:
 * 预警分类枚举（如超损预警、上险预警）
 *
 * @author Vic.xu
 * @since 2025-12-05 9:38
 */
@RegisterSelectEnum
public enum WarningCategoryEnum implements EnumSelectable {


    OVER_LOSS("超损预警"),
    INSURANCE("上险预警"),
    MONITOR("监控异常预警"),
    ACCIDENT("事故预警");

    private final String name;

    WarningCategoryEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // 实现EnumSelectable接口（按你的要求：用枚举名作为value）
    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public String getText() {
        return this.name;
    }
}
