package cn.xuqiudong.basic.generator.customize;

import cn.xuqiudong.basic.generator.config.template.CustomizeTemplateConfig;
import cn.xuqiudong.basic.generator.constant.GeneratorConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * vue 的代码生成： 以customize 的方式追加进来
 * 不额外配置 输出文件夹，直接追加的vue子包中， 手动复制出去
 * @author Vic.xu
 * @since 2026-05-13 16:40
 */
public class VueCustomizeConfig {


    public static List<CustomizeTemplateConfig> DEFAULT_VUE_TEMPLATES =  new ArrayList<>();

    static {

        /**
         * 自定义模板 vue/apis/type.ts
         */
        DEFAULT_VUE_TEMPLATES.add(templateTypeTs());

        /**
         * 自定义模板 vue/apis/index.ts
         */
        DEFAULT_VUE_TEMPLATES.add(templateIndexTs());

        /**
         * 自定义模板 vue/index.vue
         */
        DEFAULT_VUE_TEMPLATES.add(templateIndexVue());
    }

    /**
     * 自定义模板 vue/apis/type.ts
     */
    public static CustomizeTemplateConfig templateTypeTs(){
        return CustomizeTemplateConfig
                .build("vue.apis", GeneratorConstant.TEMPLATE_TYPE_TS)
                .setFileSuffix(GeneratorConstant.TS_SUFFIX).setFileNameFunction(name -> "type.ts");
    }

    /**
     * 自定义模板 vue/apis/index.ts
     */
    public static CustomizeTemplateConfig templateIndexTs(){
        return CustomizeTemplateConfig
                .build("vue.apis", GeneratorConstant.TEMPLATE_INDEX_TS)
                .setFileSuffix(GeneratorConstant.TS_SUFFIX).setFileNameFunction(name -> "index.ts");
    }

    /**
     * 自定义模板 vue/index.vue
     */
     public static CustomizeTemplateConfig templateIndexVue(){
         return CustomizeTemplateConfig
                .build("vue", GeneratorConstant.TEMPLATE_INDEX_VUE)
                .setFileSuffix(GeneratorConstant.VUE_SUFFIX).setFileNameFunction(name -> "index.vue");
     }
}
