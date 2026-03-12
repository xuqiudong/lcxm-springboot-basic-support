package cn.xuqiudong.basic.core.code2text.cache.impl;

import cn.xuqiudong.basic.core.code2text.cache.CacheRegion;
import cn.xuqiudong.basic.core.code2text.cache.model.CacheRegionConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 描述:
 *  基于 Caffeine 实现 Region
 * @author Vic.xu
 * @since 2026-01-14 16:49
 */
public class CaffeineCacheRegion implements CacheRegion {

    private final String name;
    private final Cache<String, String> cache;

    public CaffeineCacheRegion(String name, CacheRegionConfig config) {
        this.name = name;
        this.cache = Caffeine.newBuilder()
                .maximumSize(config.getMaxSize())
                .expireAfterWrite(config.getLocalTtl())
                .build();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        cache.put(key, value);
    }

    @Override
    public String getOrLoad(String key, Supplier<String> loader) {
        return cache.get(key, k -> loader.get());
    }

    @Override
    public void invalidate(String key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public void putAll(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        cache.putAll(map);
    }
}
