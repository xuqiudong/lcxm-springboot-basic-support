package cn.xuqiudong.basic.third.common.exception;

/**
 * 第三方交互统一运行时异常。
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
