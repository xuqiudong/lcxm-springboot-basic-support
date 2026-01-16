package cn.xuqiudong.common.base.code2text.resolver.impl;

import cn.xuqiudong.common.base.code2text.annotation.EnumCode2Text;
import cn.xuqiudong.common.base.code2text.resolver.Code2TextResolver;
import cn.xuqiudong.common.base.select.EnumSelectable;

/**
 * 描述:
 *   枚举类 转文本
 * @author Vic.xu
 * @since 2026-01-09 17:03
 */
public class EnumCode2TextResolver implements Code2TextResolver<EnumCode2Text> {
    @Override
    public Class<EnumCode2Text> annotationType() {
        return EnumCode2Text.class;
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
