package cn.xuqiudong.basic.secureid.filter;

import cn.xuqiudong.basic.secureid.util.IdUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Description:
 * id 解密参数包装
 * @author Vic.xu
 * @since 2026-08-21 9:02
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {
    private static final Logger logger = LoggerFactory.getLogger(ParameterRequestWrapper.class);

    private final Map<String, String[]> params;

    /**
     * 持有重建后的 JSON 请求体。
     * <p>
     * 为 null 表示当前 wrapper 不接管 body，getInputStream/getReader 继续委托原 request；
     * 非 null 表示 filter 已经读取过 body，后续读取必须从这里重复读取。
     */
    private final byte[] body;


    public ParameterRequestWrapper(HttpServletRequest request, Map<String, String[]> newParams) {
        this(request, null, newParams);
    }

    /**
     *
     * @param request HttpServletRequest
     * @param json json请求的时候的body  允许为null
     * @param newParams 请求参数键值对  允许为null
     */
    public ParameterRequestWrapper(@NotNull HttpServletRequest request, @Nullable String json,
                                   @Nullable Map<String, String[]> newParams) {
        super(request);
        this.params = decryptParameterMap(newParams);
        this.body = json == null ? null : IdUtil.decrypt(json).getBytes(StandardCharsets.UTF_8);
    }


    @Override
    public BufferedReader getReader() throws IOException {
        if (body == null) {
            return super.getReader();
        }
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (body == null) {
            return super.getInputStream();
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 当前 wrapper 基于内存字节数组同步读取，暂不支持异步 ReadListener 回调。
            }
        };
    }


    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector<>(params.keySet()).elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    private Map<String, String[]> decryptParameterMap(@Nullable Map<String, String[]> source) {
        Map<String, String[]> result = new HashMap<>();
        if (MapUtils.isEmpty(source)) {
            return result;
        }
        for (Map.Entry<String, String[]> entry : source.entrySet()) {
            String[] values = entry.getValue();
            if (values == null) {
                result.put(entry.getKey(), null);
                continue;
            }
            String[] copiedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                copiedValues[i] = decryptQuietly(values[i]);
            }
            result.put(entry.getKey(), copiedValues);
        }
        return result;
    }

    private String decryptQuietly(String value) {
        try {
            return IdUtil.decrypt(value);
        } catch (Exception e) {
            logger.debug("secure id request parameter decrypt failed, keep original value", e);
            return value;
        }
    }
}
