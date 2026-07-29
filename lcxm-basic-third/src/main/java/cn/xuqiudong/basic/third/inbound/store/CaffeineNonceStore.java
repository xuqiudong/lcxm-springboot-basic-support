package cn.xuqiudong.basic.third.inbound.store;

import java.time.Duration;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Local nonce store based on Caffeine.
 *
 * @author Vic.xu
 */
public class CaffeineNonceStore implements NonceStore {

    private final Cache<String, Date> cache;

    public CaffeineNonceStore() {
        this(10_000);
    }

    public CaffeineNonceStore(long maximumSize) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .build();
    }

    @Override
    public boolean saveIfAbsent(String appId, String nonce, Duration ttl) {
        String key = buildKey(appId, nonce);
        Date expireAt = DateUtil.date(DateUtil.current() + ttl.toMillis());
        Date old = cache.asMap().putIfAbsent(key, expireAt);
        if (old == null) {
            return true;
        }
        if (DateUtil.date().before(old)) {
            return false;
        }
        return cache.asMap().replace(key, old, expireAt);
    }

    private String buildKey(String appId, String nonce) {
        return appId + ":" + nonce;
    }
}
