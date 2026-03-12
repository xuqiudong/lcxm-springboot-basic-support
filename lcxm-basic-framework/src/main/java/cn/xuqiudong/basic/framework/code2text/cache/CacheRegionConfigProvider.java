package cn.xuqiudong.basic.framework.code2text.cache;

import cn.xuqiudong.basic.framework.code2text.cache.model.CacheRegionConfig;

/**
 * 描述:
 *  code2text 的命名空间下的缓存配置 提供者
 * @author Vic.xu
 * @since 2026-01-14 14:34
 */
public interface  CacheRegionConfigProvider {

    CacheRegionConfig get(String regionName);
}
