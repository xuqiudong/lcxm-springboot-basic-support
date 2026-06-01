package cn.xuqiudong.basic.quartz.enums;

import cn.xuqiudong.basic.framework.select.EnumSelectable;
import cn.xuqiudong.basic.framework.select.annotation.RegisterSelectEnum;

/**
 * 描述: 任务状态 或动作
 *
 * @author Vic.xu
 * @since 2025-01-17 17:00
 */
@RegisterSelectEnum
public enum QuartzStatusEnum implements EnumSelectable {


    WORKING("运行"),
    PAUSE("暂停"),
    REMOVE("删除");

    private String text;

    QuartzStatusEnum(String text) {
        this.text = text;
    }

    @Override
    public String getValue() {
        return name();
    }

    public String getText() {
        return text;
    }
}
