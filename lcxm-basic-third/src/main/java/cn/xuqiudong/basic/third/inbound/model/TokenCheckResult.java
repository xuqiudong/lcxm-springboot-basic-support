package cn.xuqiudong.basic.third.inbound.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Token checking result.
 *
 * @author Vic.xu
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenCheckResult {

    private boolean success;

    private TokenValue tokenValue;

    private String message;

    public static TokenCheckResult success(TokenValue tokenValue) {
        return new TokenCheckResult(true, tokenValue, null);
    }

    public static TokenCheckResult failed(String message) {
        return new TokenCheckResult(false, null, message);
    }
}
