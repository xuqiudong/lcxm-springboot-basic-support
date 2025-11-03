package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述:
 * controller 上下文
 *
 * @author Vic.xu
 * @since 2025-09-18 17:38
 */
public class ControllerContext extends BaseContext {

    // controller 上的请求注解 形如: @RequestMapping("/module/tableName")
    private static final String REQUEST_MAPPING_FORMAT = "@RequestMapping(\"/%s/%s\")";

    public ControllerContext(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getControllerTemplateConfig(), templateContext);
        // 固定加上 @RestController 注解
        addAnnotation("@RestController");
        // 新增  @RequestMapping("/module/tableName") 注解
        String module = bundle.getGlobalConfig().getModule();
        String className = StringUtils.uncapitalize(tableInfo.getClassName());
        addAnnotation(String.format(REQUEST_MAPPING_FORMAT, module, className));
        // 添加类上的导入
        addImport(ImportPackageUtils.getImport(RestController.class));
        addImport(ImportPackageUtils.getImport(RequestMapping.class));

        //  添加实体和 service  和 query 的包
        addImport(ImportPackageUtils.getImport(templateContext.getEntity().getFullName()));
        addImport(ImportPackageUtils.getImport(templateContext.getService().getFullName()));
        addImport(ImportPackageUtils.getImport(templateContext.getQuery().getFullName()));

    }

    /**
     * 获取泛型: 默认支持<Service, Entity>
     */
    @Override
    public List<String> genericClassNameList(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        return List.of(templateContext.getService().getClassName());
    }

    @Override
    public String classNameSuffix() {
        return "Controller";
    }
}
