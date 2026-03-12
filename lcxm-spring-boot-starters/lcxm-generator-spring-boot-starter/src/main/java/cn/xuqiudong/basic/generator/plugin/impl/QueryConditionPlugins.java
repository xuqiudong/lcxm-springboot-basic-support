package cn.xuqiudong.basic.generator.plugin.impl;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.context.QueryContext;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.plugin.BaseGeneratorPlugin;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import cn.xuqiudong.basic.mybatisplus.annotation.QueryCondition;
import cn.xuqiudong.basic.mybatisplus.enums.QueryOperation;
import cn.xuqiudong.basic.mybatisplus.util.QueryConditionUtils;
import lombok.EqualsAndHashCode;

/**
 * 描述:
 * 给查询条件加上@QueryCondition 注解
 *
 * @author Vic.xu
 * @see QueryCondition ;
 * @since 2025-11-01 14:57
 */
@EqualsAndHashCode
public class QueryConditionPlugins extends BaseGeneratorPlugin {
    @Override
    public boolean enable(ConfigBundle config) {
        // plus 才需要
        return config.getGlobalConfig().isPlus();
    }

    @Override
    public void beforeGenerate(TemplateContext templateContext) {
        QueryContext query = templateContext.getQuery();
        // 添加导入
        query.addImport(ImportPackageUtils.getImport(QueryCondition.class));
        query.addImport(ImportPackageUtils.getImport(QueryOperation.class));
        // 每个字段上加注解  @QueryCondition(operation = QueryOperation.LIKE)
        query.getFields().forEach(field -> {
            Class<?> javaType = field.getDataType().getJavaType();
            String columnName = field.getColumnName();
            QueryOperation operation = QueryConditionUtils.inferQueryOperation(javaType, columnName);
            field.addAnnotation("@QueryCondition(operation = QueryOperation." + operation + ")");
        });
    }

    @Override
    public void afterGenerate(String content) {

    }
}
