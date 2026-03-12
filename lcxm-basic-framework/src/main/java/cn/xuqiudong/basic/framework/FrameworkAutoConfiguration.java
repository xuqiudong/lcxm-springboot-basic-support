package cn.xuqiudong.basic.framework;

import cn.xuqiudong.basic.framework.code2text.Code2TextAutoConfiguration;
import cn.xuqiudong.basic.framework.jackson.LcxmJacksonAutoConfiguration;
import cn.xuqiudong.basic.framework.select.BusinessSelectAutoConfiguration;
import cn.xuqiudong.basic.framework.select.EnumSelectAutoConfiguration;
import cn.xuqiudong.basic.framework.tool.ApplicationContextHolder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述:
 * 框架模块的自动装配类
 *
 * @author Vic.xu
 * @since 2025-11-13 17:53
 */
@Configuration
@ConditionalOnProperty(prefix = "lcxm.framework", name = "enabled", havingValue = "true", matchIfMissing = true)
@ImportAutoConfiguration({
        EnumSelectAutoConfiguration.class,
        BusinessSelectAutoConfiguration.class,
        LcxmJacksonAutoConfiguration.class,
        Code2TextAutoConfiguration.class
}
)
public class FrameworkAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

}
