package cn.xuqiudong.common.base.craw;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * HTTP请求工具类
 *
 * @author VIC
 */
public class CrawlConnect {

    public static final String CONTENT_TYPE_KEY = "Content-Type";

    public static final String ACCEPT_KEY = "Accept";

    public static final String CONTENT_TYPE_VALUE_JSON = "application/json;charset=UTF-8";

    private final Logger logger = LoggerFactory.getLogger(CrawlConnect.class);

    private Connection connection;


    /**
     * 构造CrawlConnect对象
     *
     * @param url
     * @return
     */
    public static CrawlConnect build(String url) {
        return new CrawlConnect(url);
    }


    public Connection getConnection() {
        return connection;
    }

    /**
     * 忽略SSL警告 https://stackoverflow.com/questions/7744075/how-to-connect-via-https-using-jsoup 1.12
     * 就已经移除了validateTLSCertificates方法, 所以这里曲线救国一下 但是这显然不是一个好的方法
     */
    public CrawlConnect validateTlsCertificates() {
        connection.sslSocketFactory(socketFactory());
        return this;
    }

    /**
     * 构造方法
     */
    public CrawlConnect(Connection connection) {
        this.connection = connection;
    }

    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory");
        }
    }

    /**
     * 构造方法
     *
     * @param url url
     */
    public CrawlConnect(String url) {
        this.connection = Jsoup.connect(url).ignoreContentType(true).timeout(30000);
    }

    public CrawlConnect url(String url) {
        connection.url(url);
        return this;
    }

    /**
     * 超时时间 毫秒
     */
    public CrawlConnect setTimeout(int millis) {
        this.connection.timeout(millis);
        return this;
    }

    public CrawlConnect url(URL url) {
        connection.url(url);
        return this;
    }

    public CrawlConnect cookie(String name, String value) {
        connection.cookie(name, value);
        return this;
    }

    public CrawlConnect cookie(Map<String, String> cookies) {
        connection.cookies(cookies);
        return this;
    }

    public CrawlConnect data(String... keyvals) {
        connection.data(keyvals);
        return this;
    }

    public CrawlConnect data(String key, String value) {
        connection.data(key, value);
        return this;
    }

    public CrawlConnect data(Map<String, String> data) {
        connection.data(data);
        return this;
    }

    /**
     * 1.8.x之后的功能
     *
     * @return this
     */
    public CrawlConnect data(String key, String filename, InputStream in) {
        connection.data(key, filename, in);
        return this;
    }

    /**
     * @param body
     * @return this
     */
    public CrawlConnect requestBody(String body) {
        connection.requestBody(body);
        return this;
    }

    public Response execute() throws IOException {
        return connection.execute();
    }

    public CrawlConnect followRedirects(boolean followRedirects) {
        connection.followRedirects(followRedirects);
        return this;
    }

    /**
     * 本页面的查询条件
     */
    public Document getDocument() throws IOException {
        return connection.get();
    }

    public String getHtml() throws IOException {
        return this.getDocument().html();
    }

    public String getBodyText() throws IOException {
        return this.getDocument().body().text();
    }

    public CrawlConnect header(String key, String value) {
        connection.header(key, value);
        return this;
    }

    public CrawlConnect header(Map<String, String> headers) {
        if (headers != null) {
            connection.headers(headers);
        }
        return this;
    }

    /**
     * 设置请求头为json数据
     *
     * @return this
     */
    public CrawlConnect requestJson() {
        connection.header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE_JSON);
        connection.header(ACCEPT_KEY, CONTENT_TYPE_VALUE_JSON);
        return this;
    }

    public CrawlConnect maxBodySize(int bytes) {
        connection.maxBodySize(bytes);
        return this;
    }

    public CrawlConnect method(Connection.Method method) {
        connection.method(method);
        return this;
    }

    /**
     * Provide an alternate parser to use when parsing the response to a Document.
     */
    public CrawlConnect parser(Parser parser) {
        connection.parser(parser);
        return this;
    }

    /**
     * post
     */
    public Document postDocument() throws IOException {
        return connection.post();
    }

    public String postHtml() throws IOException {
        return this.postDocument().html();
    }

    public String postBodyText() throws IOException {
        return this.postDocument().body().text();
    }

    /**
     * Sets the default post data character set for x-www-form-urlencoded post data
     */
    public CrawlConnect postDataCharset(String charset) {
        connection.postDataCharset(charset);
        return this;
    }

    public Connection.Request request() {
        return connection.request();
    }

    public Response response() {
        return connection.response();
    }

    /**
     * 下载文件到本地
     *
     * @param path
     * @param fileName
     * @throws IOException
     */
    public void downFile(String path, String fileName) throws IOException {
        logger.info("下载文件 到本地{}{}", path, fileName);
        File file = getFileByPathAndName(path, fileName);
        try (FileOutputStream out = new FileOutputStream(file);) {
            Response response = this.execute();
            out.write(response.bodyAsBytes());
        }

    }

    private File getFileByPathAndName(String path, String fileName) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                logger.error("创建目录失败");
            }
        }
        return new File(path, fileName);
    }

}