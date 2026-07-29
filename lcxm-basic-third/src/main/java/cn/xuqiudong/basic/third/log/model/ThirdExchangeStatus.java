package cn.xuqiudong.basic.third.log.model;

/**
 * 第三方出站交换结果状态。
 *
 * @author Vic.xu
 */
public enum ThirdExchangeStatus {
    /**
     * 请求成功且 HTTP 状态码小于 400。
     */
    SUCCESS,

    /**
     * 请求异常、响应解析异常或 HTTP 状态码大于等于 400。
     */
    FAILED
}
