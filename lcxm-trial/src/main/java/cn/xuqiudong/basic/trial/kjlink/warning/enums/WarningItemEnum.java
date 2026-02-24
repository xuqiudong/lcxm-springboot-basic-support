package cn.xuqiudong.basic.trial.kjlink.warning.enums;

import cn.xuqiudong.common.base.select.EnumSelectable;
import cn.xuqiudong.common.base.select.annotation.RegisterSelectEnum;
import lombok.Getter;

/**
 * 描述:
 *   预警事项
 * @author Vic.xu
 * @since 2025-12-05 9:46
 */
@RegisterSelectEnum
@Getter
public enum WarningItemEnum implements EnumSelectable {
    // 超损预警类
    MAINTENANCE_ABNORMAL("维修费用异常", WarningCategoryEnum.OVER_LOSS),
    INSURANCE_AMOUNT_ABNORMAL("出险金额异常", WarningCategoryEnum.OVER_LOSS),
    // 上险预警类
    INSURANCE_ABNORMAL("上险异常", WarningCategoryEnum.INSURANCE),
    // 监控异常预警类
    POSITION_ABNORMAL("定位异常", WarningCategoryEnum.MONITOR),
    GPS_DEVICE_ABNORMAL("GPS设备异常", WarningCategoryEnum.MONITOR),
    VEHICLE_OFFLINE_DAYS_ABNORMAL("车辆离线天数异常", WarningCategoryEnum.MONITOR),
    GPS_DISCONNECT("GPS未接入", WarningCategoryEnum.MONITOR),
    // 事故预警类
    ACCIDENT_COMPENSATION("事故赔付", WarningCategoryEnum.ACCIDENT);


    private final String name;

    private final WarningCategoryEnum category;

    WarningItemEnum(String name, WarningCategoryEnum category) {
        this.name = name;
        this.category = category;
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
