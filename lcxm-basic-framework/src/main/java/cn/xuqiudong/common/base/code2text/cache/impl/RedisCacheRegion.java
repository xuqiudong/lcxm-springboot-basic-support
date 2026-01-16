package cn.xuqiudong.common.base.code2text.cache.impl;

import cn.xuqiudong.common.base.code2text.cache.CacheRegion;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 描述:
 * redis级别的缓存 区
 *
 * @author Vic.xu
 * @since 2026-01-14 16:53
 */
public class RedisCacheRegion implements CacheRegion {

    private final String name;
    private final StringRedisTemplate redis;

    public RedisCacheRegion(String name,
                            StringRedisTemplate redis) {
        this.name = name;
        this.redis = redis;
    }

    private String redisKey() {
        return "c2t:" + name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String get(String key) {
        Object v = redis.opsForHash().get(redisKey(), key);
        return v == null ? null : String.valueOf(v);
    }

    @Override
    public String getOrLoad(String key, Supplier<String> loader) {

        String v = get(key);
        if (v != null) {
            return v;
        }

        v = loader.get();
        if (v != null) {
            //TODO 过期时间设置
            redis.opsForHash().put(redisKey(), key, v);
        }
        return v;
    }

    @Override
    public void invalidate(String key) {
        redis.opsForHash().delete(redisKey(), key);
    }

    @Override
    public void invalidateAll() {
        redis.delete(redisKey());
    }

    @Override
    public void put(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        redis.opsForHash().put(redisKey(), key, value);
    }

    @Override
    public void putAll(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        redis.opsForHash().putAll(redisKey(), map);
    }
}
