package cn.xuqiudong.basic.framework.code2text;

import cn.xuqiudong.basic.framework.code2text.annotation.Code2Text;
import cn.xuqiudong.basic.framework.code2text.cache.CacheRegionConfigProvider;
import cn.xuqiudong.basic.framework.code2text.cache.CacheRegionFactory;
import cn.xuqiudong.basic.framework.code2text.cache.Code2TextCacheManager;
import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextRedisEvictListener;
import cn.xuqiudong.basic.framework.code2text.cache.event.Code2TextSpringEvictListener;
import cn.xuqiudong.basic.framework.code2text.cache.event.RedisCacheEvictPublisher;
import cn.xuqiudong.basic.framework.code2text.cache.impl.DefaultCacheRegionConfigProvider;
import cn.xuqiudong.basic.framework.code2text.cache.impl.DefaultCacheRegionFactory;
import cn.xuqiudong.basic.framework.code2text.cache.impl.DefaultCode2TextCacheManager;
import cn.xuqiudong.basic.framework.code2text.cache.runner.Code2TextPreloadRunner;
import cn.xuqiudong.basic.framework.code2text.core.Code2TextResolverRegistry;
import cn.xuqiudong.basic.framework.code2text.helper.Code2TextCacheHelper;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import cn.xuqiudong.basic.framework.code2text.resolver.impl.DefaultUserCode2TextResolver;
import cn.xuqiudong.basic.framework.code2text.resolver.impl.EnumCode2TextResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 描述:
 * 序列化时 编码转文本 自动装配类
 *
 * @author Vic.xu
 * @see Code2Text
 * @since 2026-01-14 17:22
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnProperty(prefix = "lcxm.framework.code2text", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Code2TextAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Code2TextAutoConfiguration.class);

    public static final String CHANNEL = "code2text:cache:evict";

    public Code2TextAutoConfiguration() {
        LOGGER.info("code2text: Code2TextAutoConfiguration init...");
    }

    /**
     * 缓存配置提供者
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheRegionConfigProvider cacheRegionConfigProvider() {
        LOGGER.info("code2text: DefaultCacheRegionConfigProvider init...");
        return new DefaultCacheRegionConfigProvider();
    }

    /**
     * 缓存管理器: 入口，对应一个resolver ,路由到指定的 Region
     */
    @Bean
    @ConditionalOnMissingBean
    public Code2TextCacheManager code2TextCacheManager() {
        LOGGER.info("code2text: DefaultCode2TextCacheManager init...");
        return new DefaultCode2TextCacheManager();
    }

    /**
     * 缓存工厂: 创建 CacheRegion
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheRegionFactory cacheRegionFactory(ObjectProvider<StringRedisTemplate> redisTemplate) {
        LOGGER.info("code2text: DefaultCacheRegionFactory init...");
        return new DefaultCacheRegionFactory(redisTemplate.getIfAvailable());
    }

    /**
     * 解析器注册表: 注册每个类型的code转text的解析器
     * 并创建带缓存的解析器 代理类
     */
    @Bean
    @ConditionalOnMissingBean
    public Code2TextResolverRegistry code2TextResolverRegistry(ObjectProvider<Code2TextResolver> resolverProvider,
                                                               CacheRegionFactory regionFactory,
                                                               Code2TextCacheManager cacheManager,
                                                               CacheRegionConfigProvider configProvider) {
        LOGGER.info("code2text: Code2TextResolverRegistry init...");
        return new Code2TextResolverRegistry(resolverProvider, regionFactory, cacheManager, configProvider);
    }

    /**
     * 枚举解析器
     */
    @Bean
//    @ConditionalOnMissingGenericBean(beanInterface = Code2TextResolver.class, genericType = EnumCode2Text.class)
    @ConditionalOnMissingBean
    public EnumCode2TextResolver enumCode2TextResolver() {
        LOGGER.info("code2text: EnumCode2TextResolver init...");
        return new EnumCode2TextResolver();
    }

    /**
     * userCode 解析器
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultUserCode2TextResolver defaultUserCode2TextResolver() {
        LOGGER.warn("code2text: DefaultUserCode2TextResolver init. Please define an implementation class for UserCode2Text by yourself");
        return new DefaultUserCode2TextResolver();
    }



    /* ************************** 缓存清理相关 bean below *********************************** */

    /**
     * redis 缓存失效监听器: 只删除本机的 ，不再触发事件发布
     */
    @Bean
    public Code2TextRedisEvictListener redisCacheEvictListener(
            ObjectProvider<Code2TextCacheManager> code2TextCacheManager) {
        LOGGER.info("code2text: redis缓存失效监听器 init...");
        return new Code2TextRedisEvictListener(code2TextCacheManager.getIfAvailable());
    }

    /**
     * spring 缓存失效监听器: 删除本地和redis,  发布redis监听事件， 其他节点监听 到后删除本地缓存
     */
    @Bean
    @ConditionalOnMissingBean
    public Code2TextSpringEvictListener code2TextEvictSpringListener(ObjectProvider<Code2TextCacheManager> cacheManager, RedisCacheEvictPublisher redisCacheEvictPublisher) {
        LOGGER.info("code2text: 本地缓存失效监听器 init...");
        return new Code2TextSpringEvictListener(cacheManager.getIfAvailable(), redisCacheEvictPublisher);
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            ObjectProvider<RedisConnectionFactory> factoryProvider,
            Code2TextRedisEvictListener listener) {

        RedisMessageListenerContainer c =
                new RedisMessageListenerContainer();
        c.setConnectionFactory(factoryProvider.getIfAvailable());
        c.addMessageListener(listener, new ChannelTopic(CHANNEL));
        return c;
    }

    /**
     * redis 缓存清理发布者:  监听 spring的 Code2TextCacheEvictEvent  事件
     */
    @Bean
    public RedisCacheEvictPublisher redisCacheEvictPublisher(
            StringRedisTemplate redisTemplate) {
        LOGGER.info("code2text: redis缓存清理发布者 init...");
        return new RedisCacheEvictPublisher(redisTemplate);
    }

    /**
     * 缓存清理助手: 封装了 spring 的Code2TextCacheEvictEvent事件发布
     * 提供静态方法 ，方便使用
     */
    @Bean
    @ConditionalOnMissingBean
    public Code2TextCacheHelper code2TextCacheHelper(ApplicationEventPublisher applicationEventPublisher) {
        LOGGER.info("code2text: 缓存清理助手(提供静态清理方法(发布事件)) init...");
        Code2TextCacheHelper code2TextCacheHelper = new Code2TextCacheHelper();
        code2TextCacheHelper.setPublisher(applicationEventPublisher);
        return code2TextCacheHelper;
    }

    /**
     * 缓存预加载执行器
     */
    @Bean
    @ConditionalOnMissingBean
    public Code2TextPreloadRunner code2TextPreloadRunner(Code2TextCacheManager cacheManager) {
        LOGGER.info("code2text: 缓存预加载执行器 init...");
        return new Code2TextPreloadRunner(cacheManager);
    }


}
