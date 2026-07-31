package cn.xuqiudong.basic.third.outbound.builder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.util.StrUtil;

/**
 * 出站普通参数构建器。
 *
 * <p>只用于 query/form 这类普通键值参数，减少业务代码手写 Map。
 * 文件上传使用 {@link OutboundRequestInfoBuilder#multipartFile(String, java.io.File)} 等方法，
 * header 使用 {@link OutboundRequestInfoBuilder#header(String, String)} 或厂商 Client 的 buildHeaders。</p>
 *
 * @author Vic.xu
 */
public class OutboundParams {

    private final Map<String, Object> params = new LinkedHashMap<>();

    private OutboundParams() {
    }

    /**
     * 创建普通参数构建器。
     */
    public static OutboundParams create() {
        return new OutboundParams();
    }

    /**
     * 添加非空参数。
     */
    public OutboundParams add(String name, Object value) {
        return addIfNotNull(name, value);
    }

    /**
     * value 不为 null 时添加参数。
     */
    public OutboundParams addIfNotNull(String name, Object value) {
        if (StrUtil.isNotBlank(name) && value != null) {
            params.put(name, value);
        }
        return this;
    }

    /**
     * value 不为空白字符串时添加参数。
     */
    public OutboundParams addIfNotBlank(String name, String value) {
        if (StrUtil.isNotBlank(name) && StrUtil.isNotBlank(value)) {
            params.put(name, value);
        }
        return this;
    }

    /**
     * 批量添加非空参数。
     */
    public OutboundParams addAll(Map<String, ?> source) {
        if (source != null) {
            source.forEach(this::addIfNotNull);
        }
        return this;
    }

    /**
     * 返回不可变参数 Map。
     */
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(params));
    }
}
