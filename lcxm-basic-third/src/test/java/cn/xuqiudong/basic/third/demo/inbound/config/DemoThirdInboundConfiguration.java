package cn.xuqiudong.basic.third.demo.inbound.config;

import cn.xuqiudong.basic.third.config.spring.AbstractThirdInboundConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 入站 demo 配置。
 *
 * <p>真实项目继承 {@link AbstractThirdInboundConfiguration} 后，加 {@link Configuration} 进入 Spring 容器。
 * demo 为了不依赖 Redis，切换为 Caffeine 本地存储。</p>
 */
@Configuration
public class DemoThirdInboundConfiguration extends AbstractThirdInboundConfiguration {

    /**
     * demo 使用本地 Caffeine 存储 token/nonce；真实项目默认建议使用 Redis。
     */
    @Override
    protected boolean useInboundRedisStore() {
        return false;
    }

    /**
     * 只有 useInboundRedisStore() 返回 true 时才会用到。
     */
    @Override
    protected RedisTemplate<String, Object> inboundRedisTemplate() {
        return null;
    }

    /**
     * demo 只拦截第三方业务接口。
     */
    @Override
    protected String[] inboundInterceptPathPatterns() {
        return new String[] {"/demo/third/inbound/**"};
    }
}
