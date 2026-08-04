package cn.xuqiudong.basic.third.inbound.log.context;

import java.util.Collections;
import java.util.Map;

/**
 * 第三方入站调用日志上下文持有器。
 *
 * <p>使用 ThreadLocal 保存本次调用的补充信息，切面记录完成后必须清理。</p>
 *
 * @author Vic.xu
 */
public final class InboundInvokeContextHolder {

    private static final ThreadLocal<InboundInvokeContext> HOLDER = new ThreadLocal<>();

    private InboundInvokeContextHolder() {
    }

    /**
     * 获取当前线程上下文，不存在时创建。
     */
    public static InboundInvokeContext current() {
        InboundInvokeContext context = HOLDER.get();
        if (context == null) {
            context = new InboundInvokeContext();
            HOLDER.set(context);
        }
        return context;
    }

    /**
     * 写入一个上下文字段。
     */
    public static void put(String key, Object value) {
        current().put(key, value);
    }

    /**
     * 返回当前上下文快照。
     */
    public static Map<String, Object> snapshot() {
        InboundInvokeContext context = HOLDER.get();
        return context == null ? Collections.emptyMap() : context.snapshot();
    }

    /**
     * 清理当前线程上下文，避免线程复用导致数据串扰。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
