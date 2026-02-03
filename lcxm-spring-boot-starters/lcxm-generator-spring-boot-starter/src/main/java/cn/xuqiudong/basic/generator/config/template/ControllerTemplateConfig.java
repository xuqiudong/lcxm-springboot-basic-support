package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.constant.GeneratorConstant;

/**
 * 描述:
 * Controller 模板配置
 *
 * @author Vic.xu
 * @since 2025-09-18 17:35
 */
public class ControllerTemplateConfig extends BaseTemplateConfig {


    /**
     * 构造函数: 构件默认的 子包和模板路径
     */
    public ControllerTemplateConfig() {
        super(GeneratorConstant.PACKAGE_CONTROLLER, GeneratorConstant.TEMPLATE_CONTROLLER);
    }

    @Override
    public String getFileSuffix() {
        return GeneratorConstant.JAVA_SUFFIX;
    }

    @Override
    protected void afterPropertySet() {

    }

    /**
     * Controller 模板配置 构建器
     */
    public static class Builder extends BaseConfigBuilder<ControllerTemplateConfig.Builder, ControllerTemplateConfig> {

        public Builder() {
            super(new ControllerTemplateConfig());
        }
    }
}
