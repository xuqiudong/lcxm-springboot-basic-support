package cn.xuqiudong.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述:
 * 测试自动生成代码的starter
 * 在test中启动项目 做测试，单元测试的时候 ：@SpringBootTest(classes = TestGeneratorApplication.class)
 * 默认情况下，Spring Boot 会根据 AutoConfiguration.imports 加载指定的自动配置类
 *
 * @author Vic.xu
 * @since 2025-09-10 17:00
 */
@SpringBootApplication
public class TestGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestGeneratorApplication.class, args);
    }
}
