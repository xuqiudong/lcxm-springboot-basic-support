package cn.xuqiudong.basic.framework.code2text.cache.runner;

import cn.xuqiudong.basic.framework.code2text.cache.Code2TextCacheManager;
import cn.xuqiudong.basic.framework.code2text.cache.Code2TextPreloadable;
import cn.xuqiudong.basic.framework.code2text.cache.proxy.CachedResolverProxy;
import cn.xuqiudong.basic.framework.code2text.core.Code2TextResolverRegistry;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Collection;
import java.util.Map;

/**
 * 描述:
 *   code2text 缓存预加载执行器
 * @author Vic.xu
 * @since 2026-01-16 13:46
 */
public class Code2TextPreloadRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Code2TextPreloadRunner.class);

    private Code2TextCacheManager cacheManager;

    public Code2TextPreloadRunner(Code2TextCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (cacheManager == null) {
            return;
        }
        LOGGER.info("code2text: 开始预加载 Code2TextResolver 缓存数据");
        Collection<Code2TextResolver<?>> allResolvers = Code2TextResolverRegistry.getAllResolvers();
        for (Code2TextResolver<?> resolver : allResolvers) {
            Code2TextResolver<?> target = resolver;
            // 如果是代理对象，则获取原始对象
            if (target instanceof CachedResolverProxy proxy) {
                target = proxy.getDelegate();
            }

            if (!(target instanceof Code2TextPreloadable<?> preloadable)) {
                continue;
            }
            String region = resolver.annotationType().getSimpleName();

            Map<String, String> data = preloadable.preload();
            if (data == null || data.isEmpty()) {
                continue;
            }
            LOGGER.info("code2text: 预加载 {} 缓存数据完成, size: {}", region, data.size());
            cacheManager.preload(region, data);

        }
    }
}
