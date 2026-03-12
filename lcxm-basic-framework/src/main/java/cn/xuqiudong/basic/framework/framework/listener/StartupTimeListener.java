package cn.xuqiudong.basic.framework.framework.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 描述:
 *    打印系统启动时间已经自定义banner等信息
 * @author Vic.xu
 * @since 2024-06-28 10:37
 */
public class StartupTimeListener implements SpringApplicationRunListener {

    public static final Logger LOGGER = LoggerFactory.getLogger(StartupTimeListener.class);
    private final SpringApplication application;
    private final String[] args;
    private long startTime;
    StopWatch stopWatch;

    // 必须提供这个构造方法
    public StartupTimeListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
        this.stopWatch= new StopWatch();
        stopWatch.start();
    }



    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        // 上下文准备好后的处理
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        // 上下文加载完成后的处理
    }


    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {

        StringBuilder info = new StringBuilder();
        info.append(("\n----------------------------------------------------------------"));
        try (InputStream inputStream = new ClassPathResource("META-INF/banner.txt").getInputStream()) {
            String banner = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            info.append("\n");
            info.append(banner);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        String url = WebServerListener.selfVisitUrl();
        stopWatch.stop();
        info.append(String.format("项目启动完成, 耗时%dms, 请访问: %s", stopWatch.getTotalTimeMillis(), url));
        String property = context.getEnvironment().getProperty("springdoc.api-docs.enabled");
        if ("true".equals(property)) {
            info.append("\n");
            info.append("接口文档地址: " + WebServerListener.selfVisitUrl() + "/swagger-ui/index.html");
        }
        info.append("\n----------------------------------------------------------------");
        LOGGER.info(info.toString());

    }


    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        // 应用程序启动失败时的处理
    }
}
