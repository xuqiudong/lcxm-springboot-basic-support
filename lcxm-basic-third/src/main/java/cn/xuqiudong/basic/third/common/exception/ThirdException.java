package cn.xuqiudong.basic.third.common.exception;

/**
 * Base runtime exception for third-party interaction.
 *
 * @author Vic.xu
 */
public class ThirdException extends RuntimeException {

    public ThirdException(String message) {
        super(message);
    }

    public ThirdException(String message, Throwable cause) {
        super(message, cause);
    }
}
