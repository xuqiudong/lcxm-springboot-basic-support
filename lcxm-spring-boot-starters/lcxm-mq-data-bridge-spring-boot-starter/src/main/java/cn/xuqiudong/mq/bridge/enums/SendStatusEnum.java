package cn.xuqiudong.mq.bridge.enums;

/**
 * 描述:
 * 发送状态： 初始、发送失败 、已发送、已丢弃
 *
 * @author Vic.xu
 * @since 2025-03-04 10:49
 */
public enum SendStatusEnum {

    INITIAL("待发送"),
    AMENDED("已修正"),
    FAILED("发送失败"),
    SUCCESS("已发送"),
    DISCARDED("已丢弃");

    private String desc;

    SendStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
