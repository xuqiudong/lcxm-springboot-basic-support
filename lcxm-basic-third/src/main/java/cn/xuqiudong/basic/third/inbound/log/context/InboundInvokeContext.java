package cn.xuqiudong.basic.third.inbound.log.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;

/**
 * 一次第三方入站调用中的业务上下文。
 *
 * <p>用于业务代码补充 thirdCode、bizType、orderNo 等差异化信息，公共模块不预设字段含义。</p>
 *
 * @author Vic.xu
 */
public class InboundInvokeContext {

    private final Map<String, Object> values = new LinkedHashMap<>();

    /**
     * 写入一个上下文字段。
     */
    public InboundInvokeContext put(String key, Object value) {
        if (StrUtil.isNotBlank(key) && value != null) {
            values.put(key, value);
        }
        return this;
    }

    /**
     * 返回当前上下文快照。
     */
    public Map<String, Object> snapshot() {
        if (values.isEmpty()) {
            return Collections.emptyMap();
        }
        return new LinkedHashMap<>(values);
    }
}
