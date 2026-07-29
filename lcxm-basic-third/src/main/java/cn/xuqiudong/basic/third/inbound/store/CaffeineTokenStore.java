package cn.xuqiudong.basic.third.inbound.store;

import java.time.Duration;

import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Local token store based on Caffeine.
 *
 * <p>Use this implementation for single-node deployment, demo, or tests. Clustered
 * deployments should use {@link RedisTokenStore} or a project-specific implementation.</p>
 *
 * @author Vic.xu
 */
public class CaffeineTokenStore implements TokenStore {

    private final Cache<String, TokenValue> cache;

    public CaffeineTokenStore() {
        this(10_000);
    }

    public CaffeineTokenStore(long maximumSize) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .build();
    }

    @Override
    public void put(String token, TokenValue value, Duration ttl) {
        if (token != null && value != null) {
            cache.put(token, value);
        }
    }

    @Override
    public TokenValue get(String token) {
        TokenValue value = cache.getIfPresent(token);
        if (value != null && value.isExpired()) {
            cache.invalidate(token);
            return null;
        }
        return value;
    }

    @Override
    public void remove(String token) {
        cache.invalidate(token);
    }
}
