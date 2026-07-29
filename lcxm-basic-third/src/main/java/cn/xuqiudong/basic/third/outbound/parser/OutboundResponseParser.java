package cn.xuqiudong.basic.third.outbound.parser;

/**
 * 非标准第三方响应解析器。
 *
 * @author Vic.xu
 */
@FunctionalInterface
public interface OutboundResponseParser<T> {

    /**
     * 将原始响应字符串解析为业务对象。
     */
    T parse(String responseBody) throws Exception;
}
