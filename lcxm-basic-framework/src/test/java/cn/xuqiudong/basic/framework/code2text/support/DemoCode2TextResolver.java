package cn.xuqiudong.basic.framework.code2text.support;

import cn.xuqiudong.basic.framework.code2text.cache.Code2TextPreloadable;
import cn.xuqiudong.basic.framework.code2text.cache.model.ResolverMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2026-01-12 10:46
 */
public class DemoCode2TextResolver implements Code2TextPreloadable {


    @Override
    public ResolverMeta meta() {
        return new ResolverMeta("DemoCode2TextResolver", DemoCode2TextResolver.class);
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

    public static void main(String[] args) {
        DemoCode2TextResolver resolver = new DemoCode2TextResolver();
        System.out.println(resolver.getClass().getSimpleName());
        System.out.println(resolver.codeToText("1"));
    }
}
