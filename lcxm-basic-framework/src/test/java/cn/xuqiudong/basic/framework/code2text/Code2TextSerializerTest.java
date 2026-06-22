package cn.xuqiudong.basic.framework.code2text;

import cn.xuqiudong.basic.core.util.JsonUtil;
import cn.xuqiudong.basic.framework.BaseTest;
import cn.xuqiudong.basic.framework.code2text.core.Code2TextSerializer;
import cn.xuqiudong.basic.framework.code2text.helper.Code2TextCacheHelper;
import cn.xuqiudong.basic.framework.code2text.helper.Code2TextHelper;
import cn.xuqiudong.basic.framework.code2text.support.Code2TextDemoModel;
import cn.xuqiudong.basic.framework.code2text.support.DemoCode2Text;
import cn.xuqiudong.basic.framework.code2text.support.DemoCode2TextResolver;
import cn.xuqiudong.basic.framework.code2text.support.TestCode2TextConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

/**
 * 描述:
 *  测试自定义序列化
 * @see Code2TextSerializer
 * @author Vic.xu
 * @since 2026-01-12 10:42
 */
@Import({
        TestCode2TextConfig.class
})
public class Code2TextSerializerTest extends BaseTest {

    @Test
    @DisplayName("测试自定义序列化")
    public void testCode2TextSerializer() {
        Code2TextDemoModel model = new Code2TextDemoModel();
        model.setDemoCode("code1");
        model.setDemoCode2("code1");
        model.setDemoCode3("code3");
        JsonUtil.printJson(model);
    }

    @Test
    @DisplayName("测试缓存")
    public void testCode2TextCache() {
        Code2TextCacheHelper.evict(DemoCode2TextResolver.class, "code1");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(true);
    }

    /**
     * @see DemoCode2TextResolver#preload()
     */
    @Test
    @DisplayName("测试静态转换code(内部其实使用的是resolve的缓存功能)")
    public void testCode2TextHelper() {
        String code = "code1";
        String text = Code2TextHelper.getText(DemoCode2Text.class, code);
        System.out.println(code + " -> " + text);
        Assertions.assertEquals("code1-preload", text);
    }

    @Test
    @DisplayName("测试静态转换text(内部其实使用的是resolve的缓存功能)")
    public void testText2CodeHelper() {
        String text = "code2-preload";
        Object code = Code2TextHelper.getCode(DemoCode2Text.class, text);
        System.out.println(text + " -> " + code);
        Assertions.assertEquals("code2", code);
    }


}
