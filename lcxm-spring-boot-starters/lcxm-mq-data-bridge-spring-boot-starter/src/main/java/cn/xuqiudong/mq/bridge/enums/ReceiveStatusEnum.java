package cn.xuqiudong.mq.bridge.enums;

/**
 * 描述:
 * 接受状态枚举： 状态（待处理、已处理、处理失败， 已丢弃）
 *
 * @author Vic.xu
 * @since 2025-03-04 10:55
 */
public enum ReceiveStatusEnum {

    /**
     * 接收到的消息json 解析错误，此时DataBridgeReceiveMessage.message 存的是MessageContentWrapper  而不是解析好的message
     */
    PARSE_ERROR("解析错误"),
    INITIAL("待处理"),
    AMENDED("已修正"),
    FAILED("处理失败"),
    SUCCESS("已处理"),
    DISCARDED("已丢弃");

    private String text;

    ReceiveStatusEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
