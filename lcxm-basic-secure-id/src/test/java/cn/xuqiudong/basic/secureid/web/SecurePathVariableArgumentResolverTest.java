package cn.xuqiudong.basic.secureid.web;

import cn.xuqiudong.basic.secureid.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurePathVariableArgumentResolverTest {

    private final SecurePathVariableArgumentResolver resolver =
            new SecurePathVariableArgumentResolver();

    @Test
    public void decryptsStringPathVariable() throws Exception {
        MethodParameter parameter = methodParameter("stringId", 0);
        NativeWebRequest request = webRequest(Collections.singletonMap("userId", IdUtil.encrypt("100")));

        Object value = resolver.resolveArgument(parameter, new ModelAndViewContainer(), request, new DefaultDataBinderFactory(null));

        System.out.printf("path-variable decrypt: encrypted=%s, decrypted=%s%n",
                IdUtil.encrypt("100"), value);
        assertEquals("100", value);
    }

    @Test
    public void decryptsAndConvertsLongPathVariable() throws Exception {
        MethodParameter parameter = methodParameter("longId", 0);
        NativeWebRequest request = webRequest(Collections.singletonMap("userId", IdUtil.encrypt("200")));

        Object value = resolver.resolveArgument(parameter, new ModelAndViewContainer(), request, new DefaultDataBinderFactory(null));

        assertEquals(200L, value);
    }

    @Test
    public void keepsPlainPathVariableValue() throws Exception {
        MethodParameter parameter = methodParameter("stringId", 0);
        NativeWebRequest request = webRequest(Collections.singletonMap("userId", "plain-code"));

        Object value = resolver.resolveArgument(parameter, new ModelAndViewContainer(), request, new DefaultDataBinderFactory(null));

        assertEquals("plain-code", value);
    }

    @Test
    public void supportsNormalPathVariableButSkipsMapPathVariable() throws Exception {
        assertTrue(resolver.supportsParameter(methodParameter("stringId", 0)));
        assertFalse(resolver.supportsParameter(methodParameter("map", 0)));
    }

    @Test
    public void postProcessorReplacesDefaultPathVariableResolver() {
        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
        List<org.springframework.web.method.support.HandlerMethodArgumentResolver> resolvers =
                new ArrayList<org.springframework.web.method.support.HandlerMethodArgumentResolver>();
        resolvers.add(new PathVariableMethodArgumentResolver());
        adapter.setArgumentResolvers(resolvers);

        SecurePathVariableArgumentResolverPostProcessor postProcessor =
                new SecurePathVariableArgumentResolverPostProcessor(resolver, () -> true);
        postProcessor.postProcessAfterInitialization(adapter, "requestMappingHandlerAdapter");

        int secureResolverIndex = findResolverIndex(adapter, SecurePathVariableArgumentResolver.class);
        int defaultPathVariableResolverIndex = findExactResolverIndex(adapter, PathVariableMethodArgumentResolver.class);

        assertNotEquals(-1, secureResolverIndex);
        assertEquals(-1, defaultPathVariableResolverIndex);
    }

    @Test
    public void postProcessorDoesNotInsertResolverWhenDisabled() {
        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
        List<org.springframework.web.method.support.HandlerMethodArgumentResolver> resolvers =
                new ArrayList<org.springframework.web.method.support.HandlerMethodArgumentResolver>();
        resolvers.add(new PathVariableMethodArgumentResolver());
        adapter.setArgumentResolvers(resolvers);

        SecurePathVariableArgumentResolverPostProcessor postProcessor =
                new SecurePathVariableArgumentResolverPostProcessor(resolver, () -> false);
        postProcessor.postProcessAfterInitialization(adapter, "requestMappingHandlerAdapter");

        assertEquals(-1, findResolverIndex(adapter, SecurePathVariableArgumentResolver.class));
    }

    private static MethodParameter methodParameter(String methodName, int parameterIndex) throws Exception {
        Method method = TestController.class.getDeclaredMethod(methodName, parameterTypes(methodName));
        return new MethodParameter(method, parameterIndex);
    }

    private static Class<?>[] parameterTypes(String methodName) {
        if ("longId".equals(methodName)) {
            return new Class<?>[]{Long.class};
        }
        if ("map".equals(methodName)) {
            return new Class<?>[]{Map.class};
        }
        return new Class<?>[]{String.class};
    }

    private static NativeWebRequest webRequest(Map<String, String> uriVariables) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/" + uriVariables.get("userId"));
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, new HashMap<String, String>(uriVariables));
        return new ServletWebRequest(request);
    }

    private static int findResolverIndex(RequestMappingHandlerAdapter adapter, Class<?> resolverType) {
        for (int i = 0; i < adapter.getArgumentResolvers().size(); i++) {
            if (resolverType.isInstance(adapter.getArgumentResolvers().get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int findExactResolverIndex(RequestMappingHandlerAdapter adapter, Class<?> resolverType) {
        for (int i = 0; i < adapter.getArgumentResolvers().size(); i++) {
            if (resolverType.equals(adapter.getArgumentResolvers().get(i).getClass())) {
                return i;
            }
        }
        return -1;
    }

    private static class TestController {

        void stringId(@PathVariable(name = "userId") String userId) {
        }

        void longId(@PathVariable(name = "userId") Long userId) {
        }

        void map(@PathVariable Map<String, String> variables) {
        }
    }
}
