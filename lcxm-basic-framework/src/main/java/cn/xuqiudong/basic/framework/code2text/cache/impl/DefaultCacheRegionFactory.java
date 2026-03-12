package cn.xuqiudong.basic.framework.code2text.cache.impl;

import cn.xuqiudong.basic.framework.code2text.cache.CacheRegion;
import cn.xuqiudong.basic.framework.code2text.cache.CacheRegionFactory;
import cn.xuqiudong.basic.framework.code2text.cache.model.CacheRegionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 描述:
 * 创建 CacheRegion
 *
 * @author Vic.xu
 * @since 2026-01-14 14:28
 */
public class DefaultCacheRegionFactory implements CacheRegionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCacheRegionFactory.class);

    private final StringRedisTemplate redisTemplate;

    public DefaultCacheRegionFactory(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public CacheRegion create(String name, CacheRegionConfig cfg) {

        CacheRegion local = new CaffeineCacheRegion(name, cfg);

        if (!cfg.isRedisEnabled()) {
            return local;
        }
        if (redisTemplate == null) {
            LOGGER.warn("Redis is not enabled, use local cache instead.");
            return local;
        }

        CacheRegion redis =
                new RedisCacheRegion(name, cfg, redisTemplate);

        return new CompositeCacheRegion(name, local, redis);
    }
}
