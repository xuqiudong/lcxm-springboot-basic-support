package cn.xuqiudong.basic.core.code2text.cache.impl;

import cn.xuqiudong.basic.core.code2text.cache.CacheRegion;
import cn.xuqiudong.basic.core.code2text.cache.model.CacheRegionConfig;
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

    private final CacheRegionConfig config;
    private final StringRedisTemplate redis;

    public RedisCacheRegion(String name,
                            CacheRegionConfig config,
                            StringRedisTemplate redis) {
        this.name = name;
        this.config = config;
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
            saveAndExpire(key, v);

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
        saveAndExpire(key, value);
    }

    @Override
    public void putAll(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        redis.opsForHash().putAll(redisKey(), map);
        expire();
    }

    private void saveAndExpire(String key, String value) {
        redis.opsForHash().put(redisKey(), key, value);
        expire();

    }

    private void expire() {
        // 过期时间设置 FIXME  这里有个问题  设置的是整个map的过期时间
        redis.expire(redisKey(), config.getRedisTtl());
    }
}
