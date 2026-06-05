package cn.xuqiudong.basic.quartz.enums;

import cn.xuqiudong.basic.framework.select.EnumSelectable;
import cn.xuqiudong.basic.framework.select.annotation.RegisterSelectEnum;

/**
 * 描述:
 * 运行日志中：定时任务的运行状态
 *
 * @author Vic.xu
 * @since 2025-01-20 10:36
 */
@RegisterSelectEnum
public enum TaskJobLogStatus implements EnumSelectable {

    RUNNING,
    FINISHED,
    ;

    @Override
    public String getValue() {
        return name();
    }

    @Override
    public String getText() {
        return name();
    }
}
