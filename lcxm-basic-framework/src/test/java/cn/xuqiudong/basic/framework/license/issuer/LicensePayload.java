package cn.xuqiudong.basic.framework.license.issuer;

import cn.xuqiudong.basic.framework.runtime.LcPayload;
import lombok.Data;

/**
 * Description:
 * 保持和  Payload 一致
 * @see LcPayload
 * @author Vic.xu
 * @since 2026-07-27 16:28
 */
@Data
public class LicensePayload extends LcPayload {

    /**
     * 签发时间
     */
    private long issueAt;

    /**
     * 过期时间
     */
    private long expireAt;

    /**
     * 版本
     */
    private String version;

    /**
     * 用户名称等
     */
    private String subject;

    /**
     * 随机数
     */
    protected String nonce;

    /**
     * 签名
     */
    private String sign;




}
