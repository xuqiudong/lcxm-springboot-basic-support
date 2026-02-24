package cn.xuqiudong.basic.trial.kjlink.warning.enums;

import cn.xuqiudong.common.base.select.EnumSelectable;
import cn.xuqiudong.common.base.select.annotation.RegisterSelectEnum;
import lombok.Getter;

/**
 * 描述:
 *  预警级别
 * @author Vic.xu
 * @since 2025-12-05 10:54
 */
@RegisterSelectEnum
@Getter
public enum WarningLevel implements EnumSelectable {

    MINOR("轻微"),
    MEDIUM("中等"),
    SERIOUS("严重");

    String name;

    WarningLevel(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return name();
    }

    @Override
    public String getText() {
        return name;
    }
}
