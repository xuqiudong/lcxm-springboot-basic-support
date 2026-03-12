package cn.xuqiudong.basic.framework.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

/**
 * 描述:
 *  记录请求时长的Filter
 *   spring已内置了类似的filter:CommonsRequestLoggingFilter
 * @see org.springframework.web.filter.CommonsRequestLoggingFilter;
 * @author Vic.xu
 * @since 2025-01-14 13:42
 */
public class RequestLoggerFilter extends AbstractRequestLoggingFilter {


    public static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggerFilter.class);

    public static final String[] DEFAULT_IGNORE_PATH = {"/static", "/js", "css", "images", "fonts", "favicon.ico", "assets"};

    private static final String START_WATCH = "_startWatch";

    /**
     * 请求时间超过多少毫秒算为慢请求: 默认10s
     */
    private static final long DEFAULT_SLOW_REQUEST_THRESHOLD = 10_000L;

    /**
     * 忽略的请求前缀
     */
    private String[] ignorePathPrefix;

    private long slowRequestThreshold;

    public RequestLoggerFilter() {
        super();
        this.slowRequestThreshold = DEFAULT_SLOW_REQUEST_THRESHOLD;
        this.ignorePathPrefix = DEFAULT_IGNORE_PATH;
    }

    public void setIgnorePathPrefix(String[] ignorePathPrefix) {
        this.ignorePathPrefix = ignorePathPrefix;
    }

    public void setSlowRequestThreshold(long slowRequestThreshold) {
        this.slowRequestThreshold = slowRequestThreshold;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        request.setAttribute(START_WATCH, System.nanoTime());
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        LOGGER.debug("{} userAgent = {}", message, userAgent);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        Object startTime = request.getAttribute(START_WATCH);
        if (startTime == null) {
            LOGGER.debug(message);
            return;
        }
        long nano = System.nanoTime() - (long) startTime;
        long millis = nano / 1000_000L;
        LOGGER.debug("{} took {} ms", message, millis);
        if (millis > slowRequestThreshold) {
            LOGGER.warn("{} cost {} ms, is more than {} ms, please caution about!", message, millis, slowRequestThreshold);
        }
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (StringUtils.isBlank(servletPath)) {
            return true;
        }

        if (ignorePathPrefix == null) {
            return super.shouldLog(request);
        }

        for (String ignorePath : ignorePathPrefix) {
            if (servletPath.startsWith(ignorePath)) {
                return false;
            }
        }
        return super.shouldLog(request);
    }
}
