package cn.xuqiudong.basic.framework.runtime;

import cn.hutool.core.util.URLUtil;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

/**
 * 功能描述
 * 构建签名前的待签名字符串(载荷)
 *
 * @author Vic.xu
 * @see RsaSignatureUtils#privateSign(String, String)  中的data
 * @since 2026-05-11
 */
public class SignaturePayloadBuilder {

    private SignaturePayloadBuilder() {
    }


    public static String buildIssuerPayload(LcPayload payload) {
        Assert.notNull(payload, "payload can not be null");
        return buildIssuerPayload(payload.getSubject(), payload.getVersion(),
                payload.getIssueAt(), payload.getExpireAt(), payload.getNonce());
    }


    public static String buildIssuerPayload(String subject, String version, long issueAt, long expireAt, String nonce) {
        return "subject=" + encode(subject)
                + "&issueAt=" + issueAt
                + "&expireAt=" + expireAt
                + "&nonce=" + encode(nonce)
                + "&version=" + encode(version);
    }

    /**
     * 没有特殊字符的
     * 此处保留通用性代码
     */
    private static String encode(String value) {
        return URLUtil.encode(value, StandardCharsets.UTF_8);
    }
}
