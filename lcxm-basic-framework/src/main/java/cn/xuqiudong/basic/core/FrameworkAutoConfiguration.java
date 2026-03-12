package cn.xuqiudong.basic.core;

import cn.xuqiudong.basic.core.code2text.Code2TextAutoConfiguration;
import cn.xuqiudong.basic.core.jackson.LcxmJacksonAutoConfiguration;
import cn.xuqiudong.basic.core.select.BusinessSelectAutoConfiguration;
import cn.xuqiudong.basic.core.select.EnumSelectAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
}
