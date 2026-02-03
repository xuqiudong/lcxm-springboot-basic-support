package cn.xuqiudong.common.base.code2text.cache;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述:
 * 同时绑定 code -> text 和 text -> code的缓存。
 * 但是text -> code 的缓存， 都是来自正向处理的时候的同步
 *
 * @author Vic.xu
 * @since 2026-01-19 15:25
 */
public class CacheRegionBundle {


    private final String name;

    /**
     * code -> text
     */
    private final CacheRegion forward;
    /**
     * text -> code
     */
    private final CacheRegion reverse;

    public CacheRegionBundle(String name, @NonNull CacheRegion forwardRegion, @Nullable CacheRegion reverseRegion) {
        this.name = name;
        this.forward = forwardRegion;
        this.reverse = reverseRegion;
    }

    public String name() {
        return name;
    }

    /**
     * code -> text region
     */
    public CacheRegion forward() {
        return forward;
    }

    /**
     * text -> code  region
     */
    public CacheRegion reverse() {
        return reverse;
    }

    /**
     * 批量清除: 正向 和 反向
     */
    public void invalidateAll() {
        forward.invalidateAll();
        if (reverse != null) {
            reverse.invalidateAll();
        }
    }

    /**
     * 单个清除: 正向 和 反向
     */
    public void invalidate(String code) {
        if (reverse != null) {
            String text = forward.get(code);
            if (text != null) {
                reverse.invalidate(text);
            }
        }
        forward.invalidate(code);

    }

    /**
     * 预加载: 正向 和 反向
     */
    public void preload(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        if (reverse != null) {
            Map<String, String> reverseData = data.entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue(), entry -> entry.getKey(), (k1, k2) -> k1));
            reverse.invalidateAll();
            reverse.putAll(reverseData);
        }

        forward.invalidateAll();
        forward.putAll(data);
    }
}
