package cn.xuqiudong.basic.third.log.model;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 第三方出站请求交换日志模型。
 *
 * @author Vic.xu
 */
@Data
@Accessors(chain = true)
public class ThirdExchangeLog {

    /**
     * 第三方编码。
     */
    private String thirdCode;

    /**
     * 第三方名称。
     */
    private String thirdName;

    /**
     * 业务操作名。
     */
    private String operation;

    /**
     * HTTP method。
     */
    private String method;

    /**
     * 完整请求 URL。
     */
    private String url;

    /**
     * HTTP 响应状态码；请求未发送成功时可能为空。
     */
    private Integer httpStatus;

    /**
     * 请求耗时，单位毫秒。
     */
    private long elapsedMillis;

    /**
     * 交换结果状态。
     */
    private ThirdExchangeStatus status;

    /**
     * 请求 body；文件或字节响应场景可能为空。
     */
    private String requestBody;

    /**
     * 响应 body；字节响应场景不记录。
     */
    private String responseBody;

    /**
     * 异常信息；成功时为空。
     */
    private String errorMessage;

    /**
     * 日志创建时间。
     */
    private Date createTime = DateUtil.date();
}
