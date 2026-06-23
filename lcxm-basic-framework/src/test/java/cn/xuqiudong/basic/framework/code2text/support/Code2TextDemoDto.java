package cn.xuqiudong.basic.framework.code2text.support;

import cn.xuqiudong.basic.framework.code2text.enrich.Code2TextEnrich;
import lombok.Data;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-06-23 9:26
 */
@Data
public class Code2TextDemoDto {

    private String demoCode;


    private String demoCode2;

    private String demoCode3;

    @Code2TextEnrich(type = DemoCode2Text.class, from = "demoCode")
    private String demoCodeText;

    @Code2TextEnrich(type = DemoCode2Text.class, from = "demoCode2")
    private String demoCode2Text;


    @Code2TextEnrich(type = DemoCode2Text.class, from = "demoCode3")
    private String code3Text;

}
