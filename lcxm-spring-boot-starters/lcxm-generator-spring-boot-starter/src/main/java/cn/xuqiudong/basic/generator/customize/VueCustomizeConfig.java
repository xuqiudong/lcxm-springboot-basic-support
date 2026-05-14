package cn.xuqiudong.basic.generator.customize;

import cn.xuqiudong.basic.generator.config.template.CustomizeTemplateConfig;
import cn.xuqiudong.basic.generator.constant.GeneratorConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 * vue 的代码生成： 以customize 的方式追加进来
 * 不额外配置 输出文件夹，直接追加的vue子包中， 手动复制出去
 * @author Vic.xu
 * @since 2026-05-13 16:40
 */
public class VueCustomizeConfig {


    // 1. 声明为 private，防止外部直接访问
    private static final List<CustomizeTemplateConfig> DEFAULT_VUE_TEMPLATES;

    static {
        List<CustomizeTemplateConfig> temp = new ArrayList<>();
        /**
         * 自定义模板 vue/apis/type.ts
         */
        temp.add(templateTypeTs());

        /**
         * 自定义模板 vue/apis/index.ts
         */
        temp.add(templateIndexTs());

        // 2. 将填充好数据的可变集合包装成不可变集合，再赋值给 final 字段
        DEFAULT_VUE_TEMPLATES = Collections.unmodifiableList(temp);
    }

    /**
     * 获取默认的模板列表
     */
    public static List<CustomizeTemplateConfig> getDefaultVueTemplates() {
        return DEFAULT_VUE_TEMPLATES;
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
