package cn.xuqiudong.common.base.select;

/**
 * 描述:
 *  开启业务下拉框功能
 * @author Vic.xu
 * @since 2026-01-08 10:31
 */

import cn.xuqiudong.common.base.select.controller.BusinessSelectController;
import cn.xuqiudong.common.base.select.service.BusinessSelectFacade;
import cn.xuqiudong.common.base.select.service.BusinessSelectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "lcxm.framework.business.select", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BusinessSelectAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessSelectAutoConfiguration.class);

    public BusinessSelectAutoConfiguration() {
        LOGGER.info("启用业务下拉框模块");
    }

    @Bean
    @ConditionalOnMissingBean
    public BusinessSelectFacade businessSelectFacade(ObjectProvider<BusinessSelectProvider>  provider) {
        return new BusinessSelectFacade(provider);
    }

    @Bean
    @ConditionalOnMissingBean
    public BusinessSelectController businessSelectController(BusinessSelectFacade businessSelectFacade) {
        return new BusinessSelectController(businessSelectFacade);
    }
}
