package cn.xuqiudong.basic.framework;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 描述:
 * 依赖本机 Redis 的 Spring 集成测试基类，仅供手动执行。
 *
 * @author Vic.xu
 * @since 2026-01-14 17:47
 */
@Tag("integration")
@Tag("manual")
@SpringBootTest(classes = FrameworkTestApplication.class)
public class BaseTest {

    @Test
    public void contextLoads() {
    }
}
