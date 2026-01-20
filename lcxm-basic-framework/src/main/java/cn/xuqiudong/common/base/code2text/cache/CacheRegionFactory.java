package cn.xuqiudong.common.base.code2text.cache;

import cn.xuqiudong.common.base.code2text.cache.model.CacheRegionConfig;

/**
 * 描述:
 * Registry 初始化时需要创建 Region
 *
 * @author Vic.xu
 * @since 2026-01-14 14:06
 */
public interface CacheRegionFactory {

    CacheRegion create(String regionName,
                       CacheRegionConfig config);

    /**
     * 创建正反向缓存区域
     */
    default CacheRegionBundle createBundle(String regionName,
                                           CacheRegionConfig config) {
        CacheRegion forward = create(regionName, config);
        if (config.isText2CodeCacheable()) {
            CacheRegion reverse = create(regionName + "Reverse", config);
            return new CacheRegionBundle(regionName, forward, reverse);
        }

        return new CacheRegionBundle(regionName, forward, null);
    }
}
