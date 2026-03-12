package cn.xuqiudong.basic.framework.code2text.support;

import lombok.Data;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2026-01-12 10:48
 */
@Data
public class Code2TextDemoModel {

    @DemoCode2Text
    private String demoCode;

    @DemoCode2Text
    private String demoCode2;

    @DemoCode2Text(textKey = "code3Text")
    private String demoCode3;

}
