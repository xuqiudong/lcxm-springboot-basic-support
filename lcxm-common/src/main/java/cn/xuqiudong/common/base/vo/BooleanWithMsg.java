package cn.xuqiudong.common.base.vo;

import java.io.Serializable;

/**
 * 带备注的boolean类型
 * @author Vic.xu
 */
public class BooleanWithMsg implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private boolean success;

    private String message;

    private int msgCode = 0;


    public BooleanWithMsg() {
        super();
    }

    public static BooleanWithMsg success() {
        return new BooleanWithMsg(Boolean.TRUE, "");
    }

    public static BooleanWithMsg success(String message) {
        return new BooleanWithMsg(Boolean.TRUE, message);
    }

    public static BooleanWithMsg fail(String message) {
        return new BooleanWithMsg(Boolean.FALSE, message);
    }

    public static BooleanWithMsg fail(String message, int msgCode) {
        return new BooleanWithMsg(Boolean.FALSE, message, msgCode);
    }

    private BooleanWithMsg(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }

    private BooleanWithMsg(boolean success, String message, int msgCode) {
        this(success, message);
        this.msgCode = msgCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public BooleanWithMsg setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BooleanWithMsg setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getMsgCode() {
        return msgCode;
    }

    public BooleanWithMsg setMsgCode(int msgCode) {
        this.msgCode = msgCode;
        return this;
    }

    @Override
    public String toString() {
        return "BooleanWithMsg [success=" + success + ", message=" + message + ", msgCode=" + msgCode + "]";
    }


}
