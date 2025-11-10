package cn.xuqiudong.common.base.web.filter;

import cn.xuqiudong.common.base.web.filter.holder.BasicTokenHolder;
import cn.xuqiudong.common.base.web.filter.model.BasicToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Function;

/**
 * 描述:
 * 基于 HTTP Basic 认证 过滤器, 并把认证结果 BasicToken 放进当前请求上下文 BasicTokenHolder
 *
 * @author Vic.xu
 * @since 2024-09-02 17:38
 */
public class BasicAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthorizationFilter.class);

    /**
     * 请求参数中的token参数名 RFC6750 2.2. Form-Encoded Body Parameter 规范
     * https://rfc2cn.com/rfc6750.html
     */
    private static final String BASIC_PARAM_NAME = "access_token";

    private Function<BasicToken, Boolean> validateToken;

    public BasicAuthorizationFilter(Function<BasicToken, Boolean> validateToken) {
        this.validateToken = validateToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = extractAuthorization(request);
        BasicToken token = null;
        if (StringUtils.isNotBlank(authorization)) {
            try {
                token = BasicToken.decodeBasicToken(authorization);
                if (!validateToken.apply(token)) {
                    token = null;
                    LOGGER.error("token:{}不合法", authorization);
                }
            } catch (Exception e) {
                LOGGER.error("解析HTTP Basic:[｛｝]失败", authorization, e);
            }
        }
        if (token == null) {
            sendError(response);
            return;
        }
        BasicTokenHolder.setToken(token);
        try {
            filterChain.doFilter(request, response);
        } finally {
            BasicTokenHolder.clear();
        }
    }

    /**
     * 通知前端输入账号密码
     */
    private void sendError(HttpServletResponse response) throws IOException {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic");
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * 提取认证信息
     */
    private String extractAuthorization(HttpServletRequest request) {
        // 先从请求头中获取认证信息
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            return authorization;
        }
        // 再从请求参数中获取认证信息
        if (isParameterTokenSupported(request)) {
            return request.getParameter(BASIC_PARAM_NAME);
        }
        return null;

    }


    /**
     * 是否符合  RFC6750 2.2. Form-Encoded Body Parameter 规范
     */
    private boolean isParameterTokenSupported(final HttpServletRequest request) {
        String method = request.getMethod();
        return (RequestMethod.GET.name().equals(method) || RequestMethod.POST.name().equals(method))
                && MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType())
                ;
    }
}

