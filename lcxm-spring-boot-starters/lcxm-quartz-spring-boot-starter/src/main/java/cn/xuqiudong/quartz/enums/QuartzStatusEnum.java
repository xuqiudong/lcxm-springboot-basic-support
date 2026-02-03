package cn.xuqiudong.quartz.enums;

/**
 * 描述: 任务状态 或动作
 *
 * @author Vic.xu
 * @since 2025-01-17 17:00
 */
public enum QuartzStatusEnum {

    WORKING("运行"),
    PAUSE("暂停"),
    REMOVE("删除");

    private String text;

    private QuartzStatusEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
