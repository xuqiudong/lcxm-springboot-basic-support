package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 * controller 上下文
 *
 * @author Vic.xu
 * @since 2025-09-18 17:38
 */
@Data
public class ControllerContext extends BaseContext {

    // controller 上的请求注解 形如: @RequestMapping("requestMapping")
    private static final String REQUEST_MAPPING_FORMAT = "@RequestMapping(\"%s\")";

    /**
     * 请求路径: 形如 /system/user
     * 1. 如果设置了 请求路径 则使用自定义的请求路径
     * 2. 如果没设置 默认使用 moduleName/tableName
     */
    private String requestMapping;


    public ControllerContext(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext) {
        super(tableInfo, bundle, bundle.getStrategyConfig().getControllerTemplateConfig(), templateContext);
        // 固定加上 @RestController 注解
        addAnnotation("@RestController");
        // 构建请求路径
        buildRequestMapping(tableInfo, bundle);
        // 新增  @RequestMapping("requestMapping") 注解
        addAnnotation(String.format(REQUEST_MAPPING_FORMAT, requestMapping));

        // 添加类上的导入
        addImport(ImportPackageUtils.getImport(RestController.class));
        addImport(ImportPackageUtils.getImport(RequestMapping.class));

        //  添加实体和 service  和 query 的包
        addImport(ImportPackageUtils.getImport(templateContext.getEntity().getFullName()));
        addImport(ImportPackageUtils.getImport(templateContext.getService().getFullName()));
        addImport(ImportPackageUtils.getImport(templateContext.getQuery().getFullName()));

    }

    /**
     * 构建请求路径
     */
    private void buildRequestMapping(TableInfo tableInfo, ConfigBundle bundle){
        Map<String, String> requestMappingMap = bundle.getStrategyConfig().getControllerTemplateConfig().getRequestMappingMap();

        String customizedRequestMapping = requestMappingMap.get(tableInfo.getTableName());
        if (StringUtils.isNotBlank(customizedRequestMapping)) {
            this.requestMapping = customizedRequestMapping;
        } else {
            String module = bundle.getGlobalConfig().getModule();
            String className = StringUtils.uncapitalize(tableInfo.getClassName());
            this.requestMapping = "/" + module + "/" + className;
        }
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
