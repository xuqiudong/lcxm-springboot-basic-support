package cn.xuqiudong.basic.core.code2text.cache.proxy;

import cn.xuqiudong.basic.core.code2text.cache.Code2TextCacheManager;
import cn.xuqiudong.basic.core.code2text.resolver.Code2TextResolver;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.StringJoiner;

/**
 * 描述:
 * 运行期对象， 每个resolver对应一个代理对象
 *
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
        if (StringUtils.isBlank(key)) {
            return null;
        }

        //支持多个code
        if (delegate.supportMultiValue()) {
            String[] codes = StringUtils.split(key, delegate.getSeparator());
            if (codes != null && codes.length > 1) {
                StringJoiner joiner = new StringJoiner(delegate.getSeparator());
                for (String c : codes) {
                    joiner.add(cacheManager.getOrLoad(region, c,
                            () -> delegate.codeToText(c)));
                }
                return joiner.toString();
            }
        }

        return cacheManager.getOrLoad(region, key,
                () -> delegate.codeToText(code));
    }

    @Override
    public Object textToCode(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        //支持多个text
        if (delegate.supportMultiValue()) {
            String[] texts = StringUtils.split(text, delegate.getSeparator());
            if (texts != null && texts.length > 1) {
                StringJoiner joiner = new StringJoiner(delegate.getSeparator());
                for (String t : texts) {
                    joiner.add(cacheManager.getOrLoadReverse(region, t,
                            () -> {
                                Object o = delegate.textToCode(text);
                                return o == null ? null : String.valueOf(o);
                            }));
                }
                return joiner.toString();
            }
        }

        return cacheManager.getOrLoadReverse(region, text, () -> {
            Object o = delegate.textToCode(text);
            return o == null ? null : String.valueOf(o);
        });
    }

}
