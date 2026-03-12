package cn.xuqiudong.basic.trial.kjlink.warning.enums;

import cn.xuqiudong.basic.core.select.EnumSelectable;
import cn.xuqiudong.basic.core.select.annotation.RegisterSelectEnum;
import lombok.Getter;

/**
 * 描述:
 * 预警规则枚举
 *
 * @author Vic.xu
 * @since 2025-12-05 9:44
 */
@Getter
@RegisterSelectEnum
public enum WarningRuleEnum implements EnumSelectable {

    // ---------------------- 超损预警-维修费用异常 ----------------------
    MAINTENANCE_ABNORMAL_MINOR(
            WarningItemEnum.MAINTENANCE_ABNORMAL,
            WarningLevel.MINOR,
            2,
            "20.00,30.00",
            "租赁期内维修费用在购买价款（含税）{0}%-{1}%时触发",
            "租赁期内租赁物维修费用在购买价款（含税）{0}%-{1}%，触发轻微预警"
    ),
    MAINTENANCE_ABNORMAL_SERIOUS(
            WarningItemEnum.MAINTENANCE_ABNORMAL,
            WarningLevel.SERIOUS,
            1,
            "30.00",
            "租赁期内维修费用超过购买价款（含税）{0}%时触发",
            "租赁期内租赁物维修费用超过购买价款（含税）{0}%，触发严重预警"
    ),
    // ---------------------- 超损预警-出险金额异常 ----------------------
    INSURANCE_AMOUNT_ABNORMAL_MINOR(
            WarningItemEnum.INSURANCE_AMOUNT_ABNORMAL,
            WarningLevel.MINOR,
            2,
            "20.00,30.00",
            "租赁期内单次车损出险金额在购买价款（含税）{0}%-{1}%时触发",
            "租赁期内租赁物单次车损出险金额在购买价款（含税）{0}%-{1}%，触发轻微预警"
    ),
    INSURANCE_AMOUNT_ABNORMAL_SERIOUS(
            WarningItemEnum.INSURANCE_AMOUNT_ABNORMAL,
            WarningLevel.SERIOUS,
            1,
            "30.00",
            "租赁期内单次车损出险金额超过购买价款（含税）{0}%时触发",
            "租赁期内租赁物单次车损出险金额超过购买价款（含税）{0}%，触发严重预警"
    ),
    // ---------------------- 上险预警-上险异常 ----------------------
    INSURANCE_ABNORMAL_MINOR(
            WarningItemEnum.INSURANCE_ABNORMAL,
            WarningLevel.MINOR,
            2,
            "3,6",
            "起租{0}-{1}个月未上保险时触发",
            "租赁物起租{0}-{1}个月未上保险，触发轻微预警"
    ),
    INSURANCE_ABNORMAL_SERIOUS_1(
            WarningItemEnum.INSURANCE_ABNORMAL,
            WarningLevel.SERIOUS,
            1,
            "6",
            "起租超{0}个月未上保险时触发",
            "租赁物起租超{0}个月未上保险，触发严重预警"
    ),
    INSURANCE_ABNORMAL_SERIOUS_2(
            WarningItemEnum.INSURANCE_ABNORMAL,
            WarningLevel.SERIOUS,
            0,
            "",
            "交强险过期时触发",
            "租赁物交强险已过期，触发严重预警"
    ),
    // ---------------------- 监控异常预警-定位异常 ----------------------
    POSITION_ABNORMAL_MINOR(
            WarningItemEnum.POSITION_ABNORMAL,
            WarningLevel.MINOR,
            1,
            "48",
            "非授权偏离路线未处于指定区域超过{0}小时时触发",
            "租赁物非授权偏离路线，未处于指定区域超过{0}小时，触发轻微预警"
    ),
    POSITION_ABNORMAL_SERIOUS(
            WarningItemEnum.POSITION_ABNORMAL,
            WarningLevel.SERIOUS,
            1,
            "72",
            "非授权偏离路线未处于指定区域超过{0}小时时触发",
            "租赁物非授权偏离路线，未处于指定区域超过{0}小时，触发严重预警"
    ),
    // ---------------------- 监控异常预警-GPS设备异常 ----------------------
    GPS_DEVICE_ABNORMAL_MINOR(
            WarningItemEnum.GPS_DEVICE_ABNORMAL,
            WarningLevel.MINOR,
            1,
            "48",
            "GPS设备断联/损坏超过{0}小时时触发",
            "租赁物GPS设备断联/损坏超过{0}小时，触发轻微预警"
    ),
    GPS_DEVICE_ABNORMAL_SERIOUS(
            WarningItemEnum.GPS_DEVICE_ABNORMAL,
            WarningLevel.SERIOUS,
            1,
            "72",
            "GPS设备断联/损坏超过{0}小时时触发",
            "租赁物GPS设备断联/损坏超过{0}小时，触发严重预警"
    ),
    // ---------------------- 监控异常预警-车辆离线天数异常 ----------------------
    VEHICLE_OFFLINE_DAYS_ABNORMAL_MINOR(
            WarningItemEnum.VEHICLE_OFFLINE_DAYS_ABNORMAL,
            WarningLevel.MINOR,
            2,
            "30,90",
            "车辆离线天数在{0}-{1}天时触发",
            "租赁物离线天数在{0}-{1}天，触发轻微预警"
    ),
    VEHICLE_OFFLINE_DAYS_ABNORMAL_MEDIUM(
            WarningItemEnum.VEHICLE_OFFLINE_DAYS_ABNORMAL,
            WarningLevel.MEDIUM,
            2,
            "90,120",
            "车辆离线天数在{0}-{1}天时触发",
            "租赁物离线天数在{0}-{1}天，触发中等预警"
    ),
    VEHICLE_OFFLINE_DAYS_ABNORMAL_SERIOUS(
            WarningItemEnum.VEHICLE_OFFLINE_DAYS_ABNORMAL,
            WarningLevel.SERIOUS,
            1,
            "120",
            "车辆离线天数超过{0}天时触发",
            "租赁物离线天数超过{0}天，触发严重预警"
    ),
    // ---------------------- 监控异常预警-GPS未接入 ----------------------
    GPS_DISCONNECT_SERIOUS(
            WarningItemEnum.GPS_DISCONNECT,
            WarningLevel.SERIOUS,
            0,
            "",
            "GPS未接入且出险/维修时触发",
            "租赁物GPS未接入且发生出险/维修，触发严重预警"
    ),
    // ---------------------- 事故预警-事故赔付 ----------------------
    ACCIDENT_COMPENSATION_SERIOUS_1(
            WarningItemEnum.ACCIDENT_COMPENSATION,
            WarningLevel.SERIOUS,
            1,
            "5000",
            "事故赔付金额超过{0}元时触发",
            "租赁物事故赔付金额超过{0}元，触发严重预警"
    ),
    ACCIDENT_COMPENSATION_SERIOUS_2(
            WarningItemEnum.ACCIDENT_COMPENSATION,
            WarningLevel.SERIOUS,
            0,
            "",
            "触发主责（死、伤）事故时触发",
            "租赁物发生主责（死、伤）事故，触发严重预警"
    );

    ;


    /**
     * 关联的预警事项
     */
    WarningItemEnum item;
    /**
     * 预警级别
     */
    WarningLevel warningLevel;
    /**
     * 阈值数量
     */
    int thresholdCount;
    /**
     * 默认阈值（逗号分隔，无则空）
     */
    String defaultThreshold;
    /**
     * 规则描述（含占位符） 占位符为 MessageFormat
     */
    String text;
    /**
     * 预警内容
     */
    String warningContent;

    WarningRuleEnum(WarningItemEnum item, WarningLevel warningLevel,
                    int thresholdCount, String defaultThreshold, String text, String warningContent) {
        this.item = item;
        this.warningLevel = warningLevel;
        this.thresholdCount = thresholdCount;
        this.defaultThreshold = defaultThreshold;
        this.text = text;
        this.warningContent = warningContent;
    }

    @Override
    public String getValue() {
        return name();
    }

    /**
     * 获取预警描述： 占位符替换成对应的值
     *
     */
    @Override
    public String getText() {
       return text;
    }


}
