package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.constant.GeneratorConstant;

/**
 * 描述:
 *  Service 模板配置
 * @author Vic.xu
 * @since 2025-09-18 17:35
 */
public class ServiceTemplateConfig extends BaseTemplateConfig {


    /**
     * 构造函数: 构件默认的 子包和模板路径
     */
    public ServiceTemplateConfig() {
        super(GeneratorConstant.PACKAGE_SERVICE, GeneratorConstant.TEMPLATE_SERVICE);
    }

    @Override
    public String getFileSuffix() {
        return GeneratorConstant.JAVA_SUFFIX;
    }

    @Override
    protected void afterPropertySet() {

    }

    /**
     * Service 模板配置 构建器
     */
    public static class Builder extends BaseConfigBuilder<ServiceTemplateConfig.Builder, ServiceTemplateConfig> {

        public Builder() {
            super(new ServiceTemplateConfig());
        }
    }
}
