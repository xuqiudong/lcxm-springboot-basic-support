package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.constant.GeneratorConstant;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.function.Function;

/**
 * 描述:
 * 自定义模板配置:
 * <p>
 * 自定义模板配置: 配置 自定义模板路径, 子包, 文件后缀，输出文件名
 * 上下文参见TemplateContext, 自己写定义好魔板文件即可
 * 非java的时候 将输出到自定义输出目录(如果非空)
 * </p>
 *
 * @author Vic.xu
 * @see cn.xuqiudong.basic.generator.enums.TemplateType#CUSTOMIZE
 * @see cn.xuqiudong.basic.generator.model.context.TemplateContext
 * @since 2025-11-12 9:36
 */
@Getter
public class CustomizeTemplateConfig {

    /**
     * 所在包:
     */
    protected String subPackage;


    /**
     * 非java文件时的输出路径: 参数为实体 class的name
     */
    protected Function<String, String> subPathFunction;


    /**
     * 模板路径  不带后缀的 如: /templates/entity.java , 后缀由具体的模板引擎提供
     */
    protected String templatePath;


    /**
     * 是否禁用: 禁用后, 该模板将不会被渲染
     */
    protected boolean disable;

    /**
     * 文件后缀 默认是 .java
     */
    private String fileSuffix = GeneratorConstant.JAVA_SUFFIX;

    /**
     * 文件名函数: 参数为实体 class的name
     */
    private Function<String, String> fileNameFunction;


    /**
     * 构建 自定义模板配置
     *
     * @param subPackage   模板输出到哪个子包
     * @param templatePath 模板文件路径  如: /templates/excel 对应   /templates/excel.ftl
     * @return
     */
    public static CustomizeTemplateConfig build(String subPackage, String templatePath) {
        return new CustomizeTemplateConfig(subPackage, templatePath);
    }

    /**
     * 构建非java文件的模板配置
     *
     * @param subPathFunction 模板输出到哪个子文件夹  className -> className + /vue/index.vue
     * @param templatePath    模板文件路径  如: /templates/excel 对应   /templates/excel.ftl
     */
    public static CustomizeTemplateConfig build(Function<String, String> subPathFunction, String templatePath) {
        return new CustomizeTemplateConfig(subPathFunction, templatePath);
    }

    private CustomizeTemplateConfig(String subPackage, String templatePath) {
        this.subPackage = subPackage;
        this.templatePath = templatePath;
    }


    private CustomizeTemplateConfig(Function<String, String> subPathFunction, String templatePath) {
        this.subPathFunction = subPathFunction;
        this.templatePath = templatePath;
    }

    /**
     * 禁用: 禁用后, 该模板将不会被渲染
     */
    public CustomizeTemplateConfig setDisable(boolean disable) {
        this.disable = disable;
        return this;
    }

    /**
     * 设置文件后缀(): 默认为.java
     */
    public CustomizeTemplateConfig setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
        return this;
    }

    /**
     * 生成的 文件名函数 参数为当前entity 的class name： 比如  User
     */
    public CustomizeTemplateConfig setFileNameFunction(Function<String, String> fileNameFunction) {
        this.fileNameFunction = fileNameFunction;
        return this;
    }

    /**
     * 是否是java文件
     */
    public boolean isJavaFile() {
        return StringUtils.equalsIgnoreCase(fileSuffix, GeneratorConstant.JAVA_SUFFIX);
    }

    /**
     * 获取子路径
     * 1. java文件: 获取子包
     * 2. 非java文件: 获取子路径函数
     */
    public String getSubPath(String className) {
        if (isJavaFile()) {
            return subPackage;
        }
        if (subPathFunction != null) {
            return subPathFunction.apply(className);
        }
        return subPackage;
    }

    /**
     * 获取子包 或者子路径
     */
    public String getSubPackage(String className) {
        if (isJavaFile()) {
            return subPackage;
        }
        if (subPathFunction != null) {
            String subPath = subPathFunction.apply(className);
            // 把 subPath 中的 /  \ 等换为 .  因为  NameConvertUtils.getFullOutputFilePath 中会最终处理
            subPath = subPath.replaceAll("[\\\\/]", ".");
            return subPath;
        }
        return subPackage;
    }

    public void afterPropertySet() {
        Assert.hasText(templatePath, "自定义模板路径不能为空");
        Assert.hasText(subPackage, "自定义模板子包不能为空");
        Assert.hasText(fileSuffix, "自定义模板文件后缀不能为空");
        Assert.notNull(fileNameFunction, "自定义模板文件名函数不能为空");
    }


}
