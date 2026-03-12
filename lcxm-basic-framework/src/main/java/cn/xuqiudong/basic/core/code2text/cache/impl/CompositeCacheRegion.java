package cn.xuqiudong.basic.core.code2text.cache.impl;

import cn.xuqiudong.basic.core.code2text.cache.CacheRegion;
import cn.xuqiudong.basic.core.code2text.cache.model.CacheRegionConfig;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 描述:
 * 组合 Region： caffeine + redis
 *
 * @author Vic.xu
 * @see DefaultCacheRegionFactory#create(String, CacheRegionConfig)
 * @since 2026-01-14 17:11
 */
public class CompositeCacheRegion implements CacheRegion {

    private final String name;
    private final CacheRegion level1;
    private final CacheRegion level2;

    public CompositeCacheRegion(String name,
                                CacheRegion level1,
                                CacheRegion level2) {
        this.name = name;
        this.level1 = level1;
        this.level2 = level2;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String get(String key) {
        String v = level1.get(key);
        if (v != null) {
            return v;
        }
        v = level2.get(key);
        if (v != null) {
            String finalV = v;
            level1.getOrLoad(key, () -> finalV);
        }
        return v;
    }

    @Override
    public String getOrLoad(String key, Supplier<String> loader) {

        String v = level1.get(key);
        if (v != null) {
            return v;
        }

        v = level2.get(key);
        if (v != null) {
            String finalV = v;
            level1.getOrLoad(key, () -> finalV);
            return v;
        }

        v = loader.get();
        if (v != null) {
            String finalV1 = v;
            level1.getOrLoad(key, () -> finalV1);
            level2.getOrLoad(key, () -> finalV1);
        }
        return v;
    }

    @Override
    public void invalidate(String key) {
        level1.invalidate(key);
        level2.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        level1.invalidateAll();
        level2.invalidateAll();
    }

    @Override
    public void put(String key, String value) {
        level1.put(key, value);
        level2.put(key, value);
    }

    @Override
    public void putAll(Map<String, String> map) {
        level2.invalidateAll();
        level2.putAll(map);
        level1.invalidateAll();
        level1.putAll(map);
    }
}
