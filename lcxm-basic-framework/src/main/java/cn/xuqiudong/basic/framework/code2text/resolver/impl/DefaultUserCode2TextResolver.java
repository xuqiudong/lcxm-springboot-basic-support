package cn.xuqiudong.basic.framework.code2text.resolver.impl;


import cn.xuqiudong.basic.framework.code2text.cache.model.ResolverMeta;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *  因为 BaseMpEntity 中 使用了 @UserCode2Text 注解，所以这里需要实现一个默认的解析器(但是不做真的解析)
 *  具体实现 由各自的项目自行实现 Code2TextResolver 或Code2TextPreloadable
 * @see cn.xuqiudong.basic.framework.code2text.annotation.UserCode2Text;
 * @author Vic.xu
 * @since 2026-03-27 9:36
 */
public class DefaultUserCode2TextResolver implements Code2TextResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserCode2TextResolver.class);

    @Override
    public ResolverMeta meta() {
        return new ResolverMeta("default user resolver", DefaultUserCode2TextResolver.class);
    }
    @Override
    public String codeToText(Object code) {
        LOGGER.warn("DefaultUserCode2TextResolver.codeToText() is called, cause no implementation found.");
        return "" + code;
    }

    @Override
    public Object textToCode(String text) {
        LOGGER.warn("DefaultUserCode2TextResolver.textToCode() is called, , cause no implementation found. ");
        return text;
    }

    @Override
    public boolean needCache() {
        return false;
    }
}
