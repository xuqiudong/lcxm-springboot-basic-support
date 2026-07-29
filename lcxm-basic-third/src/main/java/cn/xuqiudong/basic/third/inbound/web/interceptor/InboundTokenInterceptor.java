package cn.xuqiudong.basic.third.inbound.web.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.third.inbound.constant.InboundTokenConstants;
import cn.xuqiudong.basic.third.inbound.model.TokenCheckResult;
import cn.xuqiudong.basic.third.inbound.service.InboundTokenService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring MVC inbound token 拦截器。
 *
 * <p>业务项目通过 {@code InterceptorRegistry} 注册，并自行决定需要保护的业务 URL。</p>
 *
 * @author Vic.xu
 */
public class InboundTokenInterceptor implements HandlerInterceptor {

    private final InboundTokenService tokenService;

    private final String tokenHeaderName;

    private final String tokenParameterName;

    /**
     * 创建默认 token 拦截器。
     *
     * <p>默认从 header {@code X-Third-Token} 读取 token，并兼容参数 {@code token}。</p>
     */
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required interceptor service.")
    public InboundTokenInterceptor(InboundTokenService tokenService) {
        this(tokenService, InboundTokenConstants.TOKEN_HEADER_NAME, InboundTokenConstants.TOKEN_PARAMETER_NAME);
    }

    /**
     * 自定义 token 名称，同时用于 header 和 parameter。
     */
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required interceptor service.")
    public InboundTokenInterceptor(InboundTokenService tokenService, String tokenName) {
        this(tokenService, tokenName, tokenName);
    }

    /**
     * 自定义 token header 和 parameter 名称。
     */
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required interceptor service.")
    public InboundTokenInterceptor(InboundTokenService tokenService, String tokenHeaderName,
            String tokenParameterName) {
        if (tokenService == null) {
            throw new IllegalArgumentException("tokenService can not be null");
        }
        this.tokenService = tokenService;
        this.tokenHeaderName = StrUtil.blankToDefault(tokenHeaderName, InboundTokenConstants.TOKEN_HEADER_NAME);
        this.tokenParameterName = StrUtil.blankToDefault(tokenParameterName, InboundTokenConstants.TOKEN_PARAMETER_NAME);
    }

    /**
     * 校验请求中的 token；失败时直接写出 JSON 响应并中断请求。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        TokenCheckResult result = tokenService.checkToken(resolveToken(request));
        if (!result.isSuccess()) {
            writeJson(response, BaseResponse.error(result.getMessage()));
            return false;
        }
        return true;
    }

    /**
     * 从 header 或 parameter 中解析 token。
     */
    private String resolveToken(HttpServletRequest request) {
        // header 使用独立名称，避免与业务参数、网关鉴权或其他 token 头混淆。
        String token = request.getHeader(tokenHeaderName);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(tokenParameterName);
        }
        return token;
    }

    /**
     * 写出 token 校验失败响应。
     */
    private void writeJson(HttpServletResponse response, BaseResponse<?> result) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
