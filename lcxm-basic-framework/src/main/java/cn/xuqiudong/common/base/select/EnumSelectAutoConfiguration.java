package cn.xuqiudong.common.base.select;

import cn.xuqiudong.common.base.select.controller.EnumSelectController;
import cn.xuqiudong.common.base.select.scan.EnumSelectScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述:
 * 枚举转下拉框模块的自动配置
 * 枚举包配置: lcxm.framework.enum.scan-base-packages=cn.xuqiudong.test,cn.xuqiudong.demo
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
}
