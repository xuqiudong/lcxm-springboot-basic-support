package cn.xuqiudong.generator.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 描述: 启动后展示访问地址
 * @author Vic.xu
 * @since 2024-03-25 17:14
 */
public class ShowAccessUrlHelper implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowAccessUrlHelper.class);

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        ServerProperties serverProperties = applicationContext.getBean(ServerProperties.class);
        Integer port = serverProperties.getPort();
        ServerProperties.Servlet servlet = serverProperties.getServlet();
        String contextPath = servlet.getContextPath();
        String urlSuffix = StringUtils.isBlank(contextPath) ? String.valueOf(port) : port + contextPath;
        LOGGER.info("项目已集成代码生成功能, 请访问地址： http://127.0.0.1:{}/generator", urlSuffix);

    }
}
