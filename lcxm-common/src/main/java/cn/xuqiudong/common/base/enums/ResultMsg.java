package cn.xuqiudong.common.base.enums;

/**
 * 描述: 状态码枚举的父接口
 * @author Vic.xu
 * @since 2022-03-01 9:38
 */
public interface ResultMsg {

    /**
     * 状态码
     * @return
     */
    int getCode();

    /**
     * 错误说明
     * @return
     */
    String getMsg();
}
