package cn.xuqiudong.basic.framework.code2text.resolver.impl;

import cn.xuqiudong.basic.framework.code2text.cache.model.ResolverMeta;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import cn.xuqiudong.basic.framework.select.EnumSelectAutoConfiguration;
import cn.xuqiudong.basic.framework.select.EnumSelectable;

/**
 * 描述:
 *   枚举类 转文本
 *   适配所有实现EnumSelectable接口的枚举
 *   无注解时框架也会自动转换，加注解可自定义文本字段名/兜底策略
 * @see EnumSelectAutoConfiguration#enumSelectJacksonCustomizer()
 * @author Vic.xu
 * @since 2026-01-09 17:03
 */
public class EnumCode2TextResolver implements Code2TextResolver {

    @Override
    public ResolverMeta meta() {
        return new ResolverMeta("enum 2 text", EnumCode2TextResolver.class);
    }

    @Override
    public String codeToText(Object code) {
        if (code == null) {
            return null;
        }
        // 继续使用 枚举实现  EnumSelectable
        if (!(code instanceof EnumSelectable)) {
            return null;
        }
        return ((EnumSelectable) code).getText();

    }

    @Override
    public Object textToCode(String text) {
        // 暂时不处理  反向转换
        return null;
    }
}
