package cn.xuqiudong.basic.third.outbound.model;

/**
 * 出站请求体类型。
 *
 * <p>用于明确默认执行器如何写入请求体和补充 Content-Type。</p>
 *
 * @author Vic.xu
 */
public enum OutboundRequestType {

    /**
     * 无请求体。
     */
    NONE(null),

    /**
     * query 参数请求，请求参数拼到 URL。
     */
    QUERY(null),

    /**
     * JSON 请求体。
     */
    JSON("application/json"),

    /**
     * 表单请求体。
     */
    FORM("application/x-www-form-urlencoded"),

    /**
     * 纯文本请求体。
     */
    TEXT("text/plain"),

    /**
     * 二进制请求体。
     */
    BYTES("application/octet-stream");

    private final String contentType;

    OutboundRequestType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
