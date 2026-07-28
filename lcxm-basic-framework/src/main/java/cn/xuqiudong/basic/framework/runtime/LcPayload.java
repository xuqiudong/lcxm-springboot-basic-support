package cn.xuqiudong.basic.framework.runtime;

import lombok.Data;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-07-28 11:28
 */
@Data
public class LcPayload {

    protected long issueAt;
    protected long expireAt;
    protected String version;
    protected String subject;
    protected String nonce;

    protected String sign;

    public boolean expired() {
        return System.currentTimeMillis() > expireAt;
    }

}
