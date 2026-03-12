package cn.xuqiudong.basic.generator;

import cn.xuqiudong.basic.generator.engine.FreemarkerTemplateEngine;

/**
 * 描述:
 *   提供一个通用的代码生成器， 默认一些配置 ， 最终依然是调用 Generator#generate() 方法进行生成
 *
 *   使用方式：
 *   <pre>{@code
 *
 *       CommonFacadeConfig config = CommonFacadeConfig
 *                 .mysql("127.0.0.1", "3306", "qiudong", "qiudong", "qiudong12345678");
 *         config
 *                 .setBasePackage("cn.xuqiudong.generator");
 *         config.setModule("test");
 *         config.setMavenModule("/lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter");
 *         config.addTable("test_generate");
 *         config.addTablePrefix("t_");
 *         Generator generator = CommonGeneratorFacade.build(config);
 *         generator.generate();
 *         </code>
 *   }</pre>
 * @author Vic.xu
 * @since 2026-03-11 8:52
 */
public class CommonGeneratorFacade {


    /**
     * 构建代码生成器 Generator, 然后调用 Generator#generate()  生成代码即可
     * @see Generator#generate()
     *
     * @param config 生成器配置
     * @return Generator
     */
    public static Generator build(CommonFacadeConfig config) {
        Generator generator = Generator.create(config.getDatabaseType(), config.getUrl(), config.getUsername(), config.getPassword());
        // 全局配置
        generator.globalConfigBuilder(builder ->
                builder.author(config.getAuthor())
                        .outputDir(config.getOutputDir())
                        // 基础包路径
                        .basePackage(config.getBasePackage())
                        // 模块名称: 基础包路径的子包, 以及controller 请求路径
                        .module(config.getModule())
                        // 主键类型: 将覆盖数据库查询出的结果
                        .pkType(config.getPkType())
                        // 是否开启 lombok 注解 : 默认：true
                        .lombok(config.isLombok())
                        // 是否开启 plus 注解 : 默认：true
                        .plus(config.isPlus())
                        // 是否开启 springdoc 注解 : 默认：true
                        .springdoc(config.isSpringdoc())
                        // 是否打开输出目录 : 默认：true
                        .open(config.isOpen())
        );
        // 生成策略配置 核心配置 ★
        generator.strategyConfigBuilder(builder ->
                builder
                        .tables(config.getTables())
                        .tablePrefix(config.getTablePrefix())
                        .fileOverride(config.isFileOverride())
                        .entityConfig(config.getEntityConfig())
                        .mapperConfig(config.getMapperConfig())
                        .xmlConfig(config.getMapperXmlConfig())
                        .serviceConfig(config.getServiceConfig())
                        .controllerConfig(config.getControllerConfig())
        );
        // 自定义模板
        generator.addCustomizedTemplate(config.getCustomizeTemplateConfig());
        // 模板配置: 默认就是freemarker 模板引擎 可以不用配置
        generator.templateEngine(new FreemarkerTemplateEngine());
        return generator;
    }



}


