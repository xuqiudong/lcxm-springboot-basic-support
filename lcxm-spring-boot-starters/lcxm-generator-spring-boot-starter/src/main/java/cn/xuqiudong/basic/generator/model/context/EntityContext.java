package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.DataType;
import cn.xuqiudong.basic.generator.model.FieldInfo;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Getter;

import java.util.List;

/**
 * 描述:
 * 实体的上下文信息
 *
 * @author Vic.xu
 * @since 2025-09-15 11:18
 */
@Getter
public class EntityContext extends BaseContext {


    /**
     * 主键类型
     */
    private IdType idType = IdType.ASSIGN_UUID;

    /**
     * 实体字段信息: 将去除父类存在的字段
     */
    List<FieldInfo> fields;

    /**
     * 主键字段类型
     */
    private Class<?> pkType;

    /**
     * 主键字段名称
     */
    private String pkTypeName;


    /**
     * 实体类信息  上下文
     */
    public EntityContext(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getEntityTemplateConfig(), templateContext);
        // 主键类型 暂只支持 ASSIGN_UUID 和  AUTO(mysql  自增)
        if (tableInfo.getPk().getMeta().isAutoIncrement()) {
            this.idType = IdType.AUTO;
        }
        // 把未忽略的字段加到 fields 中
        this.fields = tableInfo.getXmlFields().stream().filter(field -> !field.isEntityIgnore())
                .map(FieldInfo::clone).toList();
        // 初始化主键字段类型
        initPrimaryType(tableInfo, bundle);
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
        initPrimaryType(tableInfo, bundle);
        return List.of(pkType.getSimpleName());
    }

    private void initPrimaryType(TableInfo tableInfo, ConfigBundle bundle) {
        if (this.pkType == null) {
            if (bundle.getGlobalConfig().getPkType() != null) {
                this.pkType = bundle.getGlobalConfig().getPkType();
            } else {
                this.pkType = tableInfo.getPk().getDataType().getJavaType();
            }
            this.pkTypeName = pkType.getSimpleName();
        }
    }

    // 实体类名 无需 后缀
    @Override
    public String classNameSuffix() {
        return "";
    }


}
