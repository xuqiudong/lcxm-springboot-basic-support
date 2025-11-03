package cn.xuqiudong.generator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-11-01 11:31
 */
@OpenAPIDefinition(
        info = @Info(
                title = "lcxm-generator-spring-boot-starter",
                description = "lcxm-generator-spring-boot-starter",
                version = "1.0.0"

        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
//        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,

        bearerFormat = "JWT")
@Configuration
@Slf4j
@AutoConfiguration
public class SpringDocConfig {
    public SpringDocConfig() {
        log.info("SpringDocConfig init");
    }
}
