package cn.xuqiudong.common.base.code2text.cache.impl;

import cn.xuqiudong.common.base.code2text.cache.CacheRegionBundle;
import cn.xuqiudong.common.base.code2text.cache.CacheRegion;
import cn.xuqiudong.common.base.code2text.cache.Code2TextCacheManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 描述:
 * 统一入口:（单例bean）, 只管 region(CacheBundle) 路由
 *
 * @author Vic.xu
 * @since 2026-01-14 14:21
 */
public class DefaultCode2TextCacheManager implements Code2TextCacheManager {


    private final Map<String, CacheRegionBundle> bundleMap =
            new ConcurrentHashMap<>();

    @Override
    public String getOrLoad(String region,
                            String code,
                            Supplier<String> loader) {

        CacheRegionBundle b = bundleMap.get(region);
        if (b == null) {
            return loader.get();
        }
        return b.forward().getOrLoad(code, loader);
    }

    @Override
    public String getOrLoadReverse(String region, String text, Supplier<String> loader) {
        CacheRegionBundle b = bundleMap.get(region);
        if (b == null) {
            return loader.get();
        }
        return b.reverse().getOrLoad(text, loader);
    }

    @Override
    public void invalidate(String region, String key) {
        CacheRegionBundle b = bundleMap.get(region);
        if (b != null) {
            b.invalidate(key);
        }
    }


    @Override
    public void invalidateAll(String region) {
        CacheRegionBundle b = bundleMap.get(region);
        if (b != null) {
            b.invalidateAll();
        }
    }

    @Override
    public void registerBundle(CacheRegionBundle bundle) {
        bundleMap.put(bundle.name(), bundle);
    }

    @Override
    public void preload(String region, Map<String, String> data) {
        CacheRegionBundle b = bundleMap.get(region);
        if (b != null) {
            b.preload(data);
        }
    }

}
