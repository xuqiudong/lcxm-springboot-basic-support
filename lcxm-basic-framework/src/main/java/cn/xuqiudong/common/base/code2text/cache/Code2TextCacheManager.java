package cn.xuqiudong.common.base.code2text.cache;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 描述:
 * 统一入口:（单例bean）, 只管 region 路由
 * - Proxy 调用 getOrLoad
 * - 事件监听调用 invalidate
 * - 启动期注册 Region
 * @author Vic.xu
 * @since 2026-01-14 14:04
 */
public interface Code2TextCacheManager {

    /**
     * 获取缓存数据
     */
    String getOrLoad(String region,
                     String key,
                     Supplier<String> loader);

    /**
     * 单个清除
     */
    void invalidate(String region, String key);

    /**
     * 批量清除
     */
    void invalidateAll(String region);

    /**
     * 注册 region
     */
    void registerRegion(CacheRegion region);

    /**
     * 预加载
     */
    void preload(String region, Map<String, String> data);
}
