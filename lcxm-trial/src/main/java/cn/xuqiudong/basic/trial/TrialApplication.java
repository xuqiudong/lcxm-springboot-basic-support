package cn.xuqiudong.basic.trial;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 描述:
 *    测试某些东西
 * @author Vic.xu
 * @since 2025-12-01 13:14
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@RestController
public class TrialApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrialApplication.class, args);
    }


    @GetMapping("/")
    public Object index(){
        return "hello world, this is trial " + LocalTime.now();
    }
}
