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

    public MapperContext(TableInfo tableInfo, ConfigBundle bundle) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getMapperTemplateConfig());
        // 固定加上Mapper注解
        addAnnotation("@Mapper");
        String mapperAnno = ImportPackageUtils.getImport(Mapper.class);
        addImport(mapperAnno);
    }

    @Override
    public List<Class<?>> genericClassList(TableInfo tableInfo) {
        return List.of();
    }

    @Override
    public String classNameSuffix() {
        return "Mapper";
    }
}
