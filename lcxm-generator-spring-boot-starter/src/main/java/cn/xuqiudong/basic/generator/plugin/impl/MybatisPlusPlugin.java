package cn.xuqiudong.basic.generator.plugin.impl;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.FieldInfo;
import cn.xuqiudong.basic.generator.model.context.EntityContext;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.plugin.IGeneratorPlugin;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * 描述:
 * mybatis- plus 插件
 *
 * @author Vic.xu
 * @since 2025-09-15 11:33
 */
public class MybatisPlusPlugin implements IGeneratorPlugin {


    @Override
    public boolean enable(ConfigBundle configBundle) {
        return configBundle.getGlobalConfig().isPlus();
    }

    /**
     * entity  上的导入
     * 字段上的 注解
     *
     * @param templateContext
     */
    @Override
    public void beforeGenerate(TemplateContext templateContext) {
        EntityContext entity = templateContext.getEntity();
        //1 实体类上的 注解和导入  @TableName("table_name")
        entity.addImport(ImportPackageUtils.getImport(TableName.class));
        String tableAnnotation = "@TableName(\"" + templateContext.getTable().getTableName() + "\")";
        entity.addAnnotation(tableAnnotation);

        List<FieldInfo> fields = entity.getFields();
        if (fields == null) {
            return;
        }
        // 处理各个字段,  这个时候 已经去除了 忽略的字段
        for (FieldInfo field : fields) {
            String columnName = field.getColumnNameSafe();
            //  这里的 columnNameSafe 是已经格式化好的   可能带有双引号, 所以需要转义  ,不然拼在注解上可能会报错
            //  "type" -> \"type\"
            columnName = columnName.replace("\"", "\\\"");
            // 判断是不是主键 加上 @TableId 注解  @TableId(value = "id", type = IdType.ASSIGN_UUID)
            if (field.isPk()) {
                // 追加imports
                entity.addImport(ImportPackageUtils.getImport(TableId.class));
                entity.addImport(ImportPackageUtils.getImport(IdType.class));
                // 追加注解 字符串  形如 @TableId(value = "\"type\"", type = IdType.ASSIGN_UUID)
                IdType idType = entity.getIdType();
                String annotation = "@TableId(value = \"" + columnName + "\", type = IdType." + idType.name() + ")";
                field.addAnnotation(annotation);
            } else {
                //  非主键  加上 @TableField 注解  @TableField(value = "name", select = false)
                //  select = false , 当为lob类型的时候
                String select = "";
                if (field.isLob()) {
                    select = ", select = false";
                }
                String annotation = "@TableField(value = \"" + columnName + "\"" + select + ")";
                field.addAnnotation(annotation);

                // // 追加imports
                entity.addImport(ImportPackageUtils.getImport(TableField.class));
            }
        }
    }

    @Override
    public void afterGenerate(String content) {

    }


}
