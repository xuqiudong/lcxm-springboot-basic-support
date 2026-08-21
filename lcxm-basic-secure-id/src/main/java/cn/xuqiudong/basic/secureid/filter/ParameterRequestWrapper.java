package cn.xuqiudong.basic.secureid.filter;

import cn.xuqiudong.basic.secureid.util.IdUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
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
    Logger logger = LoggerFactory.getLogger(ParameterRequestWrapper.class);

    private Map<String, String[]> params;

    /**
     * 持有json 请求体
     */
    private byte[] body = new byte[0];


    public ParameterRequestWrapper(HttpServletRequest request, Map<String, String[]> newParams) {
        super(request);
        //如果直接 = newParams 在后面向params中put参数的时候可能会报错:.....locaked ParamerMap
        if (MapUtils.isEmpty(newParams)) {
            newParams = new HashMap<>();
        }
        this.params = new HashMap<>(newParams);
        renewParameterMap(request);
    }

    /**
     *
     * @param request HttpServletRequest
     * @param json json请求的时候的body  允许为null
     * @param newParams 请求参数键值对  允许为null
     */
    public ParameterRequestWrapper(@NotNull HttpServletRequest request, @Nullable String json,
                                   @Nullable Map<String, String[]> newParams) {
        this(request, newParams);
        if (StringUtils.isNotBlank(json)) {
            json = IdUtil.decrypt(json);
            body = json.getBytes(StandardCharsets.UTF_8);
        }
    }


    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }


    @Override
    public String getParameter(String name) {
        String result = "";

        Object v = params.get(name);
        if (v == null) {
            result = null;
        } else if (v instanceof String[]) {
            String[] strArr = (String[]) v;
            if (strArr.length > 0) {
                result = strArr[0];
            } else {
                result = null;
            }
        } else if (v instanceof String) {
            result = (String) v;
        } else {
            result = v.toString();
        }

        return result;
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
        String[] result = null;

        Object v = params.get(name);
        if (v == null) {
            result = null;
        } else if (v instanceof String[]) {
            result = (String[]) v;
        } else if (v instanceof String) {
            result = new String[]{(String) v};
        } else {
            result = new String[]{v.toString()};
        }

        return result;
    }

    private void renewParameterMap(HttpServletRequest req) {

        String queryString = req.getQueryString();
        //加入新的参数
        if (queryString != null && queryString.trim().length() > 0) {
            String[] params = queryString.split("&");

            for (int i = 0; i < params.length; i++) {
                int splitIndex = params[i].indexOf("=");
                if (splitIndex == -1) {
                    continue;
                }

                String key = params[i].substring(0, splitIndex);

                if (!this.params.containsKey(key)) {
                    if (splitIndex < params[i].length()) {
                        String value = params[i].substring(splitIndex + 1);
                        this.params.put(key, new String[]{value});
                    }
                }
            }
        }
        // 过滤参数
        if (this.params != null) {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String[] values = entry.getValue();
                if (values != null) {
                    int len = values.length;
                    for (int i = 0; i < len; i++) {
                        values[i] = IdUtil.decrypt(values[i]);
                    }
                }
            }
        }

    }
}
