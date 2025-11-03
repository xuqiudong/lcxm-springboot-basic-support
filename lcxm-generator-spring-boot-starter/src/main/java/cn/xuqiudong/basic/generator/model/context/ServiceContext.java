package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述:
 * service 上下文模型
 *
 * @author Vic.xu
 * @since 2025-09-18 17:38
 */
public class ServiceContext extends BaseContext {

    /**
     * 构造函数
     *
     * @param tableInfo       表信息
     * @param bundle          配置信息
     * @param templateContext 此时已经组装好 entity 和 mapper
     */
    public ServiceContext(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getServiceTemplateConfig(), templateContext);
        // 固定加上 Service 注解
        addAnnotation("@Service");
        String serviceAnno = ImportPackageUtils.getImport(Service.class);
        addImport(serviceAnno);
        //  添加实体和 mapper 的包
        addImport(ImportPackageUtils.getImport(templateContext.getEntity().getFullName()));
        addImport(ImportPackageUtils.getImport(templateContext.getMapper().getFullName()));

    }

    @Override
    public List<String> genericClassNameList(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        return List.of();
    }

    @Override
    public String classNameSuffix() {
        return "Service";
    }
}
