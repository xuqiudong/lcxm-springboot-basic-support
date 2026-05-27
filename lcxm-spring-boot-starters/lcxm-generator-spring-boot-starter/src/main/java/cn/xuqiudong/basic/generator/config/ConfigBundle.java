package cn.xuqiudong.basic.generator.config;

import cn.xuqiudong.basic.generator.config.template.CustomizeTemplateConfig;
import cn.xuqiudong.basic.generator.engine.BaseTemplateEngine;
import cn.xuqiudong.basic.generator.plugin.IGeneratorPlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * 描述:
 * 所有的配置 聚合到一起
 *
 * @author Vic.xu
 * @since 2025-09-12 17:59
 */
@Getter
@Setter
public class ConfigBundle {

    /**
     * 数据源配置
     */
    private DataSourceConfig dataSourceConfig;

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig;

    /**
     * 生成细节配置
     */
    private StrategyConfig strategyConfig;

    /**
     * 模板引擎
     */
    BaseTemplateEngine templateEngine;

    /**
     * 插件配置
     */
    Set<IGeneratorPlugin> plugins;

    /**
     * 自定义模板配置
     */
    List<CustomizeTemplateConfig> customizedTemplates;

    public ConfigBundle(DataSourceConfig dataSourceConfig, GlobalConfig globalConfig, StrategyConfig strategyConfig,
                        BaseTemplateEngine templateEngine, Set<IGeneratorPlugin> plugins,
                        List<CustomizeTemplateConfig> customizedTemplates
    ) {
        this.dataSourceConfig = dataSourceConfig;
        this.globalConfig = globalConfig;
        this.strategyConfig = strategyConfig;
        this.templateEngine = templateEngine;
        this.plugins = plugins;
        this.customizedTemplates = customizedTemplates;
    }


}
