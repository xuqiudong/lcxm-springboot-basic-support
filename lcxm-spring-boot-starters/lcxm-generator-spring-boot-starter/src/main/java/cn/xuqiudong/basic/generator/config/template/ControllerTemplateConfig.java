package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.constant.GeneratorConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.map.HashedMap;

import java.util.Map;

/**
 * 描述:
 * Controller 模板配置
 *
 * @author Vic.xu
 * @since 2025-09-18 17:35
 */
@Getter
@Setter
public class ControllerTemplateConfig extends BaseTemplateConfig {

    /**
     * 请求路径 映射：  表名和请求路径
     * 不配置则是 /｛module｝/｛className｝
     */
    private Map<String, String> requestMappingMap = new HashedMap<>();


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

        /**
         * 添加 请求路径映射
         *
         * @param tableName      表名
         * @param requestMapping 请求路径
         */
        public Builder addRequestMapping(String tableName, String requestMapping) {
            this.config.requestMappingMap.put(tableName, requestMapping);
            return this;
        }

        /**
         * 添加 请求路径映射 Map
         */
        public Builder addRequestMapping(Map<String, String> requestMappingMap) {
            this.config.requestMappingMap.putAll(requestMappingMap);
            return this;
        }

        public Builder() {
            super(new ControllerTemplateConfig());
        }


    }
}
