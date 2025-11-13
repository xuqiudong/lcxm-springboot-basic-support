package cn.xuqiudong.common.base.select;

import cn.xuqiudong.common.base.select.controller.EnumSelectController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述:
 * 枚举转下拉框模块的自动配置
 *
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
}
