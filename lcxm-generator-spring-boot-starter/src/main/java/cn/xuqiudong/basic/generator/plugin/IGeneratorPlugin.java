package cn.xuqiudong.basic.generator.plugin;

import cn.xuqiudong.basic.generator.factory.GeneratorFactory;
import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;

/**
 * 描述:
 * 代码生成的插件: 给生成的代码加一些扩展
 * - 如: SwaggerPlugin：扫描字段注释，加上 @Schema
 * - 如:LombokPlugin：类上自动加 @Data、@Builder
 * @see GeneratorFactory#beforeGenerate(TemplateContext)
 * @author Vic.xu
 * @since 2025-09-11 10:04
 */
public interface IGeneratorPlugin {


    /**
     * 插件是否启用
     */
    boolean enable(ConfigBundle config);

    /**
     * 在生成之前, 一般处理一些元数据, 比如追加一些注解
     */
    void beforeGenerate(TemplateContext templateContext);

    /**
     * 在生成内容之后: 直接处理内容 (一般很少使用)
     */
    void afterGenerate(String content);

}
