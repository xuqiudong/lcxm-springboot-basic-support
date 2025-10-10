package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import org.springframework.stereotype.Service;

/**
 * 描述:
 * service 上下文模型
 *
 * @author Vic.xu
 * @since 2025-09-18 17:38
 */
public class ServiceContext extends BaseContext {

    public ServiceContext(TableInfo tableInfo, ConfigBundle bundle) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getServiceTemplateConfig());
        // 固定加上 Service 注解
        addAnnotation("@Service");
        String mapperAnno = ImportPackageUtils.getImport(Service.class);
        addImport(mapperAnno);
    }

    @Override
    public String classNameSuffix() {
        return "Service";
    }
}
