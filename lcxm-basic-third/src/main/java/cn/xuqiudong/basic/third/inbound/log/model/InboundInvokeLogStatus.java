package cn.xuqiudong.basic.third.inbound.log.model;

/**
 * 第三方入站调用结果。
 *
 * @author Vic.xu
 */
public enum InboundInvokeLogStatus {

    /**
     * 方法正常返回。
     */
    SUCCESS,

    /**
     * 方法执行异常。
     */
    FAILED
}
