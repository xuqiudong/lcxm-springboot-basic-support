package cn.xuqiudong.basic.framework.code2text.resolver.impl;

import cn.xuqiudong.basic.framework.code2text.model.ResolverMeta;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import cn.xuqiudong.basic.framework.code2text.type.Code2TextType;
import cn.xuqiudong.basic.framework.code2text.type.VoidCode2Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *   空解解析器： 返回code本身
 * @author Vic.xu
 * @since 2026-06-22 8:59
 */
public class VoidCode2TextResolver implements Code2TextResolver {


    private static final Logger LOGGER = LoggerFactory.getLogger(VoidCode2TextResolver.class);

    @Override
    public ResolverMeta meta() {
        return new ResolverMeta("void resolver", VoidCode2TextResolver.class);
    }

    @Override
    public Class<? extends Code2TextType> type() {
        return VoidCode2Text.class;
    }

    @Override
    public String codeToText(Object code) {
        LOGGER.warn("VoidCode2TextResolver.codeToText() is called.");
        return code + "";
    }

    @Override
    public Object textToCode(String text) {
        return null;
    }

    @Override
    public boolean needCache() {
        return false;
    }
}
