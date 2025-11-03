package cn.xuqiudong.basic.generator.plugin.impl;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.context.EntityContext;
import cn.xuqiudong.basic.generator.model.context.QueryContext;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.plugin.IGeneratorPlugin;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 描述:
 * springdoc 注解插件
 * <p>
 * 1. entity
 * 2. query
 * </p>
 *
 * @author Vic.xu
 * @since 2025-09-11 10:26
 */
public class SpringdocPlugin implements IGeneratorPlugin {
    @Override
    public boolean enable(ConfigBundle config) {
        return config.getGlobalConfig().isSpringdoc();
    }

    /**
     * 加上 @Schema 注解:  类上 和字段上
     */
    @Override
    public void beforeGenerate(TemplateContext templateContext) {
        // 1  处理entity
        EntityContext entity = templateContext.getEntity();
        // 类上加 @Schema(name="className", description = "表备注")
        entity.addAnnotation("@Schema(name=\"" + entity.getClassName() + "\", " +
                "description = \"" + templateContext.getTable().getComments() + "\")");
        // 加上导入
        entity.addImport(ImportPackageUtils.getImport(Schema.class));

        // 各个字段加上 @Schema(description = "字段备注")
        entity.getFields().forEach(field -> {
            field.addAnnotation("@Schema(description = \"" + field.getComments() + "\")");
        });
        // 2 处理 query
        QueryContext query = templateContext.getQuery();
        // 类上加 @Schema(name="className", description = "表备注")
        query.addAnnotation("@Schema(name=\"" + query.getClassName() + "\", " +

                "description = \"" + templateContext.getTable().getComments() + "\")");
        // 加上导入
        query.addImport(ImportPackageUtils.getImport(Schema.class));
        // 各个字段加上 @Schema(description = "字段备注")
        query.getFields().forEach(field -> {
            field.addAnnotation("@Schema(description = \"" + field.getComments() + "\")");
        });
    }


    @Override
    public void afterGenerate(String content) {
    }
}
