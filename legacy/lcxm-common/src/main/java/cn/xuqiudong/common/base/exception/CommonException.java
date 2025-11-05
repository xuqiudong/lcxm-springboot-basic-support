/**
 *
 */
package cn.xuqiudong.common.base.exception;

import cn.xuqiudong.common.base.enums.ResultMsg;

/**
 * 说明 :  通用的基本的异常 包含code和MSG 对应 @see BaseResponse
 * @author Vic.xu
 * @since  2020年7月22日上午11:27:28
 */
public class CommonException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code = 999;

    public CommonException() {
        super();
    }

    /**
     * @param code
     */
    public CommonException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public CommonException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }


    public CommonException(Throwable cause) {
        super(cause);
    }

    public CommonException(ResultMsg msg) {
        this(msg.getCode(), msg.getMsg());
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public CommonException setCode(int code) {
        this.code = code;
        return this;
    }

}
