package cn.xuqiudong.basic.third.inbound.web.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.inbound.constant.InboundTokenConstants;
import cn.xuqiudong.basic.third.inbound.context.InboundTokenContextHolder;
import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import cn.xuqiudong.basic.third.inbound.service.InboundTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * Spring MVC inbound token 拦截器。
 *
 * <p>业务项目通过 {@code InterceptorRegistry} 注册，并自行决定需要保护的业务 URL。</p>
 *
 * @author Vic.xu
 */
public class InboundTokenInterceptor implements AsyncHandlerInterceptor {

    private InboundTokenService tokenService;

    private String tokenHeaderName = InboundTokenConstants.TOKEN_HEADER_NAME;

    private String tokenParameterName = InboundTokenConstants.TOKEN_PARAMETER_NAME;

    /**
     * 设置 token 服务。
     */
    public void setTokenService(InboundTokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 设置 token header 名称；为空时使用默认值 {@code X-Third-Token}。
     */
    public void setTokenHeaderName(String tokenHeaderName) {
        this.tokenHeaderName = StrUtil.blankToDefault(tokenHeaderName, InboundTokenConstants.TOKEN_HEADER_NAME);
    }

    /**
     * 设置 token parameter 名称；为空时使用默认值 {@code token}。
     */
    public void setTokenParameterName(String tokenParameterName) {
        this.tokenParameterName = StrUtil.blankToDefault(tokenParameterName, InboundTokenConstants.TOKEN_PARAMETER_NAME);
    }

    /**
     * 校验请求中的 token；失败时直接写出 JSON 响应并中断请求。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        InboundTokenContextHolder.clear();
        try {
            if (tokenService == null) {
                throw new ThirdException("tokenService can not be null");
            }
            TokenValue tokenValue = tokenService.checkToken(resolveToken(request));
            InboundTokenContextHolder.bind(tokenValue);
            return true;
        } catch (ThirdException e) {
            writeJson(response, BaseResponse.error(e.getMessage()));
            return false;
        }
    }

    /**
     * 请求完成后清理当前线程的 token 身份。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        InboundTokenContextHolder.clear();
    }

    /**
     * 异步请求释放原 Servlet 线程时立即清理，避免线程池复用导致身份串扰。
     */
    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        InboundTokenContextHolder.clear();
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
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }
}
