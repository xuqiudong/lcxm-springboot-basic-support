package cn.xuqiudong.basic.third.inbound.service;

import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.xuqiudong.basic.third.inbound.config.InboundAppConfig;
import cn.xuqiudong.basic.third.inbound.model.TokenApplyRequest;
import cn.xuqiudong.basic.third.inbound.model.TokenCheckResult;
import cn.xuqiudong.basic.third.inbound.model.TokenIssueResult;
import cn.xuqiudong.basic.third.inbound.registry.InboundAppConfigRegistry;
import cn.xuqiudong.basic.third.inbound.store.CaffeineNonceStore;
import cn.xuqiudong.basic.third.inbound.store.CaffeineTokenStore;
import cn.xuqiudong.basic.third.security.RsaSignatureUtils;
import cn.xuqiudong.basic.third.security.SignaturePayloadBuilder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Minimal token issue/check/revoke flow test.
 */
public class InboundTokenServiceTest {

    @Test
    public void serviceShouldIssueCheckAndRevokeToken() {
        RsaSignatureUtils.RsaKeyPair keyPair = RsaSignatureUtils.createKeys();
        InboundAppConfig config = new InboundAppConfig();
        config.setAppId("demo-app");
        config.setThirdCode("demo");
        config.setPublicKey(keyPair.getPublicKey());
        config.addUsername("thirdUser");
        InboundTokenService service = new InboundTokenService(
                List.of(new DemoRegistry(config)),
                new CaffeineTokenStore(),
                new CaffeineNonceStore());

        TokenApplyRequest request = buildSignedRequest(keyPair.getPrivateKey());
        TokenIssueResult tokenResponse = service.issueToken(request);

        assertTrue(tokenResponse.isSuccess());
        assertNotNull(tokenResponse.getToken());

        TokenCheckResult checkResponse = service.checkToken(tokenResponse.getToken());
        assertTrue(checkResponse.isSuccess());

        TokenCheckResult revokeResponse = service.revokeToken(tokenResponse.getToken());
        assertTrue(revokeResponse.isSuccess());

        TokenCheckResult afterRevoke = service.checkToken(tokenResponse.getToken());
        assertFalse(afterRevoke.isSuccess());
    }

    private TokenApplyRequest buildSignedRequest(String privateKey) {
        TokenApplyRequest request = new TokenApplyRequest();
        request.setAppId("demo-app");
        request.setNonce("nonce-1");
        request.setTimestamp(DateUtil.current());
        request.setUsername("thirdUser");
        request.setSign(RsaSignatureUtils.privateSign(SignaturePayloadBuilder.buildTokenPayload(request), privateKey));
        return request;
    }

    private static class DemoRegistry implements InboundAppConfigRegistry {

        private final InboundAppConfig config;

        private DemoRegistry(InboundAppConfig config) {
            this.config = config;
        }

        @Override
        public String thirdCode() {
            return "demo";
        }

        @Override
        public InboundAppConfig inboundConfig() {
            return config;
        }
    }
}
