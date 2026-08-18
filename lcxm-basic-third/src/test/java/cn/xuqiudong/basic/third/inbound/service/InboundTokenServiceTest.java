package cn.xuqiudong.basic.third.inbound.service;

import cn.hutool.core.date.DateUtil;
import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.inbound.config.InboundAppConfig;
import cn.xuqiudong.basic.third.inbound.model.TokenApplyRequest;
import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import cn.xuqiudong.basic.third.inbound.registry.InboundAppConfigRegistry;
import cn.xuqiudong.basic.third.inbound.store.CaffeineNonceStore;
import cn.xuqiudong.basic.third.inbound.store.CaffeineTokenStore;
import cn.xuqiudong.basic.third.security.RsaSignatureUtils;
import cn.xuqiudong.basic.third.security.SignaturePayloadBuilder;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
                Collections.singletonList(new DemoRegistry(config)),
                new CaffeineTokenStore(),
                new CaffeineNonceStore());

        TokenApplyRequest request = buildSignedRequest(keyPair.getPrivateKey());
        String token = service.issueToken(request);

        assertNotNull(token);

        TokenValue checkResponse = service.checkToken(token);
        assertNotNull(checkResponse);

        TokenValue revokeResponse = service.revokeToken(token);
        assertNotNull(revokeResponse);

        try {
            service.checkToken(token);
        } catch (ThirdException e) {
            return;
        }
        throw new AssertionError("revoked token should be invalid");
    }

    @Test
    public void serviceShouldReloadConfigWhenCacheDisabled() {
        RsaSignatureUtils.RsaKeyPair keyPair = RsaSignatureUtils.createKeys();
        ReloadableRegistry registry = new ReloadableRegistry(keyPair.getPublicKey(), "firstUser");
        InboundTokenService service = new InboundTokenService(
                Collections.singletonList(registry),
                new CaffeineTokenStore(),
                new CaffeineNonceStore(),
                Duration.ZERO);

        TokenApplyRequest firstRequest = buildSignedRequest(keyPair.getPrivateKey(), "firstUser");
        assertNotNull(service.issueToken(firstRequest));

        registry.setUsername("secondUser");
        TokenApplyRequest secondRequest = buildSignedRequest(keyPair.getPrivateKey(), "secondUser");
        assertNotNull(service.issueToken(secondRequest));
        assertEquals(3, registry.getLoadCount());
    }

    @Test
    public void serviceShouldUseCustomUsernameCheckerBeforeConfiguredUsernames() {
        RsaSignatureUtils.RsaKeyPair keyPair = RsaSignatureUtils.createKeys();
        InboundAppConfig config = new InboundAppConfig();
        config.setAppId("demo-app");
        config.setPublicKey(keyPair.getPublicKey());
        config.addUsername("configuredUser");
        config.setUsernameChecker("dynamicUser"::equals);
        InboundTokenService service = new InboundTokenService(
                Collections.singletonList(new DemoRegistry(config)),
                new CaffeineTokenStore(),
                new CaffeineNonceStore());

        assertNotNull(service.issueToken(buildSignedRequest(keyPair.getPrivateKey(), "dynamicUser")));

        try {
            service.issueToken(buildSignedRequest(keyPair.getPrivateKey(), "configuredUser"));
            fail("custom username checker should take precedence over configured usernames");
        } catch (ThirdException e) {
            assertEquals("invalid username", e.getMessage());
        }
    }

    private TokenApplyRequest buildSignedRequest(String privateKey) {
        return buildSignedRequest(privateKey, "thirdUser");
    }

    private TokenApplyRequest buildSignedRequest(String privateKey, String username) {
        TokenApplyRequest request = new TokenApplyRequest();
        request.setAppId("demo-app");
        request.setNonce("nonce-1");
        request.setTimestamp(DateUtil.current());
        request.setUsername(username);
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

    private static class ReloadableRegistry implements InboundAppConfigRegistry {

        private final String publicKey;

        private final AtomicInteger loadCount = new AtomicInteger();

        private String username;

        private ReloadableRegistry(String publicKey, String username) {
            this.publicKey = publicKey;
            this.username = username;
        }

        @Override
        public String thirdCode() {
            return "demo";
        }

        @Override
        public InboundAppConfig inboundConfig() {
            loadCount.incrementAndGet();
            InboundAppConfig config = new InboundAppConfig();
            config.setAppId("demo-app");
            config.setThirdCode("demo");
            config.setPublicKey(publicKey);
            config.addUsername(username);
            return config;
        }

        private void setUsername(String username) {
            this.username = username;
        }

        private int getLoadCount() {
            return loadCount.get();
        }
    }
}
