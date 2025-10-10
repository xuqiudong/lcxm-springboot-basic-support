package cn.xuqiudong.basic.generator.plugin.impl;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.plugin.BaseGeneratorPlugin;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import lombok.Data;

/**
 * 描述:
 * lombok插件
 *
 * @author Vic.xu
 * @since 2025-09-11 10:40
 */
@Data
public class LombokPlugin extends BaseGeneratorPlugin {


    @Override
    public boolean enable(ConfigBundle configBundle) {
        return configBundle.getGlobalConfig().isLombok();
    }

    /**
     * 加上 @Data 注解 和导入
     */
    @Override
    public void beforeGenerate(TemplateContext context) {
        // 注解
        context.getEntity().addAnnotation("@Data");
        // 导入
        context.getEntity().addImport(ImportPackageUtils.getImport(Data.class));
    }

    @Override
    public void afterGenerate(String content) {

    }
}
