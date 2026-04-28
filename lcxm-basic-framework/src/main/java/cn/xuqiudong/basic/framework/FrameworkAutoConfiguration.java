package cn.xuqiudong.basic.framework;

import cn.xuqiudong.basic.framework.code2text.Code2TextAutoConfiguration;
import cn.xuqiudong.basic.framework.jackson.LcxmJacksonAutoConfiguration;
import cn.xuqiudong.basic.framework.select.BusinessSelectAutoConfiguration;
import cn.xuqiudong.basic.framework.select.EnumSelectAutoConfiguration;
import cn.xuqiudong.basic.framework.tool.ApplicationContextHolder;
import cn.xuqiudong.basic.framework.web.filter.RequestLoggerFilter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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


    /**
     * 创建一个ApplicationContextHolder，用于获取Spring上下文
     */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    /**
     * 请求日志过滤器
     */
    @Bean
    @ConditionalOnMissingBean(name = "requestLoggerFilter")
    public FilterRegistrationBean<RequestLoggerFilter> requestLoggerFilterRegistration() {
        FilterRegistrationBean<RequestLoggerFilter> registrationBean = new FilterRegistrationBean<>();
        RequestLoggerFilter requestLoggerFilter = new RequestLoggerFilter();
        registrationBean.setFilter(requestLoggerFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("requestLoggerFilter");
        registrationBean.setOrder(2);
        return registrationBean;
    }

}
