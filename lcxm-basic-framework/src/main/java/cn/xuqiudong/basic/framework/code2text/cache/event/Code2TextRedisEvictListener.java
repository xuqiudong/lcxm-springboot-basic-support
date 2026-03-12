package cn.xuqiudong.basic.framework.code2text.cache.event;

import cn.xuqiudong.basic.framework.code2text.cache.Code2TextCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;

/**
 * 描述:
 * redis 缓存失效监听器： 只删除本地的 ，不再触发事件发布
 *
 * @author Vic.xu
 * @since 2026-01-15 16:46
 */
public class Code2TextRedisEvictListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Code2TextRedisEvictListener.class);

    private final Code2TextCacheManager cacheManager;

    public Code2TextRedisEvictListener(Code2TextCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        LOGGER.debug("监听到redis清除code2text缓存事件: {}", msg);
        String[] arr = msg.split("\\|", 2);
        String region = arr[0];
        String key = "*".equals(arr[1]) ? null : arr[1];

        if (key == null) {
            cacheManager.invalidateAll(region);
        } else {
            cacheManager.invalidate(region, key);
        }
    }
}
