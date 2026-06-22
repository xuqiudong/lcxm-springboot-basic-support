package cn.xuqiudong.basic.framework.code2text.support;

import cn.xuqiudong.basic.framework.code2text.annotation.Code2Text;
import lombok.Data;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2026-01-12 10:48
 */
@Data
public class Code2TextDemoModel {

    @Code2Text(type = DemoCode2Text.class)
    private String demoCode;

    @Code2Text(type = DemoCode2Text.class)
    private String demoCode2;

    @Code2Text(textKey = "code3Text", type = DemoCode2Text.class)
    private String demoCode3;

}
