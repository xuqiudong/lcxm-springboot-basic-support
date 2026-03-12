package cn.xuqiudong.basic.core.code2text.cache.event;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 描述:
 * 失效事件模型
 * @author Vic.xu
 * @since 2026-01-15 14:20
 */
@Getter
public class Code2TextCacheEvictEvent extends ApplicationEvent {

    private final String region;
    /**
     * 缓存key: null 表示全部失效
     */
    private final String key;

    public Code2TextCacheEvictEvent(String region, String key) {
        super(region);
        this.region = region;
        this.key = key;
    }

    public boolean isAll() {
        return key == null;
    }
}
