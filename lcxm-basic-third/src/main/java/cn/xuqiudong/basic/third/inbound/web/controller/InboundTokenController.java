package cn.xuqiudong.basic.third.inbound.web.controller;

import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.inbound.model.TokenApplyRequest;
import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import cn.xuqiudong.basic.third.inbound.service.InboundTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring MVC token controller.
 *
 * <p>只提供第三方接入需要的默认 token 入口。业务接口的 token 校验交给
 * {@link cn.xuqiudong.basic.third.inbound.web.interceptor.InboundTokenInterceptor}。</p>
 *
 * @author Vic.xu
 */
@RestController
@RequestMapping("/inbound/api")
public class InboundTokenController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundTokenController.class);

    private final InboundTokenService tokenService;

    /**
     * 创建默认 token controller。
     */
    public InboundTokenController(InboundTokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 校验 appId/timestamp/nonce/username/sign 后签发 token。
     */
    @PostMapping("/obtain-token")
    public BaseResponse<String> obtainToken(@RequestBody TokenApplyRequest request) {
        try {
            return BaseResponse.success(tokenService.issueToken(request));
        } catch (ThirdException e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 注销 token。
     */
    @PostMapping("/revoke-token")
    public BaseResponse<TokenValue> revokeToken(@RequestParam("token") String token) {
        try {
            return BaseResponse.success(tokenService.revokeToken(token));
        } catch (ThirdException e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * token 接口对第三方开放，默认兜底返回业务错误，避免把异常转换成 HTTP 500。
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleException(Exception e) {
        LOGGER.warn("third inbound token api failed", e);
        return BaseResponse.error("third inbound token api failed");
    }
}
