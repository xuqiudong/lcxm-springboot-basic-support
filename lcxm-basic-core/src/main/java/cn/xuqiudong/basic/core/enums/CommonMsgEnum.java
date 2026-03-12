package cn.xuqiudong.basic.core.enums;

/**
 * 通用的一些状态码
 * @author VIC
 *
 */
public enum CommonMsgEnum implements ResultMsg {

    /**求参数错误*/
    PARAM_ERROR(400, "请求参数错误!"),
    /**当前操作需要先登陆*/
    NOT_LOGIN(401, "当前操作需要先登陆！"),
    /**服务器出了点小问题*/
    INTERNAL_SERVER_ERROR(500, "服务器出了点小问题"),
    /**表单重复提交*/
    REPEAT_COMMIT(1001, "表单重复提交");

    private final int code;

    private final String msg;


    CommonMsgEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
