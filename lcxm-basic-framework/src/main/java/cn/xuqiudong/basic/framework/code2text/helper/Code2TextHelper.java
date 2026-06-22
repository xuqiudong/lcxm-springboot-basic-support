package cn.xuqiudong.basic.framework.code2text.helper;

import cn.xuqiudong.basic.framework.code2text.core.Code2TextResolverRegistry;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import cn.xuqiudong.basic.framework.code2text.type.Code2TextType;

/**
 * 描述:
 * 静态方法获取 code 对应的 text
 *
 * @author Vic.xu
 * @since 2026-01-16 14:49
 */
public class Code2TextHelper {

    private Code2TextHelper() {
    }

    /**
     * 获取 code 对应的 text
     * 需要在spring环境下 使用
     *
     * @param typeClass type
     * @param code      code
     * @return text
     */
    public static String getText(Class<? extends Code2TextType> typeClass, String code) {

        Code2TextResolver resolver = Code2TextResolverRegistry.get(typeClass);

        if (resolver == null) {
            return null;
        }
        return resolver.codeToText(code);
    }

    /**
     * 获取 text 对应的 code
     * 需要在spring环境下 使用
     *
     * @param typeClass type
     * @param text      text
     * @return code
     */
    public static Object getCode(Class<? extends Code2TextType> typeClass, String text) {

        Code2TextResolver resolver = Code2TextResolverRegistry.get(typeClass);

        if (resolver == null) {
            return null;
        }
        return resolver.textToCode(text);
    }


    /**
     * 获取 code 对应的 text
     * 需要在spring环境下 使用
     *
     * @param typeClass   解析器类型
     * @param code        code
     * @param defaultText 默认值
     * @return text
     */
    public static String getText(Class<? extends Code2TextType> typeClass, String code, String defaultText) {
        Code2TextResolver resolver = Code2TextResolverRegistry.get(typeClass);

        if (resolver == null) {
            return defaultText;
        }
        String text = resolver.codeToText(code);

        return text == null ? defaultText : text;
    }
}
