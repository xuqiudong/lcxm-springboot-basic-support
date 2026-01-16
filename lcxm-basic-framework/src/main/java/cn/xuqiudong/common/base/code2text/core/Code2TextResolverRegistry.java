package cn.xuqiudong.common.base.code2text.core;

import cn.xuqiudong.common.base.code2text.cache.CacheRegionConfigProvider;
import cn.xuqiudong.common.base.code2text.cache.CacheRegionFactory;
import cn.xuqiudong.common.base.code2text.cache.proxy.CachedResolverProxy;
import cn.xuqiudong.common.base.code2text.cache.Code2TextCacheManager;
import cn.xuqiudong.common.base.code2text.cache.CacheRegion;
import cn.xuqiudong.common.base.code2text.cache.model.CacheRegionConfig;
import cn.xuqiudong.common.base.code2text.resolver.Code2TextResolver;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述:
 * 注册 Code2Text 解析器, 当Code2TextResolverRegistry 初始化完成时，会自动注册所有实现Code2TextResolver的类
 *
 *
 * @author Vic.xu
 * @since 2026-01-09 17:41
 */
public class Code2TextResolverRegistry implements InitializingBean {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Code2TextResolverRegistry.class);

    /**
     * 解析器注册表
     */
    /**
     * annotationType -> proxy resolver
     */
    private static final Map<Class<? extends Annotation>, Code2TextResolver<?>> REGISTRY =
            new ConcurrentHashMap<>();

    private final ObjectProvider<Code2TextResolver<?>> resolverProvider;
    private final CacheRegionFactory regionFactory;
    private final Code2TextCacheManager cacheManager;
    private final CacheRegionConfigProvider configProvider;

    public Code2TextResolverRegistry(
            ObjectProvider<Code2TextResolver<?>> resolverProvider,
            CacheRegionFactory regionFactory,
            Code2TextCacheManager cacheManager,
            CacheRegionConfigProvider configProvider) {

        this.resolverProvider = resolverProvider;
        this.regionFactory = regionFactory;
        this.cacheManager = cacheManager;
        this.configProvider = configProvider;
    }

    @Override
    public void afterPropertiesSet() {

        resolverProvider.orderedStream().forEach(this::doRegister);

        LOGGER.info("Code2TextResolverRegistry init success, resolver size: {}",
                REGISTRY.size());
    }

    /**
     * 对外获取 Resolver（实际是 Proxy）
     */
    public static Code2TextResolver<? extends Annotation> get(
            Class<? extends Annotation> annoType) {
        return REGISTRY.get(annoType);
    }

    public static void register(Code2TextResolver<? extends Annotation> resolver) {
        REGISTRY.put(resolver.annotationType(), resolver);
    }

    /**
     * 注册 Resolver + Region + Proxy
     */
    public void doRegister(Code2TextResolver<? extends Annotation> resolver) {
        if (resolver == null) {
            return;
        }
        if (ObjectUtils.anyNull(configProvider, regionFactory, cacheManager)) {
            REGISTRY.put(resolver.annotationType(), resolver);
            return;
        }

        Class<? extends Annotation> annoType = resolver.annotationType();
        // 命名空间使用注解的简称
        String regionName = annoType.getSimpleName();

        CacheRegionConfig config = configProvider.get(regionName);

        CacheRegion region = regionFactory.create(regionName, config);

        cacheManager.registerRegion(region);

        Code2TextResolver proxy =
                new CachedResolverProxy(resolver, regionName, cacheManager);

        REGISTRY.put(annoType, proxy);

        LOGGER.info("Code2Text resolver registered: {}, region: {}",
                resolver.getClass().getSimpleName(), regionName);
    }

    /**
     * 获取所有注册的解析器
     */
    public static Collection<Code2TextResolver<?>> getAllResolvers() {
        return REGISTRY.values();
    }

}
