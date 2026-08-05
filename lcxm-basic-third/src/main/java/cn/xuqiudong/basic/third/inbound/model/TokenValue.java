package cn.xuqiudong.basic.third.inbound.model;

import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Stored token value.
 *
 * @author Vic.xu
 */
@Data
@AllArgsConstructor
public class TokenValue implements Serializable {

    private static final long serialVersionUID = 1L;

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
