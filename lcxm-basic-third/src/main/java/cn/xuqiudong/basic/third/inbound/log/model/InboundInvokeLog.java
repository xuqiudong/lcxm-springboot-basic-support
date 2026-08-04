package cn.xuqiudong.basic.third.inbound.log.model;

import java.util.Date;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 第三方入站业务方法调用日志。
 *
 * @author Vic.xu
 */
@Data
@Accessors(chain = true)
public class InboundInvokeLog {

    /**
     * 本次调用标识。
     */
    private String requestId;

    /**
     * 业务操作名称。
     */
    private String operation;

    /**
     * 第三方标识。
     */
    private String thirdCode;

    /**
     * 业务补充上下文，例如 thirdCode、bizType、orderNo、outRequestNo。
     */
    private Map<String, Object> context;

    /**
     * 方法入参序列化结果。
     */
    private String requestParams;

    /**
     * 方法出参序列化结果。
     */
    private String responseParams;

    /**
     * 异常类名。
     */
    private String exceptionClass;

    /**
     * 异常信息。
     */
    private String exceptionMessage;

    /**
     * 异常堆栈。
     */
    private String exceptionStack;

    /**
     * 调用结果。
     */
    private InboundInvokeLogStatus status;

    /**
     * 方法耗时，单位毫秒。
     */
    private long elapsedMillis;

    /**
     * 日志创建时间。
     */
    private Date createTime = DateUtil.date();
}
