package cn.xuqiudong.basic.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 描述:
 *  测试模块的启动类
 * @author Vic.xu
 * @since 2026-01-14 17:37
 */
@SpringBootApplication(exclude =  {
        DataSourceAutoConfiguration.class
})
public class FrameworkTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkTestApplication.class, args);
    }
}
