package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.DataType;
import cn.xuqiudong.basic.generator.model.FieldInfo;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import lombok.Getter;

import java.util.List;

/**
 * 描述:
 * 分页列表查询参数VO生成 上下文
 * <p>
 * 1. 继承 PageQuery
 * 2. 字段上加上 @QueryCondition 和 @Schema 注解
 * 3. 每个 QueryCondition 需要判断 QueryOperation
 * 4. 去掉主键 和 Entity 父类的字段
 * </p>
 *
 * @author Vic.xu
 * @see cn.xuqiudong.common.query.PageQuery
 * @since 2025-11-01 14:35
 */
@Getter
public class QueryContext extends BaseContext {

    /**
     * 实体字段信息: 将去除父类存在的字段 以及主键
     */
    private List<FieldInfo> fields;

    /**
     * 基类构造函数: 构造一些统统字段
     * 1. packageName 包全路径
     * 2. className 类名
     * 3. className4Field 类名首字母小写
     * 4. 处理泛型
     *
     * @param tableInfo 表信息
     * @param bundle    配置信息
     */
    public QueryContext(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getQueryTemplateConfig(), templateContext);
        // 以下代码同entity

        // 把未忽略的字段 和非主键加到 fields 中
        this.fields = tableInfo.getXmlFields().stream().filter(field -> !field.isEntityIgnore() && !field.isPk())
                .map(FieldInfo::clone).toList();
        // 字段对应的数据类型是否需要导入
        fields.forEach(field -> {
            DataType dataType = field.getDataType();
            if (ImportPackageUtils.needImport(dataType.getJavaType())) {
                addImport(ImportPackageUtils.getImport(dataType.getJavaType()));
            }
        });
    }

    @Override
    public List<String> genericClassNameList(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        return List.of();
    }

    /**
     * 类名后缀
     */
    @Override
    public String classNameSuffix() {
        return "Query";
    }
}
