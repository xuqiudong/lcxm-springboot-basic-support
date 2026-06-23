package cn.xuqiudong.basic.framework.code2text.support;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.xuqiudong.basic.framework.code2text.enrich.Code2TextEnrich;
import cn.xuqiudong.basic.framework.code2text.enrich.EnrichFieldMetaBuilder;
import cn.xuqiudong.basic.framework.code2text.enrich.EnrichFieldModel;

import java.util.List;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-06-23 11:37
 */
public class Code2TextDemoChildDto extends Code2TextDemoDto {

    private String childDemoCode;

    @Code2TextEnrich(type = DemoCode2Text.class, from = "childDemoCode")
    private String childDemoCode2Text;


    public static void main(String[] args) {
        List<EnrichFieldModel> enricherFieldModels = EnrichFieldMetaBuilder.getEnricherFieldModels(Code2TextDemoChildDto.class);
        for (EnrichFieldModel model : enricherFieldModels) {
            PropDesc propDesc = model.getPropDesc();
            String name = propDesc.getField().getName();
            System.out.println(name);
            PropDesc fromPropDesc = model.getFromPropDesc();
            String fromName = fromPropDesc.getField().getName();
            System.out.println(fromName);
        }

        System.out.println("-----------------");
        BeanDesc beanDesc = BeanUtil.getBeanDesc(Code2TextDemoChildDto.class);
        for (PropDesc propDesc : beanDesc.getProps()) {
            String name = propDesc.getField().getName();
            // 是否包含 Code2TextEnrich 注解
            boolean isEnrich = propDesc.getField()
                    .isAnnotationPresent(Code2TextEnrich.class);
            String fromName = null;
            if (isEnrich) {
                Code2TextEnrich enrich = propDesc.getField().getAnnotation(Code2TextEnrich.class);
                fromName = enrich.from();
            }
            // 一行 打印field name 是否包含注解  包含的话打印注解 的 from 属性值
            System.out.println(name + " " + isEnrich + " " + fromName);


        }

    }
}
