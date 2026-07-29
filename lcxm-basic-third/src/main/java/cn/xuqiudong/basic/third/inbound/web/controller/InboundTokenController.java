package cn.xuqiudong.basic.third.inbound.web.controller;

import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.third.inbound.model.TokenApplyRequest;
import cn.xuqiudong.basic.third.inbound.model.TokenCheckResult;
import cn.xuqiudong.basic.third.inbound.model.TokenIssueResult;
import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import cn.xuqiudong.basic.third.inbound.service.InboundTokenService;
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
        TokenIssueResult result = tokenService.issueToken(request);
        return result.isSuccess() ? BaseResponse.success(result.getToken()) : BaseResponse.error(result.getMessage());
    }

    /**
     * 注销 token。
     */
    @PostMapping("/revoke-token")
    public BaseResponse<TokenValue> revokeToken(@RequestParam("token") String token) {
        TokenCheckResult result = tokenService.revokeToken(token);
        return result.isSuccess()
                ? BaseResponse.success(result.getTokenValue())
                : BaseResponse.error(result.getMessage());
    }
}
