package cn.xuqiudong.common.base.code2text.cache.model;

import lombok.Data;

import java.time.Duration;

/**
 * 描述:
 * Region 配置模型
 *
 * @author Vic.xu
 * @since 2026-01-14 14:06
 */
@Data
public class CacheRegionConfig {
    /**
     * 本地缓存 TTL
     */
    private Duration localTtl = Duration.ofMinutes(10);
    /**
     * Redis TTL
      */
    private Duration redisTtl = Duration.ofHours(2);

    /**
     * 最大缓存数量
     */
    private int maxSize = 2000;

    /**
     * 是否预加载
     */
    private boolean preload;

    /**
     * 是否缓存 text -> code
     */
    private boolean text2CodeCacheable = true;


    /**
     * 是否启用 Redis
     * 默认启用： 但是会判断容器是否提供StringRedisTemplate
     */
    private boolean redisEnabled = true;
}
