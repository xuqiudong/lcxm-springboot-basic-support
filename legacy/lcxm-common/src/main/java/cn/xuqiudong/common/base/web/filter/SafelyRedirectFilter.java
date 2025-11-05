package cn.xuqiudong.common.base.web.filter;

import cn.xuqiudong.common.base.web.filter.wrapper.RedirectResponseWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 描述:
 *  重定向之前对重定向地址进行白名单过滤， 注意把此Filter的顺序设置在前面
 * <p>
 * 1. 内部系统 应直接放行
 * 2. 外部地址 进行白名单过滤
 * 2.1   是否白名单：如果既没有设置 白名单过滤逻辑 也没有设置白名单列表 则直接拦截
 * 2.2 白名单过滤逻辑 和 白名单列表  满足其一即可
 * </p>
 *
 * @author Vic.xu
 * @since 2025-02-08 9:30
 */
public class SafelyRedirectFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafelyRedirectFilter.class);

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    /**
     * 拦截后的错误处理
     */
    protected BiConsumer<HttpServletResponse, String> sendErrorConsumer;

    /**
     * 白名单过滤逻辑
     */
    protected Function<String, Boolean> whiteListFilterFunction;

    /**
     * 白名单列表
     */
    protected List<String> whiteList;

    public void setSendErrorConsumer(BiConsumer<HttpServletResponse, String> sendErrorConsumer) {
        this.sendErrorConsumer = sendErrorConsumer;
    }

    public void setWhiteListFilterFunction(Function<String, Boolean> whiteListFilterFunction) {
        this.whiteListFilterFunction = whiteListFilterFunction;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 使用自定义的HttpServletResponseWrapper来拦截sendRedirect调用
        RedirectResponseWrapper responseWrapper = new RedirectResponseWrapper(response);
        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            // 1. 检查是否有重定向URL
            String redirectUrl = responseWrapper.getRedirectUrl();
            if (StringUtils.isBlank(redirectUrl)) {
                return;
            }
            if (isInternalRedirect(redirectUrl)) {
                // 2. 内部路径，直接放行
                response.sendRedirect(redirectUrl);
                return;
            }
            // 3. 进行白名单过滤
            boolean white = isWhite(redirectUrl);
            LOGGER.info("外部重定向地址[{}]白名单过滤结果", redirectUrl, white);
            if (white) {
                response.sendRedirect(redirectUrl);
                return;
            }
            // 4. 非白名单，拦截 并提示前端
            sendError(response, redirectUrl);
        }
    }

    /**
     * 拦截后的错误处理
     */
    protected void sendError(HttpServletResponse response, String redirectUrl) throws IOException {
        if (sendErrorConsumer != null) {
            sendErrorConsumer.accept(response, redirectUrl);
            return;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Redirect URL [" + redirectUrl + "] not allowed");
    }

    /**
     * 是否白名单
     */
    protected boolean isWhite(String url) {
        //如果即没有设置白名单列表 也没有设置白名单过滤函数，则直接拦截
        if (whiteList == null && whiteListFilterFunction == null) {
            return false;
        }
        boolean allow = false;
        if (whiteList != null) {
            allow = whiteList.stream().anyMatch(url::startsWith);
        }
        if (whiteListFilterFunction != null) {
            allow = allow || whiteListFilterFunction.apply(url);
        }
        return allow;
    }

    /**
     *  是否内部地址
     */
    private boolean isInternalRedirect(String url) {
        // 判断是否是内部 URL（不带 http/https，或是相对路径）
        url = url.trim().toLowerCase(Locale.ROOT);
        return !url.startsWith(HTTP) && !url.startsWith(HTTPS);
    }
}
