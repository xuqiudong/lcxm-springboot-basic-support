package cn.xuqiudong.common.base.code2text.helper;

import cn.xuqiudong.common.base.code2text.core.Code2TextResolverRegistry;
import cn.xuqiudong.common.base.code2text.resolver.Code2TextResolver;

import java.lang.annotation.Annotation;

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
     *   需要在spring环境下 使用
     * @param annoType 注解类型
     * @param code     code
     * @return text
     */
    public static String getText(Class<? extends Annotation> annoType, String code) {

        Code2TextResolver resolver = Code2TextResolverRegistry.get(annoType);

        if (resolver == null) {
            return null;
        }
        return resolver.codeToText(code);
    }

    public static Object getCode(Class<? extends Annotation> annoType, String text) {

        Code2TextResolver resolver = Code2TextResolverRegistry.get(annoType);

        if (resolver == null) {
            return null;
        }
        return resolver.textToCode(text);
    }


    /**
     * 获取 code 对应的 text
     *   需要在spring环境下 使用
     * @param annoType 注解类型
     * @param code     code
     * @param defaultText 默认值
     * @return text
     */
    public static String getText(Class<? extends Annotation> annoType, String code, String defaultText) {
        Code2TextResolver resolver = Code2TextResolverRegistry.get(annoType);

        if (resolver == null) {
            return defaultText;
        }
        String text = resolver.codeToText(code);

        return text == null ? defaultText : text;
    }
}
