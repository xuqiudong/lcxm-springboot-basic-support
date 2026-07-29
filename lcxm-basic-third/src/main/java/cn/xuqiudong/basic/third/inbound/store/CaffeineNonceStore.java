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
        Date current = cache.getIfPresent(key);
        if (current != null && DateUtil.date().before(current)) {
            return false;
        }
        cache.put(key, DateUtil.date(DateUtil.current() + ttl.toMillis()));
        return true;
    }

    private String buildKey(String appId, String nonce) {
        return appId + ":" + nonce;
    }
}
