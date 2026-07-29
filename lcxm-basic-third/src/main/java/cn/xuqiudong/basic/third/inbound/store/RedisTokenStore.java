package cn.xuqiudong.basic.third.inbound.store;

import java.time.Duration;

import cn.xuqiudong.basic.third.inbound.model.TokenValue;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis token store based on Spring Data Redis.
 *
 * <p>The project passes its own {@link RedisTemplate}; this module does not create
 * Redis connections or read Redis configuration.</p>
 *
 * @author Vic.xu
 */
public class RedisTokenStore implements TokenStore {

    public static final String DEFAULT_KEY_PREFIX = "third:inbound:token:";

    private final RedisTemplate<String, Object> redisTemplate;

    private final String keyPrefix;

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required RedisTemplate.")
    public RedisTokenStore(RedisTemplate<String, Object> redisTemplate) {
        this(redisTemplate, DEFAULT_KEY_PREFIX);
    }

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for required RedisTemplate.")
    public RedisTokenStore(RedisTemplate<String, Object> redisTemplate, String keyPrefix) {
        if (redisTemplate == null) {
            throw new IllegalArgumentException("redisTemplate can not be null");
        }
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix == null || keyPrefix.isBlank() ? DEFAULT_KEY_PREFIX : keyPrefix;
    }

    @Override
    public void put(String token, TokenValue value, Duration ttl) {
        redisTemplate.opsForValue().set(buildKey(token), value, ttl);
    }

    @Override
    public TokenValue get(String token) {
        Object value = redisTemplate.opsForValue().get(buildKey(token));
        return value instanceof TokenValue tokenValue ? tokenValue : null;
    }

    @Override
    public void remove(String token) {
        redisTemplate.delete(buildKey(token));
    }

    private String buildKey(String token) {
        return keyPrefix + token;
    }
}
