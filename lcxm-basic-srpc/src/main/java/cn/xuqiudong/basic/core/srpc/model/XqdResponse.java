package cn.xuqiudong.basic.core.srpc.model;

import cn.xuqiudong.basic.core.exception.CommonException;

import java.io.Serializable;

/**
 * 描述: rpc响应对象
 *
 * @author Vic.xu
 * @date 2022-02-22 9:13
 */
public class XqdResponse<T> implements Serializable {

    private static final long serialVersionUID = -7591845740010761678L;

    private static final int DEFAULT_ERROR_CODE = 999;

    private int code;

    private String msg;

    private T data;


    public static XqdResponse error(String msg) {
        return error(DEFAULT_ERROR_CODE, msg);
    }

    public static XqdResponse error(int code, String msg) {
        return new XqdResponse(code, msg, null);
    }

    public static XqdResponse success() {
        return success(null);
    }

    public static <T> XqdResponse success(T data) {
        return new XqdResponse(0, null, data);
    }


    public XqdResponse() {
    }


    private XqdResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
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

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取远程方法返回值， 先判断code
     *
     * @return
     */
    public Object getResultData() {
        if (code != 0) {
            throw new CommonException("Error calling remote method, code:" + code + ", msg:" + msg);
        }
        return data;
    }

}
