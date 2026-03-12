package cn.xuqiudong.basic.framework.code2text.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2026-01-15 9:38
 */
@TestConfiguration
public class TestCode2TextConfig {

    @Bean
    public DemoCode2TextResolver demoCode2TextResolver() {
        return new DemoCode2TextResolver();
    }
}
