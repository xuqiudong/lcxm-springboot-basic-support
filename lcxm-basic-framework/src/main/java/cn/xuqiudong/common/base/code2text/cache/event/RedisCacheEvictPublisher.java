package cn.xuqiudong.common.base.code2text.cache.event;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 描述:
 *    发布 Redis:  本机删缓存 → Spring Event → Redis 广播
 * @see Code2TextRedisEvictListener
 * @author Vic.xu
 * @since 2026-01-15 16:43
 */
public class RedisCacheEvictPublisher {
    private static final String CHANNEL = "code2text:cache:evict";

    private final StringRedisTemplate redisTemplate;

    public RedisCacheEvictPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sendRedisEvictEvent(Code2TextCacheEvictEvent event) {
        String msg = serialize(event);
        redisTemplate.convertAndSend(CHANNEL, msg);
    }

    private String serialize(Code2TextCacheEvictEvent e) {
        if (e.getKey() == null) {
            return e.getRegion() + "|*";
        }
        return e.getRegion() + "|" + e.getKey();
    }
}
