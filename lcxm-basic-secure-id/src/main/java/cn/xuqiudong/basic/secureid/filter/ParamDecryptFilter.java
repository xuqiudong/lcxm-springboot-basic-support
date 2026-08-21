package cn.xuqiudong.basic.secureid.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
public class ParamDecryptFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(ParamDecryptFilter.class);

    /**
     * 不拦截的URL
     */
    public static String[] UNFILTER_URLS = {"/js/", "/css",
            "/images/"/*, "/moblie/", "/svn/", "/base/" */};

    /**
     * 分号实现匿名访问非授权网址
     */
    public static String[] US_OTHERS = {"/js/", "/js;", "/css/", "/css;", "/images/",
            "/images;"};

    private MultipartResolver resolver;

    private static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";

    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String url = ((HttpServletRequest) request).getRequestURI();
        for (String s : US_OTHERS) {
            if (url.contains(s) && url.contains(";")) {
                logger.warn("ICBCSTL-53723");
                return;
            }
        }
        for (String s : UNFILTER_URLS) {
            if (url.contains(s)) {
                chain.doFilter(request, response);
                return;
            }
        }

        Map<String, String[]> paramMap = request.getParameterMap();
        String jsonBody = null;
        String enctype = request.getContentType();

        //1 文件表单的特殊处理
        if (StringUtils.isNotBlank(enctype) && enctype.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            // 新建的MultipartResolver 没有注入相关配置的属性 故删除
            MultipartHttpServletRequest req = resolver.resolveMultipart((HttpServletRequest) request);
            paramMap = req.getParameterMap();
            request = req;
        }
        //2. 如果是JSON格式的请求数据
        boolean isJsonRequest = StringUtils.isNotBlank(enctype) && enctype.contains(MediaType.APPLICATION_JSON_VALUE);
        if (isJsonRequest) {
            jsonBody = getRequestJsonString((HttpServletRequest) request);
        }
        //如果参数为null  json 请求体也是null 则不处理
        if (MapUtils.isEmpty(paramMap) && StringUtils.isBlank(jsonBody)) {
            chain.doFilter(request, response);
            return;
        }
        request = new ParameterRequestWrapper((HttpServletRequest) request, jsonBody, paramMap);
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        ServletContext sc = config.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sc);
        this.resolver = (MultipartResolver) webApplicationContext.getBean(MULTIPART_RESOLVER_BEAN_NAME);
        Assert.notNull(resolver, "multipartResolver  must not be null");
        logger.info(" init ParamDecryptFilter and get multipartResolver");

    }

    /* ************************获取参数的一些方法↓↓↓↓↓↓************************************* */


    /***
     * 获取 request 中 JSON 字符串的内容
     *
     */
    private static String getRequestJsonString(HttpServletRequest request) throws IOException {
        String requestMethod = request.getMethod();
        // GET
        if (HttpMethod.GET.name().equalsIgnoreCase(requestMethod)) {
            return new String(request.getQueryString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8).replaceAll("%22", "\"");
            // POST
        } else {
            return getRequestPostStr(request);
        }
    }

    /**
     * 描述:获取 post 请求的 byte[] 数组
     */
    private static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
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
