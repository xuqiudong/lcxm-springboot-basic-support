package cn.xuqiudong.common.base.code2text.cache;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 真正缓存的细节，  一个 Resolver = 一个 Region = 一个命名空间  = 序列化注解的 simpleName
 * @author Vic.xu
 * @since 2026-01-14 14:03
 */
public interface  CacheRegion {

    String name();

    String get(String key);

    void put(String key, String value);

    String getOrLoad(String key, Supplier<String> loader);

    void invalidate(String key);

    void invalidateAll();

    default void putAll(Map<String, String> map) {
        if (map != null) {
            map.forEach(this::put);
        }
    }
}
