package cn.xuqiudong.basic.core.code2text.support;

import cn.xuqiudong.basic.core.code2text.cache.Code2TextPreloadable;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2026-01-12 10:46
 */
public class DemoCode2TextResolver implements Code2TextPreloadable<DemoCode2Text> {


    @Override
    public Class<DemoCode2Text> annotationType() {
        return DemoCode2Text.class;
    }

    @Override
    public String codeToText(Object code) {
        return code + "-Demo";
    }

    @Override
    public Object textToCode(String text) {
        return "none code for " + text;
    }

    @Override
    public Map<String, String> preload() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "1-Demo");
        map.put("2", "2-Demo");
        map.put("3", "3-Demo");
        map.put("code1", "code1-preload");
        map.put("code2", "code2-preload");
        map.put("code3", "code3-preload");
        return map;
    }
}
