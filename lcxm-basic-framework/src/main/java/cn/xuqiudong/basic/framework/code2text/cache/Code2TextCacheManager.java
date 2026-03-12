package cn.xuqiudong.basic.framework.code2text.cache;

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
     * 获取code对应的text
     */
    String getOrLoad(String region,
                     String code,
                     Supplier<String> loader);

    /**
     * 获取text对应的code
     */
    String getOrLoadReverse(String region,
                             String text,
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
     * 注册 CacheBundle(region)
     */
    void registerBundle(CacheRegionBundle bundle);


    /**
     * 预加载
     */
    void preload(String region, Map<String, String> data);
}
