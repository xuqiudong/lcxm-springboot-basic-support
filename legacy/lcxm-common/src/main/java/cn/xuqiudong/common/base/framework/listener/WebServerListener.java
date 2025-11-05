package cn.xuqiudong.common.base.framework.listener;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;

/**
 * 描述:
 * 获取端口和 contextPath
 *
 * @author Vic.xu
 * @since 2024-06-28 13:08
 */
public class WebServerListener implements ApplicationListener<WebServerInitializedEvent> {

    public static int port;

    public static String contextPath;

    public static int getPort() {
        return port;
    }

    public static String getContextPath() {
        return contextPath;
    }

    /**
     * 自身访问地址
     *
     * @return http://127.0.0.1:｛port｝｛contextPath｝
     */
    public static String selfVisitUrl() {
        return String.format("http://127.0.0.1:%d%s", port, contextPath == null ? "" : contextPath);
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        WebServer webServer = event.getWebServer();
        port = webServer.getPort();
        contextPath = event.getApplicationContext().getEnvironment().getProperty("server.servlet.context-path");
    }

}
