package cn.xuqiudong.basic.generator.plugin.impl;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.context.BaseContext;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.plugin.BaseGeneratorPlugin;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述:
 * lombok插件
 * <p>
 * 1. entity
 * 2. query
 * </p>
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
     * 加上  为 entity 和query  加上  @Data 和 @EqualsAndHashCode 注解 和导入
     */
    @Override
    public void beforeGenerate(TemplateContext context) {
        // 1  entity
        addAnnotationAndImport(context.getEntity());
        // 2 query
        addAnnotationAndImport(context.getQuery());
    }

    private void addAnnotationAndImport(BaseContext context) {
        // 加注解
        context.addAnnotation("@Data");
        context.addImport(ImportPackageUtils.getImport(Data.class));
        // 有父类的话 则加上 @EqualsAndHashCode(callSuper = true)
        if (context.isHasSuperClass()) {
            context.addAnnotation("@EqualsAndHashCode(callSuper = true)");
            context.addImport(ImportPackageUtils.getImport(EqualsAndHashCode.class));

        }
    }

    @Override
    public void afterGenerate(String content) {

    }
}
