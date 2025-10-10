package cn.xuqiudong.basic.generator.config;

import cn.xuqiudong.basic.generator.engine.BaseTemplateEngine;
import cn.xuqiudong.basic.generator.engine.FreemarkerTemplateEngine;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述:
 * 生成使用的模板引擎的配置
 *
 * @author Vic.xu
 * @since 2025-09-11 10:06
 */
@Getter
@Setter
public class EngineConfig {


    /**
     * 模板引擎 :  默认使用freemarker
     */
    private BaseTemplateEngine templateEngine;


    public static class Builder implements IConfigBuilder<EngineConfig> {

        final EngineConfig templateConfig;

        public Builder() {
            this.templateConfig = new EngineConfig();
            this.templateConfig.setTemplateEngine(new FreemarkerTemplateEngine());
        }

        /**
         * 模板引擎
         */
        public Builder templateEngine(BaseTemplateEngine templateEngine) {
            templateConfig.setTemplateEngine(templateEngine);
            return this;
        }


        @Override
        public EngineConfig build() {
            return templateConfig;
        }
    }
}
