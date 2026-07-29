package cn.xuqiudong.basic.third.outbound.executor;

import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;

/**
 * 出站请求执行器接口。
 *
 * @author Vic.xu
 */
public interface OutboundExecutor {

    /**
     * 执行 HTTP 请求并解析为业务响应对象。
     */
    <T> T execute(OutboundRequestInfo<T> request);

    /**
     * 执行 HTTP 请求并返回原始字节。
     */
    byte[] executeBytes(OutboundRequestInfo<?> request);
}
