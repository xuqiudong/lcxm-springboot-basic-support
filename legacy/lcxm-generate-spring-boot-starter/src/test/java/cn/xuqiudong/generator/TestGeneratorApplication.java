package cn.xuqiudong.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 说明 :  测试自动生成代码的starter
 * 在test中启动项目 做测试，单元测试的时候 ：@SpringBootTest(classes = TestGeneratorApplication.class)
 *  *   默认情况下，Spring Boot 会根据 AutoConfiguration.imports 加载指定的自动配置类
 * @author VIC.xu
 * @since  2020年3月10日 上午10:50:28
 */
@SpringBootApplication
public class TestGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestGeneratorApplication.class, args);
    }

    // 支持跨越 方便测试
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // CORS 配置对所有接口都有效
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
