package cn.xuqiudong.basic.framework.code2text;

import cn.xuqiudong.basic.core.util.JsonUtil;
import cn.xuqiudong.basic.framework.BaseTest;
import cn.xuqiudong.basic.framework.code2text.enrich.Code2TextEnrichHelper;
import cn.xuqiudong.basic.framework.code2text.support.Code2TextDemoDto;
import cn.xuqiudong.basic.framework.code2text.support.Code2TextDemoModel;
import cn.xuqiudong.basic.framework.code2text.support.TestCode2TextConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-06-23 9:25
 */
@Import({
        TestCode2TextConfig.class
})
public class Code2TextEnrichTest extends BaseTest {


    @Test
    @DisplayName("测试单个dto enricher")
    public void enrichObject(){
        Code2TextDemoModel model = new Code2TextDemoModel();
        model.setDemoCode("code1");
        model.setDemoCode2("code2");
        model.setDemoCode3("code3");
        Code2TextDemoDto dto = Code2TextEnrichHelper.enrichObject(model, Code2TextDemoDto.class);
        JsonUtil.printJson(dto);
    }

    @Test
    @DisplayName("测试多个 dto enricher")
    public void enrichList(){
        List<Code2TextDemoModel> list = new ArrayList<>();
        list.add(new Code2TextDemoModel("c1", "c2", "c3"));
        list.add(new Code2TextDemoModel("a1", "a2", "a3"));
        list.add(new Code2TextDemoModel("b1", "b2", "b3"));
        List<Code2TextDemoDto> result = Code2TextEnrichHelper.enrichList(list, Code2TextDemoDto.class);
        JsonUtil.printJson(result);

    }
}
