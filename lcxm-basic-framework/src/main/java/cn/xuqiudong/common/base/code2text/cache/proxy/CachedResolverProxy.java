package cn.xuqiudong.common.base.code2text.cache.proxy;

import cn.xuqiudong.common.base.code2text.cache.Code2TextCacheManager;
import cn.xuqiudong.common.base.code2text.resolver.Code2TextResolver;

import java.lang.annotation.Annotation;

/**
 * 描述:
 *  运行期对象， 每个resolver对应一个代理对象
 * @author Vic.xu
 * @since 2026-01-14 14:09
 */
public class CachedResolverProxy<A extends Annotation>
        implements Code2TextResolver<A> {

    private final Code2TextResolver<A> delegate;
    private final String region;
    private final Code2TextCacheManager cacheManager;

    public CachedResolverProxy(Code2TextResolver<A> delegate,
                               String region,
                               Code2TextCacheManager cacheManager) {
        this.delegate = delegate;
        this.region = region;
        this.cacheManager = cacheManager;
    }

    /**
     * 获取代理对象
     */
    public Code2TextResolver<A> getDelegate() {
        return delegate;
    }

    @Override
    public Class<A> annotationType() {
        return delegate.annotationType();
    }

    @Override
    public String codeToText(Object code) {
        if (code == null) {
            return null;
        }
        String key = String.valueOf(code);

        return cacheManager.getOrLoad(region, key,
                () -> delegate.codeToText(code));
    }

    @Override
    public Object textToCode(String text) {
        //TODO 缓存
        return delegate.textToCode(text);
    }
}
