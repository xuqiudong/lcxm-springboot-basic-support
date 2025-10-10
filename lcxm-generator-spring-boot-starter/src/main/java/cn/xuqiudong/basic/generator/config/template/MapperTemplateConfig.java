package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.constant.GeneratorConstant;
import lombok.Getter;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-09-17 14:02
 */
@Getter
public class MapperTemplateConfig extends BaseTemplateConfig {


    /**
     * 构造函数: 构件默认的 子包和模板路径
     */
    public MapperTemplateConfig() {
        super(GeneratorConstant.PACKAGE_MAPPER, GeneratorConstant.TEMPLATE_MAPPER);
    }

    @Override
    public String getFileSuffix() {
        return GeneratorConstant.JAVA_SUFFIX;
    }

    @Override
    protected void afterPropertySet() {

    }

    /**
     * Mapper 模板配置 构建器
     */
    public static class Builder extends BaseConfigBuilder<MapperTemplateConfig.Builder, MapperTemplateConfig> {

        public Builder() {
            super(new MapperTemplateConfig());
        }
    }
}
