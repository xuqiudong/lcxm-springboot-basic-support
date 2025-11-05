package cn.xuqiudong.common.base.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 描述:
 * 判断请求的来源是否安全;
 * 建议直接配置拦截指定的一些url，以免误伤  registrationBean.addUrlPatterns("url1", "url2")
 * <p>
 * 1. 必须包含Referer   <br/>
 * 2. 相同的host 则放行  <br/>
 * 3. 在白名单内在放行     <br/>
 * 3.1   是否白名单：如果既没有设置 白名单过滤逻辑 也没有设置白名单列表 则直接拦截  <br/>
 * 3.2 白名单过滤逻辑 和 白名单列表  满足其一即可    <br/>
 * </p>
 *
 * @author Vic.xu
 * @since 2025-06-23 11:01
 */
public class SafetyRefererFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafetyRefererFilter.class);

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

        String referer = getReferer(request);
        if (StringUtils.isBlank(referer)) {
            refuseRequest(response, null);
            return;
        }
        try {
            // 相同的 host 请求 允许
            if (isSameHost(request, referer)) {
                LOGGER.info("Referer URL [{}] is from the same host", referer);
                filterChain.doFilter(request, response);
                return;
            }
            // Referer 不在白名单内则  拒绝
            if (!isWhite(referer)) {
                LOGGER.warn("Referer URL [{}] is not in the white list", referer);
                refuseRequest(response, null);
                return;
            }
            filterChain.doFilter(request, response);

        } catch (URISyntaxException e) {
            LOGGER.error("Invalid Referer URL: {}", referer, e);
            refuseRequest(response, "Invalid Referer URL: " + referer);
        }
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


    private void refuseRequest(HttpServletResponse response, String msg) throws IOException {
        if (msg != null) {
            msg = "Invalid Referer URL";
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, msg);
    }

    /**
     * 判断是否是同域名
     */
    private boolean isSameHost(HttpServletRequest request, String referer) throws URISyntaxException {
        String safeReferer = referer;
        if (!safeReferer.startsWith("http")) {
            safeReferer = "https://" + safeReferer;
        }

        URI currentUri = new URI(request.getRequestURL().toString());
        URI refererUri = new URI(safeReferer);
        return currentUri.getHost().equalsIgnoreCase(refererUri.getHost());
    }

    private String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }
}
