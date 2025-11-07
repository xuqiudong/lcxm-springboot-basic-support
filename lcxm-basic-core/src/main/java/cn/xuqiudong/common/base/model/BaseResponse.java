package cn.xuqiudong.common.base.model;

import cn.xuqiudong.common.base.enums.ResultMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.style.ToStringCreator;

import java.io.Serializable;

/**
 * 返回JSON的基本格式
 *
 * @author VIC
 *
 */
public final class BaseResponse<T> implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(BaseResponse.class);

    private static final long serialVersionUID = 1L;

    /**
     * 状态码 0为正确
     */
    private int code;

    /**
     * 错误提示信息
     */
    private String msg;

    /**
     * 数据对象
     */
    private T data;

    public static <T> BaseResponse<T> error(ResultMsg msg) {
        return error(msg.getCode(), msg.getMsg());
    }

    public static <T> BaseResponse<T> judge(boolean judge) {
        return judge ? success() : error();
    }

    /** 构建操作成功的对象 */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(0, "", data);
    }

    /** 构建操作成功的对象 */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<T>(0, "");
    }

    public static <T> BaseResponse<T> error() {
        return error(999, "");
    }

    /** 构建操作失败的对象 */
    public static <T> BaseResponse<T> error(String msg) {
        return error(999, msg);
    }

    /** 构建操作失败的对象 */
    public static <T> BaseResponse<T> error(int code, String msg) {
        return new BaseResponse<T>(code, msg);
    }

    /**
     * 转JSON
     */
    public String toJson() {
        return new ToStringCreator(this)
                .append("code", code)
                .append("msg", msg)
                //  如果 data 是 User 这种复杂对象，会自动简化
                .append("data", data)
                .toString();
    }


    @Override
    public String toString() {
        return this.toJson();
    }

    public BaseResponse() {
    }

    private BaseResponse(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    private BaseResponse(int code, String msg, T data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse<?> appendMsg(String msg) {
        this.msg += msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public BaseResponse<?> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public BaseResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return 0 == code;
    }


}
