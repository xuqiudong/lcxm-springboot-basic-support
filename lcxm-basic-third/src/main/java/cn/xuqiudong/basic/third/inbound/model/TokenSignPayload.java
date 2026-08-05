package cn.xuqiudong.basic.third.inbound.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Fields that participate in token signature.
 *
 * @author Vic.xu
 */
@Data
@Accessors(chain = true)
public class TokenSignPayload {

    private String appId;

    private String nonce;

    private long timestamp;

    private String username;


    public TokenSignPayload() {
        this.timestamp = System.currentTimeMillis();
    }
}
