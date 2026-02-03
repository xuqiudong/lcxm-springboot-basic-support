package cn.xuqiudong.mq.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 描述:  在test中启动项目 做测试，单元测试的时候 ：@SpringBootTest(classes = TestStarterApplication.class)
 *   默认情况下，Spring Boot 会根据 AutoConfiguration.imports 加载指定的自动配置类
 * @author Vic.xu
 * @since 2025-02-26 14:16
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class TestStarterApplication {

    private static final Logger logger = LoggerFactory.getLogger(TestStarterApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TestStarterApplication.class, args);
    }




}