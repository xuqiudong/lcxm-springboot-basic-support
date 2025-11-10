package cn.xuqiudong.basic.generator;

import cn.xuqiudong.basic.generator.config.DataSourceConfig;
import cn.xuqiudong.basic.generator.config.GlobalConfig;
import cn.xuqiudong.basic.generator.config.StrategyConfig;
import cn.xuqiudong.basic.generator.engine.BaseTemplateEngine;
import cn.xuqiudong.basic.generator.engine.FreemarkerTemplateEngine;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.basic.generator.factory.GeneratorFactory;
import cn.xuqiudong.basic.generator.plugin.IGeneratorPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 描述:
 * 代码生成入口:  收集全部的配置,然后调用生成工厂进行生成
 *
 * @author Vic.xu
 * @since 2025-09-12 16:54
 */
public class Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);

    /**
     * 数据源配置
     */
    private final DataSourceConfig.Builder dataSourceConfigBuilder;

    /**
     * 全局配置
     */
    private final GlobalConfig.Builder globalConfigBuilder;

    /**
     * 生成细节配置
     */
    private final StrategyConfig.Builder strategyConfigBuilder;

    /**
     * 模板引擎: 默认使用Freemarker模板引擎
     */
    private BaseTemplateEngine templateEngine;

    /**
     * 自定义插件
     */
    private final List<IGeneratorPlugin> customizedPlugins;

    // 私有构造函数 初始化一些默认配置
    private Generator(DataSourceConfig.Builder dataSourceConfigBuilder) {
        this.dataSourceConfigBuilder = dataSourceConfigBuilder;
        this.globalConfigBuilder = new GlobalConfig.Builder();
        this.strategyConfigBuilder = new StrategyConfig.Builder();
        // 默认使用Freemarker模板引擎
        this.templateEngine = new FreemarkerTemplateEngine();
        this.customizedPlugins = new ArrayList<>();
    }

    /**
     * 1. 创建生成器: 指定数据库连接信息
     */
    public static Generator create(DatabaseType databaseType, String url, String username, String password) {
        return new Generator(new DataSourceConfig.Builder(databaseType, url, username, password));
    }

    /**
     * 1. 创建生成器: 指定数据库连接信息
     */
    public static Generator create(DataSourceConfig.Builder dataSourceConfigBuilder) {
        return new Generator(dataSourceConfigBuilder);
    }

    /**
     * 2. 配置全局参数
     */
    public Generator globalConfigBuilder(Consumer<GlobalConfig.Builder> consumer) {
        consumer.accept(this.globalConfigBuilder);
        return this;
    }

    /**
     * 2. 配置生成细节
     */
    public Generator strategyConfigBuilder(Consumer<StrategyConfig.Builder> consumer) {
        consumer.accept(this.strategyConfigBuilder);
        return this;
    }

    /**
     * 4. 配置模板   除非使用新的模板引擎,否则不用配置
     */
    public Generator templateEngine(BaseTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }

    /**
     * 5. 添加自定义插件, 在生成之前执行 处理模板引擎的上下文数据模型
     */
    public Generator addPlugin(IGeneratorPlugin customizedPlugin) {
        this.customizedPlugins.add(customizedPlugin);
        return this;
    }


    /**
     * 6. 开始生成
     */
    public void generate() {
        new GeneratorFactory(dataSourceConfigBuilder.build(),
                globalConfigBuilder.build(),
                strategyConfigBuilder.build(),
                templateEngine,
                customizedPlugins)
                .generate();

        LOGGER.info("generate success!");
    }




}
