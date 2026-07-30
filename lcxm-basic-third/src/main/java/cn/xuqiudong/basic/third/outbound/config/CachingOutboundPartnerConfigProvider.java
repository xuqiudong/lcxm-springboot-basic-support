package cn.xuqiudong.basic.third.outbound.config;

import java.time.Duration;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * 基于 Caffeine 的出站配置缓存包装器。
 *
 * <p>项目需要缓存配置时包一层即可；基础 Client 不强制缓存，也不直接依赖 Redis。</p>
 *
 * @author Vic.xu
 */
public class CachingOutboundPartnerConfigProvider<C extends OutboundPartnerConfig>
        implements OutboundPartnerConfigProvider<C> {

    private static final Duration DEFAULT_EXPIRE_AFTER_WRITE = Duration.ofMinutes(5);

    private final OutboundPartnerConfigProvider<C> delegate;

    private final Cache<ThirdIdentity, C> cache;

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for invalid delegate provider.")
    public CachingOutboundPartnerConfigProvider(OutboundPartnerConfigProvider<C> delegate) {
        this(delegate, DEFAULT_EXPIRE_AFTER_WRITE);
    }

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for invalid delegate provider.")
    public CachingOutboundPartnerConfigProvider(OutboundPartnerConfigProvider<C> delegate,
            Duration expireAfterWrite) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate can not be null");
        }
        this.delegate = delegate;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWrite == null ? DEFAULT_EXPIRE_AFTER_WRITE : expireAfterWrite)
                .build();
    }

    @Override
    public C getConfig(ThirdIdentity thirdIdentity) {
        return cache.get(thirdIdentity, delegate::getConfig);
    }

    /**
     * 清理指定第三方配置缓存。
     */
    public void invalidate(ThirdIdentity thirdIdentity) {
        cache.invalidate(thirdIdentity);
    }

    /**
     * 清理全部第三方配置缓存。
     */
    public void invalidateAll() {
        cache.invalidateAll();
    }
}
