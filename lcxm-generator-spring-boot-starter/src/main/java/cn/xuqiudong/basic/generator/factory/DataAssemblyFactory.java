package cn.xuqiudong.basic.generator.factory;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.config.GlobalConfig;
import cn.xuqiudong.basic.generator.model.FieldInfo;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.model.context.ControllerContext;
import cn.xuqiudong.basic.generator.model.context.EntityContext;
import cn.xuqiudong.basic.generator.model.context.MapperContext;
import cn.xuqiudong.basic.generator.model.context.QueryContext;
import cn.xuqiudong.basic.generator.model.context.ServiceContext;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.model.meta.ColumnMeta;
import cn.xuqiudong.basic.generator.model.meta.TableMeta;
import cn.xuqiudong.basic.generator.util.NameConvertUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 描述:
 * 组装数据的工厂
 *
 * @author Vic.xu
 * @since 2025-09-13 9:58
 */
public class DataAssemblyFactory {

    ConfigBundle bundle;

    public DataAssemblyFactory(ConfigBundle bundle) {
        this.bundle = bundle;
    }


    /**
     * 获取所有表和和字段的信息,构建 TemplateContext 上下文信息
     */
    public List<TemplateContext> listTemplateContexts() {
        Set<String> tables = this.bundle.getStrategyConfig().getTables();
        Assert.notNull(tables, "请指定要生成的表");
        List<TemplateContext> result = new ArrayList<>();
        tables.forEach(tableName -> {
            TemplateContext context = buildTemplateContext(tableName);
            result.add(context);
        });
        return result;
    }

    /**
     * 构建模版数据
     * 注意需要按顺序构建， 后面的构建依赖前面的构建结果
     */
    public TemplateContext buildTemplateContext(String tableName) {
        GlobalConfig globalConfig = bundle.getGlobalConfig();
        TemplateContext templateContext = new TemplateContext(globalConfig);
        // 1. 构建 tableInfo  主要用于xml 和 entity
        TableInfo tableInfo = buildTableInfo(tableName);
        templateContext.setTable(tableInfo);
        // 2. 构建 EntityContext
        EntityContext entityContext = new EntityContext(tableInfo, bundle, templateContext);
        templateContext.setEntity(entityContext);

        // 2.1 构建QueryContext
        QueryContext queryContext = new QueryContext(tableInfo, bundle, templateContext);
        templateContext.setQuery(queryContext);

        // 3. 构建MapperContext
        MapperContext mapperContext = new MapperContext(tableInfo, bundle, templateContext);
        templateContext.setMapper(mapperContext);
        //4. 构建ServiceContext
        ServiceContext serviceContext = new ServiceContext(tableInfo, bundle, templateContext);
        templateContext.setService(serviceContext);
        //5. 构建ControllerContext
        ControllerContext controllerContext = new ControllerContext(tableInfo, bundle, templateContext);
        templateContext.setController(controllerContext);

        return templateContext;
    }

    /**
     * 构建TableInfo by tableName
     */
    private TableInfo buildTableInfo(String tableName) {
        // 表信息
        TableMeta tableMeta = bundle.getDataSourceConfig().getDao().queryTable(tableName);
        Assert.notNull(tableMeta, tableName + " 表不存在");
        TableInfo tableInfo = buildTableInfo(tableMeta);
        // 列信息
        List<ColumnMeta> columns = bundle.getDataSourceConfig().getDao().queryColumns(tableName);
        // 列信息组装
        for (ColumnMeta meta : columns) {
            buildTableField(tableInfo, meta);
        }
        //xml  字段排序: 主键字段优先 然后父类字段放在前面
        tableInfo.initResultMapFields();
        return tableInfo;
    }

    private TableInfo buildTableInfo(TableMeta tableMeta) {
        //  开始组装TableInfo
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableMeta(tableMeta);
        // 表转化为java的name: 比如 sys_user -> User
        String className = NameConvertUtils.tableToJava(tableInfo.getTableName(), bundle.getStrategyConfig().getTablePrefix());
        tableInfo.setClassName(className);
        return tableInfo;
    }

    /**
     * 组装TableField
     */
    public FieldInfo buildTableField(TableInfo tableInfo, ColumnMeta meta) {
        FieldInfo field = new FieldInfo();
        field.setMeta(bundle.getDataSourceConfig().getDatabaseType(), meta);

        // 判断字段是否是主键
        boolean pk = meta.isPk();
        if (pk && tableInfo.getPk() == null) {
            tableInfo.setPk(field);
        }
        // 类字段是否被忽略
        if (isIgnoredField(field.getFieldName())) {
            field.setEntityIgnore(true);
        }
        // 表字段是否被忽略 忽略就不会出现在 mapper.xml 和 entity 中
        if (!isIgnoredColumn(meta.getColumnName())) {
            tableInfo.addXmlField(field);
        }

        return field;
    }

    /**
     * 判断 属性字段是否被忽略:  在父类存在
     */
    private boolean isIgnoredField(String fieldName) {
        return bundle.getStrategyConfig().getEntityTemplateConfig().isFieldIgnored(fieldName);
    }

    /**
     * 判断 列是否被忽略:  生成时候指定忽略
     */
    private boolean isIgnoredColumn(String columnName) {
        return bundle.getStrategyConfig().getEntityTemplateConfig().isTableColumnIgnored(columnName);
    }

    /**
     * 构建模版数据
     *
     * @param tableInfo
     */
    public TemplateContext buildTemplateContext(TableInfo tableInfo) {
        GlobalConfig globalConfig = bundle.getGlobalConfig();
        TemplateContext templateContext = new TemplateContext(globalConfig);
        return templateContext;
    }
}
