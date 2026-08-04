package cn.xuqiudong.basic.third.config.spring;

import java.util.Collection;

import cn.xuqiudong.basic.third.inbound.constant.InboundTokenConstants;
import cn.xuqiudong.basic.third.inbound.log.aspect.InboundInvokeLogAspect;
import cn.xuqiudong.basic.third.inbound.log.service.InboundInvokeLogger;
import cn.xuqiudong.basic.third.inbound.log.service.Slf4jInboundInvokeLogger;
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
 * <p>本类不加 {@code @Configuration}，具体项目的配置类继承后自行添加。
 * 默认提供 token 存储、token 服务、token controller、token 拦截器和入站调用日志切面。</p>
 *
 * @author Vic.xu
 */
public abstract class AbstractThirdInboundConfiguration {

    /**
     * token 存储 bean。
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
        InboundTokenInterceptor interceptor = new InboundTokenInterceptor();
        interceptor.setTokenService(tokenService);
        interceptor.setTokenHeaderName(inboundTokenHeaderName());
        interceptor.setTokenParameterName(InboundTokenConstants.TOKEN_PARAMETER_NAME);
        return interceptor;
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
     * 创建入站业务方法调用日志切面。
     *
     * <p>slf4j 打印和项目自定义落库互不替代，可以同时生效。</p>
     */
    @Bean
    public InboundInvokeLogAspect inboundInvokeLogAspect() {
        return new InboundInvokeLogAspect(printInboundInvokeLog(), inboundInvokeSlf4jTextMaxLength(),
                customInboundInvokeLogger(), inboundInvokeExtraIgnoreTypes());
    }

    /**
     * 是否使用 Redis 存储 token 和 nonce。
     */
    protected boolean useInboundRedisStore() {
        return true;
    }

    /**
     * 项目侧提供 RedisTemplate。
     */
    protected abstract RedisTemplate<String, Object> inboundRedisTemplate();

    /**
     * token header 名称。
     */
    protected String inboundTokenHeaderName() {
        return InboundTokenConstants.TOKEN_HEADER_NAME;
    }

    /**
     * 是否打印 slf4j 入站业务方法调用日志。
     */
    protected boolean printInboundInvokeLog() {
        return true;
    }

    /**
     * slf4j 打印入参、出参、异常堆栈时的最大长度；小于等于 0 表示不裁剪。
     */
    protected int inboundInvokeSlf4jTextMaxLength() {
        return Slf4jInboundInvokeLogger.DEFAULT_TEXT_MAX_LENGTH;
    }

    /**
     * 项目自定义入站业务方法调用日志处理器。
     */
    protected InboundInvokeLogger customInboundInvokeLogger() {
        return null;
    }

    /**
     * 入站业务方法日志需要额外忽略的参数类型。
     */
    protected Class<?>[] inboundInvokeExtraIgnoreTypes() {
        return new Class<?>[0];
    }

    /**
     * 是否由当前配置类自动注册 token 拦截器。
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
