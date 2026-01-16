package cn.xuqiudong.common.base.code2text.cache.impl;

import cn.xuqiudong.common.base.code2text.cache.CacheRegion;
import cn.xuqiudong.common.base.code2text.cache.Code2TextCacheManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 描述:
 * 统一入口:（单例bean）, 只管 region 路由
 *
 * @author Vic.xu
 * @since 2026-01-14 14:21
 */
public class DefaultCode2TextCacheManager implements Code2TextCacheManager {

    private final Map<String, CacheRegion> regionMap =
            new ConcurrentHashMap<>();

    @Override
    public String getOrLoad(String region,
                            String key,
                            Supplier<String> loader) {

        CacheRegion r = regionMap.get(region);
        if (r == null) {
            return loader.get();
        }
        return r.getOrLoad(key, loader);
    }

    @Override
    public void invalidate(String region, String key) {
        CacheRegion r = regionMap.get(region);
        if (r != null) {
            r.invalidate(key);
        }
    }


    @Override
    public void invalidateAll(String region) {
        CacheRegion r = regionMap.get(region);
        if (r != null) {
            r.invalidateAll();
        }
    }

    @Override
    public void registerRegion(CacheRegion region) {
        regionMap.put(region.name(), region);
    }

    @Override
    public void preload(String region, Map<String, String> data) {
        CacheRegion r = regionMap.get(region);
        if (r == null) {
            return;
        }
        r.invalidateAll();
        r.putAll(data);
    }

}
