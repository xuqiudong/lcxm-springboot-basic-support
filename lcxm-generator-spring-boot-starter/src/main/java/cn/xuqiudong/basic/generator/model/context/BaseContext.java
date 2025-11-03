package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.config.template.BaseTemplateConfig;
import cn.xuqiudong.basic.generator.model.TableInfo;
import cn.xuqiudong.basic.generator.util.GenericStringUtils;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 描述:
 * 各个类型模板的模板上下文数据模型 基类
 *
 * @author Vic.xu
 * @since 2025-09-15 15:03
 */
@Getter
@Setter
public abstract class BaseContext {

    /**
     * 包名 全路径
     */
    protected String packageName;

    /**
     * 类名
     */
    protected String className;

    /**
     * 类被注入时候的名称: 类名小写
     */
    @Getter(AccessLevel.NONE)
    protected String className4Field;

    /**
     * 父类 (带泛型)
     */
    protected String superClass;

    /**
     * 是否有父类
     */
    protected boolean hasSuperClass;

    /**
     * 导入的包  默认字段排序
     */
    protected Set<String> imports = new TreeSet<>();

    /**
     * 类上需要加的注解  默认字段排序
     */
    protected Set<String> annotations = new TreeSet<>();

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
    public BaseContext(TableInfo tableInfo, ConfigBundle bundle, BaseTemplateConfig templateConfig, TemplateContext templateContext) {
        this.className = tableInfo.getClassName() + classNameSuffix();
        this.className4Field = StringUtils.uncapitalize(className);
        // 包路径: BasePackage + 模块 + 类名
        this.packageName = bundle.getGlobalConfig().getBasePackage();
        String module = bundle.getGlobalConfig().getModule();
        if (StringUtils.isNotEmpty(module)) {
            this.packageName += "." + module;
        }
        String subPackage = templateConfig.getSubPackage();
        if (StringUtils.isNotEmpty(subPackage)) {
            this.packageName += "." + subPackage;
        }
        // 处理泛型
        handleGeneric(tableInfo, bundle, templateConfig, templateContext);
    }

    /**
     * 处理类上的泛型
     */
    public void handleGeneric(TableInfo tableInfo, ConfigBundle bundle, BaseTemplateConfig templateConfig, TemplateContext templateContext) {
        Class<?> supperClass = templateConfig.getSupperClass();
        if (supperClass != null) {
            this.hasSuperClass = true;
            this.superClass = supperClass.getSimpleName();
            // 泛型
            if (templateConfig.isSupperClassWithGeneric()) {
                List<String> genericClassNameList = genericClassNameList(tableInfo, bundle, templateContext);
                if (genericClassNameList != null) {
                    String genericString = GenericStringUtils.classNameToGenericString(genericClassNameList);
                    this.superClass += genericString;
                    this.imports.add(ImportPackageUtils.getImport(supperClass));
                }
            }
            // 导入 父 class 的包
            String anImport = ImportPackageUtils.getImport(supperClass);
            this.imports.add(anImport);
        }
    }

    /**
     * 获取泛型类 列表
     */
    public abstract List<String> genericClassNameList(TableInfo tableInfo, ConfigBundle bundle, TemplateContext templateContext);

    /**
     * 获取全类名: 包名 + . + 类名
     */
    public String getFullName() {
        return packageName + "." + className;
    }

    /**
     * 添加导入包
     */
    public void addImport(String imp) {
        imports.add(imp);
    }

    /**
     * 类上需要加的注解
     */
    public void addAnnotation(String annotation) {
        annotations.add(annotation);
    }

    public void addAnnotations(Set<String> annotations) {
        annotations.addAll(annotations);
    }


    /**
     * 类名后缀 比如 Service
     *
     * @return UserService = User + Service
     */
    public abstract String classNameSuffix();


}
