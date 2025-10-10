package cn.xuqiudong.common.base.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * 描述:
 * 注入框架的一些基础配置：lcxm-common.properties; 优先级低；
 * 可被覆盖,仅提供一些默认配置
 *
 * @author Vic.xu
 * @since 2025-07-09 15:40
 */
public class LcxmCommonPropertiesPostProcessor implements EnvironmentPostProcessor, Ordered {

    public static final String LOCATION = "META-INF/lcxm-common.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new ClassPathResource(LOCATION);
        if (!resource.exists()) {
            return;
        }
        PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();
        try {
            PropertySource<?> propertySource = loader.load(LOCATION, resource).get(0);
            environment.getPropertySources().addLast(propertySource);
        } catch (IOException e) {

            throw new IllegalStateException("Failed to load lcxm-common.properties from " + resource, e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
