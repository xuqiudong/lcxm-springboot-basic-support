package cn.xuqiudong.basic.core.srpc.protocol;


import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * description：
 * 优化下srpc请求 HttpClient的设置
 *
 * @author Vic.xu
 * @since 2024-12-04 9:52
 */
public class HttpClientProvider {
    private static final CloseableHttpClient httpClient;
    private static final ScheduledExecutorService scheduler;

    private static RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig.custom()
            // 从连接池获取连接的超时时间
            .setConnectionRequestTimeout(20000)
            // 建立连接的超时时间
            .setConnectTimeout(20000)
            // 数据传输的超时时间
            .setSocketTimeout(50000)
            .build();

    static {
        // 1. 创建连接池
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connManager.setMaxTotal(200);
        // 设置每个路由的最大连接数
        connManager.setDefaultMaxPerRoute(50);

        // 2. 设置 Keep-Alive 策略
        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> 30 * 1000; // 30 秒

        // 3. 设置请求超时配置
        RequestConfig requestConfig = RequestConfig.copy(DEFAULT_REQUEST_CONFIG).build();

        // 4. 定期清理空闲和过期连接
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            // 关闭空闲超过 30 秒的连接
            connManager.closeExpiredConnections();
            connManager.closeIdleConnections(30, TimeUnit.SECONDS);
        }, 0, 5, TimeUnit.MINUTES);

        // 5. 设置重试策略
        HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
            if (executionCount >= 3) {
                return false;                  // 最大重试次数为 3
            }
            if (exception instanceof InterruptedIOException ||
                    exception instanceof UnknownHostException ||
                    exception instanceof SSLException) {
                // 超时、未知主机、SSL异常不重试
                return false;
            }
            // 连接超时重试
            return exception instanceof ConnectTimeoutException;
        };

        // 整合所有配置
        httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setRetryHandler(retryHandler)
                .build();
    }


    /**
     * 快速创建“自定义传输超时”的RequestConfig（供特殊业务使用）
     * 仅修改SocketTimeout，其他超时沿用全局默认值（避免重复配置，减少出错）
     *
     * @param socketTimeoutMs 传输超时 毫秒数
     * @return RequestConfig
     */
    public static RequestConfig createCustomSocketTimeoutConfig(int socketTimeoutMs) {
        return RequestConfig.copy(DEFAULT_REQUEST_CONFIG)
                .setSocketTimeout(socketTimeoutMs)
                .build();
    }


    // 获取 HttpClient 实例
    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    // 关闭资源
    public static void shutdown() {
        scheduler.shutdown();
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
