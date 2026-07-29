package cn.xuqiudong.basic.third.config.spring;

import java.util.Collection;

import cn.xuqiudong.basic.third.inbound.constant.InboundTokenConstants;
import cn.xuqiudong.basic.third.inbound.registry.InboundAppConfigRegistry;
import cn.xuqiudong.basic.third.inbound.service.InboundTokenService;
import cn.xuqiudong.basic.third.inbound.store.CaffeineNonceStore;
import cn.xuqiudong.basic.third.inbound.store.CaffeineTokenStore;
import cn.xuqiudong.basic.third.inbound.store.NonceStore;
import cn.xuqiudong.basic.third.inbound.store.RedisNonceStore;
import cn.xuqiudong.basic.third.inbound.store.RedisTokenStore;
import cn.xuqiudong.basic.third.inbound.store.TokenStore;
import cn.xuqiudong.basic.third.inbound.web.controller.InboundTokenController;
import cn.xuqiudong.basic.third.inbound.web.interceptor.InboundTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 第三方入站 Spring 配置基类。
 *
 * <p>本类不加 {@code @Configuration}，具体项目的配置类继承它并自行添加
 * {@code @Configuration}。默认提供 token 服务、token controller、token 拦截器和拦截路径。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractThirdInboundConfiguration {

    /**
     * token 存储 bean。
     *
     * <p>默认使用 Redis；如需切换本地存储，覆盖 {@link #useInboundRedisStore()}。</p>
     */
    @Bean
    public TokenStore inboundTokenStore() {
        if (useInboundRedisStore()) {
            return new RedisTokenStore(inboundRedisTemplate());
        }
        return new CaffeineTokenStore();
    }

    /**
     * nonce 防重存储 bean。
     */
    @Bean
    public NonceStore inboundNonceStore() {
        if (useInboundRedisStore()) {
            return new RedisNonceStore(inboundRedisTemplate());
        }
        return new CaffeineNonceStore();
    }

    /**
     * 创建入站 token 服务。
     *
     * @param registries Spring 容器中所有第三方入站配置 registry
     * @param tokenStore token 存储
     */
    @Bean
    public InboundTokenService inboundTokenService(Collection<InboundAppConfigRegistry> registries,
            TokenStore tokenStore, NonceStore nonceStore) {
        return new InboundTokenService(registries, tokenStore, nonceStore);
    }

    /**
     * 创建默认 token controller。
     */
    @Bean
    public InboundTokenController inboundTokenController(InboundTokenService tokenService) {
        return new InboundTokenController(tokenService);
    }

    /**
     * 创建 token 拦截器。
     */
    @Bean
    public InboundTokenInterceptor inboundTokenInterceptor(InboundTokenService tokenService) {
        return new InboundTokenInterceptor(tokenService, inboundTokenHeaderName(),
                InboundTokenConstants.TOKEN_PARAMETER_NAME);
    }

    /**
     * 注册 token 拦截器到 Spring MVC。
     */
    @Bean
    public WebMvcConfigurer inboundTokenWebMvcConfigurer(InboundTokenInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                if (!registerInboundTokenInterceptor()) {
                    return;
                }
                registry.addInterceptor(interceptor)
                        .addPathPatterns(inboundInterceptPathPatterns())
                        .excludePathPatterns(inboundExcludePathPatterns());
            }
        };
    }

    /**
     * 是否使用 Redis 存储 token 和 nonce。
     *
     * <p>默认使用 Redis；返回 {@code false} 时使用 Caffeine 本地存储。</p>
     */
    protected boolean useInboundRedisStore() {
        return true;
    }

    /**
     * 项目侧提供 RedisTemplate。
     *
     * <p>token 存储和 nonce 防重存储共用该 RedisTemplate。</p>
     */
    protected abstract RedisTemplate<String, Object> inboundRedisTemplate();

    /**
     * token header 名称，默认避免使用通用的 {@code token} header。
     */
    protected String inboundTokenHeaderName() {
        return InboundTokenConstants.TOKEN_HEADER_NAME;
    }

    /**
     * 是否由当前配置类自动注册 token 拦截器。
     *
     * <p>Spring MVC XML 项目如需在 XML 中手动注册拦截器，覆盖该方法返回 {@code false}。</p>
     */
    protected boolean registerInboundTokenInterceptor() {
        return true;
    }

    /**
     * 需要 token 校验的入站业务路径。
     */
    protected String[] inboundInterceptPathPatterns() {
        return new String[] {InboundTokenConstants.API_PATH_PATTERN};
    }

    /**
     * 不需要 token 校验的入站路径。
     */
    protected String[] inboundExcludePathPatterns() {
        return new String[] {InboundTokenConstants.OBTAIN_TOKEN_PATH, InboundTokenConstants.REVOKE_TOKEN_PATH};
    }
}
