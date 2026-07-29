package cn.xuqiudong.basic.third.inbound.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Token issuing result.
 *
 * @author Vic.xu
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenIssueResult {

    private boolean success;

    private String token;

    private String message;

    public static TokenIssueResult success(String token) {
        return new TokenIssueResult(true, token, null);
    }

    public static TokenIssueResult failed(String message) {
        return new TokenIssueResult(false, null, message);
    }
}
