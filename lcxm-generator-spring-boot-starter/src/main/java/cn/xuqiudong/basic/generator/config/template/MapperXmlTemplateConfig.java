package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.constant.GeneratorConstant;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-09-18 11:50
 */
public class MapperXmlTemplateConfig extends BaseTemplateConfig {

    /**
     * 构造函数: 构件默认的 子包和模板路径
     */
    public MapperXmlTemplateConfig() {
        super(GeneratorConstant.PACKAGE_MAPPER, GeneratorConstant.TEMPLATE_XML);
    }

    @Override
    public String getFileSuffix() {
        return GeneratorConstant.XML_SUFFIX;
    }

    @Override
    protected void afterPropertySet() {

    }

    /**
     * Mapper XML 模板配置 构建器
     */
    public static class Builder extends BaseTemplateConfig.BaseConfigBuilder<MapperXmlTemplateConfig.Builder, MapperXmlTemplateConfig> {

        public Builder() {
            super(new MapperXmlTemplateConfig());
        }
    }
}
