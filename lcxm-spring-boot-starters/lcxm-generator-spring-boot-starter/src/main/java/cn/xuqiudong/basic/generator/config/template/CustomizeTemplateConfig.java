package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.config.IConfigBuilder;
import cn.xuqiudong.basic.generator.constant.GeneratorConstant;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.function.Function;

/**
 * 描述:
 * 自定义模板配置:
 * <p>
 * 自定义模板配置: 配置 自定义模板路径, 子包, 文件后缀，输出文件名
 * 上下文参见TemplateContext, 自己写定义好魔板文件即可
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
     * @param subPackage 模板所在包
     * @param templatePath  模板文件路径  如: /templates/excel 对应   /templates/excel.ftl
     * @return
     */
    public static CustomizeTemplateConfig build(String subPackage, String templatePath) {
        return new CustomizeTemplateConfig(subPackage, templatePath);
    }

    private CustomizeTemplateConfig(String subPackage, String templatePath) {
        this.subPackage = subPackage;
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

    public void afterPropertySet() {
        Assert.hasText(templatePath, "自定义模板路径不能为空");
        Assert.hasText(subPackage, "自定义模板子包不能为空");
        Assert.hasText(fileSuffix, "自定义模板文件后缀不能为空");
        Assert.notNull(fileNameFunction, "自定义模板文件名函数不能为空");
    }




}
