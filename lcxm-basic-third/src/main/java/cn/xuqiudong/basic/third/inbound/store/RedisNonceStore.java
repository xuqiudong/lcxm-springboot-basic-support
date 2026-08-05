package cn.xuqiudong.basic.third.inbound.store;

import cn.hutool.core.util.StrUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Redis nonce store based on Spring Data Redis.
 *
 * @author Vic.xu
 */
public class RedisNonceStore implements NonceStore {

    public static final String DEFAULT_KEY_PREFIX = "third:inbound:nonce:";

    protected final RedisTemplate<String, Object> redisTemplate;

    protected final String keyPrefix;

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required RedisTemplate.")
    public RedisNonceStore(RedisTemplate<String, Object> redisTemplate) {
        this(redisTemplate, DEFAULT_KEY_PREFIX);
    }

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required RedisTemplate.")
    public RedisNonceStore(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
        if (redisTemplate == null) {
            throw new IllegalArgumentException("redisTemplate can not be null");
        }
        this.redisTemplate = redisTemplate;
        this.keyPrefix = StrUtil.blankToDefault(keyPrefix, DEFAULT_KEY_PREFIX);
    }

    @Override
    public boolean saveIfAbsent(String appId, String nonce, long ttlSeconds) {
        String key = buildKey(appId, nonce);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1");
        if (Boolean.TRUE.equals(success)) {
            // Compatible with older Spring Data Redis. This is not strictly atomic like SET NX EX.
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    protected String buildKey(String appId, String nonce) {
        return keyPrefix + appId + ":" + nonce;
    }
}
