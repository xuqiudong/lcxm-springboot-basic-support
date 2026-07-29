package cn.xuqiudong.basic.third.security;

import cn.hutool.core.util.URLUtil;
import cn.xuqiudong.basic.third.inbound.model.TokenSignPayload;

/**
 * 签名原文构建工具。
 *
 * @author Vic.xu
 */
public final class SignaturePayloadBuilder {

    private SignaturePayloadBuilder() {
    }

    /**
     * 根据 token 申请请求构建签名原文。
     */
    public static String buildTokenPayload(TokenSignPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("payload can not be null");
        }
        return buildTokenPayload(payload.getAppId(), payload.getNonce(), payload.getTimestamp(), payload.getUsername());
    }

    /**
     * 按固定字段顺序构建 token 签名原文。
     */
    public static String buildTokenPayload(String appId, String nonce, long timestamp, String username) {
        if (appId == null || appId.isBlank()) {
            throw new IllegalArgumentException("appId can not be blank");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username can not be blank");
        }
        return "appId=" + encode(appId)
                + "&timestamp=" + timestamp
                + "&nonce=" + encode(nonce)
                + "&username=" + encode(username);
    }

    /**
     * 对参与签名的字符串字段做 query 参数编码。
     */
    private static String encode(String value) {
        return URLUtil.encodeQuery(value == null ? "" : value);
    }
}
