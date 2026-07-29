package cn.xuqiudong.basic.third.inbound.model;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Stored token value.
 *
 * @author Vic.xu
 */
@Data
@AllArgsConstructor
public class TokenValue {

    private String appId;

    private String thirdCode;

    private String username;

    /**
     * Token expiration time.
     */
    private Date expireAt;

    public boolean isExpired() {
        return expireAt != null && DateUtil.date().after(expireAt);
    }
}
