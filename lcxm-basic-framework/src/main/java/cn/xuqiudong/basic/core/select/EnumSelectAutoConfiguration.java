package cn.xuqiudong.basic.core.select;

import cn.xuqiudong.basic.core.select.controller.EnumSelectController;
import cn.xuqiudong.basic.core.select.json.EnumSelectableSerializer;
import cn.xuqiudong.basic.core.select.scan.EnumSelectScanner;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述:
 * 枚举转下拉框模块的自动配置
 * 枚举包配置: lcxm.framework.enum.scan-base-packages=cn.xuqiudong.test,cn.xuqiudong.demo
 *
 * <p>
 *     META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports下：
 *     cn.xuqiudong.common.FrameworkAutoConfiguration
 * </p>
 * @author Vic.xu
 * @since 2025-11-13 17:59
 */
@Configuration
@ConditionalOnProperty(prefix = "lcxm.framework.enum.select", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EnumSelectAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumSelectAutoConfiguration.class);


    public EnumSelectAutoConfiguration() {
        LOGGER.info("启用枚举转下拉框模块");
    }

    /**
     * 允许用户自定义Controller覆盖默认实现
     */
    @Bean
    @ConditionalOnMissingBean
    public EnumSelectController enumSelectController() {
        return new EnumSelectController();
    }

    /**
     * 扫描枚举 注册到 EnumSelectRegistry
     */
    @Bean
    @ConditionalOnMissingBean
    public EnumSelectScanner enumSelectScanner(
            ApplicationContext applicationContext,
            @Value("${lcxm.framework.enum.scan-base-packages:}") String scanPackage) {
        return new EnumSelectScanner(applicationContext, scanPackage);
    }

    /**
     *  Jackson序列化配置：全局注册枚举序列化器 EnumSelectableSerializer
     *  使得每个EnumSelectable字段转json的时候追加一个xxxText字段
     *  不用在每个EnumSelectable 字段上加@JsonSerialize(using = EnumSelectableSerializer.class)
     */
    @Bean("enumSelectJacksonCustomizer")
    @ConditionalOnMissingBean(name = "enumSelectJacksonCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer enumSelectJacksonCustomizer() {
        LOGGER.info("注册枚举转json序列化器");
        return builder -> {
            // 注册自定义序列化器
            SimpleModule enumModule = new SimpleModule();
            enumModule.addSerializer(EnumSelectable.class, new EnumSelectableSerializer());
            builder.modulesToInstall(enumModule);
        };
    }

}
