package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 描述:
 * mapper接口和 xml 的上下文 数据模型
 *
 * @author Vic.xu
 * @since 2025-09-17 13:57
 */
public class MapperContext extends BaseContext {

    public MapperContext(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getMapperTemplateConfig(), templateContext);
        // 固定加上Mapper注解
        addAnnotation("@Mapper");
        String mapperAnno = ImportPackageUtils.getImport(Mapper.class);
        addImport(mapperAnno);

        // 导入实体类
        addImport(ImportPackageUtils.getImport(templateContext.getEntity().getFullName()));
    }

    /**
     * 使用Entity 作为泛型
     */
    @Override
    public List<String> genericClassNameList(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        return List.of(templateContext.getEntity().getClassName());
    }

    @Override
    public String classNameSuffix() {
        return "Mapper";
    }
}
