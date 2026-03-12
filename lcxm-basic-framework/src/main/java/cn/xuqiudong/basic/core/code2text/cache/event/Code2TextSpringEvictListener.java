package cn.xuqiudong.basic.core.code2text.cache.event;

import cn.xuqiudong.basic.core.code2text.cache.Code2TextCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * 描述:
 *  本地失效监听器: 删除本地和redis,  发布redis监听事件， 其他节点监听 到后删除本地缓存
 * @see Code2TextRedisEvictListener
 * @author Vic.xu
 * @since 2026-01-15 14:21
 */
public class Code2TextSpringEvictListener implements ApplicationListener<Code2TextCacheEvictEvent> {


    private static final Logger LOGGER = LoggerFactory.getLogger(Code2TextSpringEvictListener.class);

    private final Code2TextCacheManager cacheManager;

    private final RedisCacheEvictPublisher redisCacheEvictPublisher;

    public Code2TextSpringEvictListener(Code2TextCacheManager cacheManager, RedisCacheEvictPublisher redisCacheEvictPublisher) {
        this.cacheManager = cacheManager;
        this.redisCacheEvictPublisher = redisCacheEvictPublisher;
    }

    /**
     * 清除缓存, 并发布redis事件
     * @see Code2TextRedisEvictListener
     */
    @Override
    public void onApplicationEvent(Code2TextCacheEvictEvent event) {
        LOGGER.debug("监听到Code2TextCacheEvictEvent事件， 将要清除{}下的{}缓存", event.getRegion(), event.isAll() ? "所有" : event.getKey());

        if (event.isAll()) {
            cacheManager.invalidateAll(event.getRegion());
        } else {
            cacheManager.invalidate(event.getRegion(), event.getKey());
        }
        redisCacheEvictPublisher.sendRedisEvictEvent(event);
    }

}
