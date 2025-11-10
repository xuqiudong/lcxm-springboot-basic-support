package cn.xuqiudong.common.base.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * 系统级别的工具类
 *
 * @author VIC
 */
public final class Tools {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

    private static final String ROOT_PATH = "/";

    /**
     * 获得当前线程中的request
     *
     * @throws IllegalStateException 当前线程中不存在 Request 上下文
     */
    public static HttpServletRequest currentRequest() {
        ServletRequestAttributes attr = getServletRequestAttributes();
        return attr.getRequest();
    }

    /**
     * 当前线程中的currentResponse
     *
     * @return
     * @throws IllegalStateException 当前线程中不存在 Request 上下文
     */
    public static HttpServletResponse currentResponse() {
        ServletRequestAttributes attr = getServletRequestAttributes();
        return attr.getResponse();
    }

    private static ServletRequestAttributes getServletRequestAttributes() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            throw new IllegalStateException("当前线程中不存在 Request 上下文");
        }
        return attr;
    }


    /**
     * 获得当前线程中的session 不存在则返回null
     *
     * @return
     */
    public static HttpSession currentSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            return null;
        }
        return attr.getRequest().getSession(false);
    }

    static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通过response输出JSON
     *
     * @return
     */
    public static void writeJson(Object obj, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(objectMapper.writeValueAsString(obj));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Terminate here");
        }
    }

    /**
     * 获得当前请求的URL(不包含工程名)：
     * <p>
     * http://localhost:8080/project/user?key=123  →  /user
     * </p>
     */
    public static String getRequestUrl(HttpServletRequest request) {
        //获得工程名
        String contextPath = request.getContextPath();
        //获得包含工程名的当前页面全路径
        String uri = request.getRequestURI();
        //去掉相对地址中的参数
        int i = uri.indexOf("?");
        if (i < 0) {
            i = uri.length();
        }
        return uri.substring(contextPath.length(), i);
    }


    /**
     * 获取根地址：即工程名前缀 + 工程名
     * <p>
     * http://localhost:8080/project/user?key=123     →   http://localhost:8080/project
     * 其中test是工程名
     * </p>
     */
    public static String getRootUrl(HttpServletRequest request) {
        //地址栏的地址
        String url = request.getRequestURL().toString();
        //获得包含工程名的当前页面全路径
        String uri = request.getRequestURI();
        if (uri != null && !ROOT_PATH.equals(uri) && url.contains(uri)) {
            url = url.substring(0, url.indexOf(uri));
        }
        //去掉工程名以后的部分

        String contextPath = request.getContextPath();
        if (StringUtils.hasText(contextPath)) {
            return url + ROOT_PATH + contextPath;
        }
        return url;


    }

    /**
     * 获得当前请求的完整路径：包含协议和请求参数 : http://localhost:8080/test/user?key=123
     *
     * @param request
     * @return whole url
     */
    public static String getCurrentUrl(HttpServletRequest request) {
        return new StringBuilder().append(request.getRequestURL())
                .append(request.getQueryString() == null ? "" : "?" + request.getQueryString()).toString();
    }

    /**
     * 随机生成一个uuid
     */
    public static String randomUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 是否是ajax请求
     *
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }
}
