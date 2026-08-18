package cn.xuqiudong.basic.third.inbound.web.interceptor;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Date;

import cn.xuqiudong.basic.third.inbound.config.InboundAppConfig;
import cn.xuqiudong.basic.third.inbound.context.InboundTokenContextHolder;
import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import cn.xuqiudong.basic.third.inbound.registry.InboundAppConfigRegistry;
import cn.xuqiudong.basic.third.inbound.service.InboundTokenService;
import cn.xuqiudong.basic.third.inbound.store.CaffeineTokenStore;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class InboundTokenInterceptorTest {

    @After
    public void clearContext() {
        InboundTokenContextHolder.clear();
    }

    @Test
    public void interceptorShouldBindAndClearCheckedTokenIdentity() throws Exception {
        CaffeineTokenStore tokenStore = new CaffeineTokenStore();
        TokenValue storedValue = new TokenValue("demo-app", "demo", "thirdUser",
                new Date(System.currentTimeMillis() + 60_000));
        tokenStore.put("valid-token", storedValue, 60);
        InboundTokenService tokenService = new InboundTokenService(
                Collections.singletonList(registry()), tokenStore, null);
        InboundTokenInterceptor interceptor = new InboundTokenInterceptor();
        interceptor.setTokenService(tokenService);

        InboundTokenContextHolder.bind(new TokenValue("old-app", "old", "oldUser", null));
        HttpServletRequest request = requestWithToken("valid-token");
        assertTrue(interceptor.preHandle(request, null, new Object()));
        assertEquals("thirdUser", InboundTokenContextHolder.currentUsername());
        assertEquals("demo-app", InboundTokenContextHolder.current().getAppId());
        assertNotSame(storedValue, InboundTokenContextHolder.current());

        interceptor.afterCompletion(request, null, new Object(), null);
        assertNull(InboundTokenContextHolder.current());
    }

    @Test
    public void interceptorShouldClearContextWhenAsyncHandlingStarts() {
        InboundTokenContextHolder.bind(new TokenValue("demo-app", "demo", "thirdUser", null));

        new InboundTokenInterceptor().afterConcurrentHandlingStarted(null, null, new Object());

        assertNull(InboundTokenContextHolder.current());
    }

    private InboundAppConfigRegistry registry() {
        return new InboundAppConfigRegistry() {
            @Override
            public String thirdCode() {
                return "demo";
            }

            @Override
            public InboundAppConfig inboundConfig() {
                InboundAppConfig config = new InboundAppConfig();
                config.setAppId("demo-app");
                return config;
            }
        };
    }

    private HttpServletRequest requestWithToken(String token) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    if ("getHeader".equals(method.getName()) && "X-Third-Token".equals(args[0])) {
                        return token;
                    }
                    return null;
                });
    }
}
