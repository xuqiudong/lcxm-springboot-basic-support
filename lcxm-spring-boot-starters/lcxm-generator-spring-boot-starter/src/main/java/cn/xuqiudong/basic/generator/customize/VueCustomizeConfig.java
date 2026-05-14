package cn.xuqiudong.basic.generator.customize;

import cn.xuqiudong.basic.generator.config.template.CustomizeTemplateConfig;
import cn.xuqiudong.basic.generator.constant.GeneratorConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 * vue 的代码生成： 以customize 的方式追加进来
 * <p>
 *     vue/className/apis/type.ts
 *     vue/className/apis/index.ts
 *     vue/className/index.vue
 * </p>
 *
 * @author Vic.xu
 * @since 2026-05-13 16:40
 */
public class VueCustomizeConfig {


    // 1. 声明为 private，防止外部直接访问
    private static final List<CustomizeTemplateConfig> DEFAULT_VUE_TEMPLATES = Collections.unmodifiableList(init());

    private static List<CustomizeTemplateConfig> init() {
        List<CustomizeTemplateConfig> configs = new ArrayList<>();
        // 1. 生成 vue/className/apis/type.ts
        configs.add(buildVueConfig("apis", GeneratorConstant.TEMPLATE_TYPE_TS, GeneratorConstant.TS_SUFFIX, "type"));

        // 2. 生成 vue/className/apis/index.ts
        configs.add(buildVueConfig("apis", GeneratorConstant.TEMPLATE_INDEX_TS, GeneratorConstant.TS_SUFFIX, "index"));

        // 3. 生成 vue/className/index.vue
        configs.add(buildVueConfig("", GeneratorConstant.TEMPLATE_INDEX_VUE, GeneratorConstant.VUE_SUFFIX, "index"));
        return configs;
    }

    /**
     * 抽取重复的构建逻辑，只保留变化的参数
     */
    private static CustomizeTemplateConfig buildVueConfig(String subPath, String templateName, String fileSuffix, String fileNameSuffix) {
        return CustomizeTemplateConfig
                .build(name -> "vue/" + name + (subPath.isEmpty() ? "" : "/" + subPath), templateName)
                .setFileSuffix(fileSuffix)
                .setFileNameFunction(name -> fileNameSuffix);
    }

    public static List<CustomizeTemplateConfig> getDefaultVueTemplates() {
        return DEFAULT_VUE_TEMPLATES;
    }
}
