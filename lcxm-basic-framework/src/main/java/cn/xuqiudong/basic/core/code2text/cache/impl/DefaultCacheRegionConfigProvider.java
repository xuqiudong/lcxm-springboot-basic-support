package cn.xuqiudong.basic.core.code2text.cache.impl;

import cn.xuqiudong.basic.core.code2text.cache.CacheRegionConfigProvider;
import cn.xuqiudong.basic.core.code2text.cache.model.CacheRegionConfig;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2026-01-14 14:35
 */
public class DefaultCacheRegionConfigProvider
        implements CacheRegionConfigProvider {

    @Override
    public CacheRegionConfig get(String regionName) {
        // 全部默认值
        return new CacheRegionConfig();
    }
}
