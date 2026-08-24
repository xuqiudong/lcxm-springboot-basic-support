package cn.xuqiudong.basic.secureid.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 对id数据进行解密过滤器，
 * 注意 加解密的SALT的来源，如果是来自某个Filter, 则应该放在此Filter之前
 *
 * 要不要使用：OncePerRequestFilter  内部转发的时候要不要再次解密
 * @author Vic.xu
 * @since 2026-08-21 9:05
 */
public class ParamDecryptFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ParamDecryptFilter.class);

    /**
     * 不拦截的URL
     */
    private static final List<String> UNFILTER_URLS = Collections.unmodifiableList(Arrays.asList(
            "/js/", "/css", "/images/"));

    private final MultipartResolver resolver;

    /**
     * 构造请求参数解密过滤器。
     * <p>
     * 文件表单只能可靠解析一次，因此这里强制使用业务项目配置好的 MultipartResolver，
     * 避免 filter 内部自行创建 resolver 导致上传配置不一致。
     *
     * @param resolver 业务项目中的 multipartResolver bean
     */
    public ParamDecryptFilter(MultipartResolver resolver) {
        Assert.notNull(resolver, "multipartResolver must not be null");
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String url = request.getRequestURI();
        for (String s : UNFILTER_URLS) {
            if (url.contains(s)) {
                chain.doFilter(request, response);
                return;
            }
        }

        Map<String, String[]> paramMap = request.getParameterMap();
        String jsonBody = null;
        String enctype = request.getContentType();
        MultipartHttpServletRequest resolvedMultipartRequest = null;

        //1 文件表单的特殊处理
        if (StringUtils.isNotBlank(enctype) && enctype.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            MultipartHttpServletRequest req = resolveMultipartRequest(request);
            if (req != null) {
                if (req != request) {
                    resolvedMultipartRequest = req;
                }
                paramMap = req.getParameterMap();
                request = req;
            }
        }
        //2. 如果是JSON格式的请求数据
        boolean isJsonRequest = StringUtils.isNotBlank(enctype)
                && enctype.contains(MediaType.APPLICATION_JSON_VALUE)
                && !HttpMethod.GET.name().equalsIgnoreCase(request.getMethod());
        if (isJsonRequest) {
            jsonBody = getRequestJsonString((HttpServletRequest) request);
        }
        //如果参数为null  json 请求体也是null 则不处理
        if (MapUtils.isEmpty(paramMap) && jsonBody == null) {
            try {
                chain.doFilter(request, response);
            } finally {
                cleanupMultipart(resolvedMultipartRequest);
            }
            return;
        }
        request = new ParameterRequestWrapper(request, jsonBody, paramMap);
        try {
            chain.doFilter(request, response);
        } finally {
            cleanupMultipart(resolvedMultipartRequest);
        }

    }

    private MultipartHttpServletRequest resolveMultipartRequest(HttpServletRequest request) {
        if (request instanceof MultipartHttpServletRequest) {
            return (MultipartHttpServletRequest) request;
        }
        if (!resolver.isMultipart(request)) {
            return null;
        }
        // 只有在当前请求尚未被解析、且项目提供了 MultipartResolver 时才主动解析。
        // 这样才能在 controller 之前拿到文件表单中的普通字段；文件内容不读取、不修改。
        return resolver.resolveMultipart(request);
    }

    private void cleanupMultipart(MultipartHttpServletRequest request) {
        if (request == null) {
            return;
        }
        resolver.cleanupMultipart(request);
    }

    /* ************************获取参数的一些方法↓↓↓↓↓↓************************************* */


    /***
     * 获取 request 中 JSON 字符串的内容
     *
     */
    private static String getRequestJsonString(HttpServletRequest request) throws IOException {
        return getRequestPostStr(request);
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     */
    private static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        // 不能依赖 contentLength：chunked 请求、网关转发或某些容器场景下可能为 -1。
        // 这里按 input stream 读取到 EOF，保证 JSON body 被完整缓存后再交给 wrapper 重复读取。
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int readLength;
        while ((readLength = request.getInputStream().read(buffer)) != -1) {
            outputStream.write(buffer, 0, readLength);
        }
        return outputStream.toByteArray();
    }

    /**
     * 描述:获取 post 请求内容
     */
    private static String getRequestPostStr(HttpServletRequest request) throws IOException {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = StandardCharsets.UTF_8.name();
        }
        return new String(buffer, charEncoding);
    }
}
