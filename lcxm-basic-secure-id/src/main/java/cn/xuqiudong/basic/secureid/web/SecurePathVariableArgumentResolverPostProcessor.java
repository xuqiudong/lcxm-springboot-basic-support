package cn.xuqiudong.basic.secureid.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 把 Spring 默认 {@link PathVariableMethodArgumentResolver} 替换成 secure-id 路径参数解析器。
 * <p>
 * 不能只依赖 {@code WebMvcConfigurer#addArgumentResolvers}：
 * Spring MVC 会先注册一批内置 resolver，默认 {@code PathVariableMethodArgumentResolver}
 * 已经能处理普通 {@code @PathVariable}，如果 secure-id resolver 排在它后面，就没有执行机会。
 * <p>
 * secure-id resolver 继承自默认 resolver，只增加解密过程；替换默认 resolver 比额外插入一个 resolver
 * 更接近“覆盖默认路径参数解析行为”，也避免责任链中同时存在两个普通路径参数 resolver。
 *
 * @author Vic.xu
 * @since 2026-08-24
 */
public class SecurePathVariableArgumentResolverPostProcessor implements BeanPostProcessor {

    /**
     * 用于替换 Spring 默认 PathVariable resolver 的 secure-id resolver。
     */
    private final SecurePathVariableArgumentResolver securePathVariableArgumentResolver;

    /**
     * 路径参数解密总开关。
     * <p>
     * 开关放在注册阶段判断；关闭时不调整 Spring MVC 默认 resolver 列表。
     */
    private final Supplier<Boolean> enabledSupplier;

    public SecurePathVariableArgumentResolverPostProcessor(SecurePathVariableArgumentResolver securePathVariableArgumentResolver,
                                                           Supplier<Boolean> enabledSupplier) {
        this.securePathVariableArgumentResolver = securePathVariableArgumentResolver;
        this.enabledSupplier = enabledSupplier;
    }

    /**
     * RequestMappingHandlerAdapter 初始化完成后，其默认 argumentResolvers 已经构建完成。
     * 这时替换默认 PathVariable resolver，既能保留 Spring 默认能力，又能额外增加 secure-id 解密。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!enabledSupplier.get()) {
            return bean;
        }
        if (!(bean instanceof RequestMappingHandlerAdapter)) {
            return bean;
        }

        RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
        List<HandlerMethodArgumentResolver> resolvers = adapter.getArgumentResolvers();
        if (resolvers == null || containsSecurePathVariableResolver(resolvers)) {
            return bean;
        }

        List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<HandlerMethodArgumentResolver>(resolvers);
        int replaceIndex = findPathVariableResolverIndex(newResolvers);
        if (replaceIndex >= 0) {
            newResolvers.set(replaceIndex, securePathVariableArgumentResolver);
        } else {
            // 极端情况下找不到默认 resolver，则放在责任链最前面兜底。
            newResolvers.add(0, securePathVariableArgumentResolver);
        }
        adapter.setArgumentResolvers(newResolvers);
        return bean;
    }

    /**
     * 避免重复注册导致同一个参数被多次尝试。
     */
    private boolean containsSecurePathVariableResolver(List<HandlerMethodArgumentResolver> resolvers) {
        for (HandlerMethodArgumentResolver resolver : resolvers) {
            if (resolver instanceof SecurePathVariableArgumentResolver) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找 Spring 默认 PathVariable resolver 的位置。
     */
    private int findPathVariableResolverIndex(List<HandlerMethodArgumentResolver> resolvers) {
        for (int i = 0; i < resolvers.size(); i++) {
            if (PathVariableMethodArgumentResolver.class.equals(resolvers.get(i).getClass())) {
                return i;
            }
        }
        return -1;
    }
}
