package cn.xuqiudong.basic.core.web.intercept.log;

import cn.xuqiudong.basic.core.tool.Tools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 描述:slf4j日志加入TraceId
 * <p>
 *  比如：logback 中：   <pattern>[%X{traceId}].....</pattern>
 * </p>
 * @see  <a href="https://blog.csdn.net/weixin_36380516/article/details/128179968"></a>
 * @author Vic.xu
 * @since 2022-12-12 11:08
 */
public class TraceUtils {

    private static final String TRACE_ID = "traceId";

    private static final Logger logger = LoggerFactory.getLogger(TraceUtils.class);

    public static void createTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = Tools.randomUuid();
            logger.debug("create traceId :{}", traceId);
            MDC.put(TRACE_ID, traceId);
        }
    }

    public static void destroyTraceId() {
        MDC.remove(TRACE_ID);
    }
}
