package cn.xuqiudong.basic.framework.code2text.core;

import cn.xuqiudong.basic.framework.code2text.cache.CacheRegionBundle;
import cn.xuqiudong.basic.framework.code2text.cache.CacheRegionConfigProvider;
import cn.xuqiudong.basic.framework.code2text.cache.CacheRegionFactory;
import cn.xuqiudong.basic.framework.code2text.cache.Code2TextCacheManager;
import cn.xuqiudong.basic.framework.code2text.cache.model.CacheRegionConfig;
import cn.xuqiudong.basic.framework.code2text.cache.proxy.CachedResolverProxy;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import cn.xuqiudong.basic.framework.code2text.resolver.PlaceholderCode2TextResolver;
import cn.xuqiudong.basic.framework.code2text.resolver.impl.VoidCode2TextResolver;
import cn.xuqiudong.basic.framework.code2text.type.Code2TextType;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述:
 * 注册 Code2Text 解析器, 当Code2TextResolverRegistry 初始化完成时，会自动注册所有实现Code2TextResolver的类
 *
 * @author Vic.xu
 * @since 2026-01-09 17:41
 */
public class Code2TextResolverRegistry implements InitializingBean {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Code2TextResolverRegistry.class);


    /**
     * 解析器注册表
     * Code2TextType class -> proxy resolver
     */
    private static final Map<Class<? extends Code2TextType>, Code2TextResolver> REGISTRY = new ConcurrentHashMap<>();

    /**
     * 占位符 解析器注册表
     * Code2TextType class -> proxy resolver
     */
    private static final Map<Class<? extends Code2TextType>, Code2TextResolver> PLACEHOLDER_REGISTRY =
            new ConcurrentHashMap<>();

    /**
     * 空解析器
     */
    private static final VoidCode2TextResolver VOID_RESOLVER = new VoidCode2TextResolver();


    private final ObjectProvider<Code2TextResolver> resolverProvider;
    private final CacheRegionFactory regionFactory;
    private final Code2TextCacheManager cacheManager;
    private final CacheRegionConfigProvider configProvider;

    public Code2TextResolverRegistry(
            ObjectProvider<Code2TextResolver> resolverProvider,
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
     * 对外获取 Resolver
     * 1. 获取解析器
     * 2. 获取占位符解析器
     * 3. 如果没有则返回 VoidCode2TextResolver
     */
    public static Code2TextResolver get(Class<? extends Code2TextType> typeClass) {
        Code2TextResolver resolver = REGISTRY.get(typeClass);
        if (resolver != null) {
            return resolver;
        }
        resolver = PLACEHOLDER_REGISTRY.get(typeClass);
        if (resolver != null) {
            return resolver;
        }
        LOGGER.warn("Code2TextResolver not found: [{}], will use VoidCode2TextResolver!", typeClass.getName());
        return VOID_RESOLVER;
    }

    public static <T extends Code2TextResolver> void register(T resolver) {
        register2Map(resolver);
    }

    /**
     * 注册 Resolver + Region + Proxy
     */
    public void doRegister(Code2TextResolver resolver) {
        if (resolver == null) {
            return;
        }
        // 不需要缓存 则直接注册 解析器
        if (!resolver.needCache()) {
            register2Map(resolver);
            LOGGER.info("Code2Text resolver registered(need not cache): [{}] ",
                    resolver.type().getName());
            return;
        }
        if (ObjectUtils.anyNull(configProvider, regionFactory, cacheManager)) {
            register2Map(resolver);
            LOGGER.info("Code2Text resolver registered(no cache region): [{}] , ",
                    resolver.type().getName());
            return;
        }

        // 命名空间使用Code2TextType 的简称 ★
        String regionName = resolver.getRegion();

        CacheRegionConfig config = configProvider.get(regionName);

        CacheRegionBundle regionBundle = regionFactory.createBundle(regionName, config);

        cacheManager.registerBundle(regionBundle);

        Code2TextResolver proxy =
                new CachedResolverProxy(resolver, regionName, cacheManager);

        register2Map(proxy);

        LOGGER.info("Code2Text resolver registered: {}, region: {}",
                resolver.getClass().getSimpleName(), regionName);
    }

    /**
     * 获取所有注册的解析器
     */
    public static Collection<Code2TextResolver> getAllResolvers() {
        return REGISTRY.values();
    }


    /**
     * 注册 解析器:
     * 如果是 PlaceholderCode2TextResolver 则注册到 PLACEHOLDER_REGISTRY
     */
    private static void register2Map(Code2TextResolver resolver) {
        Assert.notNull(resolver, "resolver can not be null");
        if (resolver instanceof PlaceholderCode2TextResolver) {
            PLACEHOLDER_REGISTRY.put(resolver.type(), resolver);
        } else {
            REGISTRY.put(resolver.type(), resolver);
        }
    }

}
