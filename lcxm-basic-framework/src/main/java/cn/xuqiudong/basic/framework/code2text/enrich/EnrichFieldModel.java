package cn.xuqiudong.basic.framework.code2text.enrich;

import cn.hutool.core.bean.PropDesc;
import cn.xuqiudong.basic.framework.code2text.type.Code2TextType;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-06-22 21:16
 */
@Data
public class EnrichFieldModel {

    /**
     * 字段上的注解
     */
    private Code2TextEnrich code2TextEnrich;

    /**
     * 待增强(填充)的字段描述
     */
    private PropDesc propDesc;

    /**
     *  from 字段的描述
     */
    private PropDesc fromPropDesc;

    /**
     * 解析器类型
     */
    private Class<? extends Code2TextType> type;

    /**
     * from 字段名
     */
    private String from;

    /**
     * 未找到文本时是否返回原值
     */
    private boolean fallbackToRaw;

    public EnrichFieldModel(Code2TextEnrich code2TextEnrich) {
        Assert.notNull(code2TextEnrich, "code2TextEnrich can not be null");
        this.code2TextEnrich = code2TextEnrich;
        this.type = code2TextEnrich.type();
        this.from = code2TextEnrich.from();
        this.fallbackToRaw = code2TextEnrich.fallbackToRaw();
    }

}
