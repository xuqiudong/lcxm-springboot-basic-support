package cn.xuqiudong.basic.third.outbound.executor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Minimal HTTP behavior tests for Hutool based executor.
 */
public class HttpOutboundExecutorTest {

    private HttpServer server;

    private String baseUrl;

    private final AtomicReference<String> method = new AtomicReference<>();

    private final AtomicReference<String> query = new AtomicReference<>();

    private final AtomicReference<String> body = new AtomicReference<>();

    @Before
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/echo", this::handle);
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/echo";
    }

    @After
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void executeGetShouldAppendQueryParams() {
        OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                .url(baseUrl)
                .method(ThirdHttpMethod.GET)
                .queryParam("name", "vic")
                .queryParam("page", 1)
                .responseType(String.class)
                .build();

        String response = new HttpOutboundExecutor().execute(request);

        assertEquals("ok", response);
        assertEquals("GET", method.get());
        assertEquals("name=vic&page=1", query.get());
        assertEquals("", body.get());
    }

    @Test
    public void executePostShouldKeepQueryParamsInUrlAndJsonInBody() {
        OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                .url(baseUrl)
                .method(ThirdHttpMethod.POST)
                .queryParam("access_token", "token")
                .body(Map.of("name", "vic"))
                .responseType(String.class)
                .build();

        String response = new HttpOutboundExecutor().execute(request);

        assertEquals("ok", response);
        assertEquals("POST", method.get());
        assertEquals("access_token=token", query.get());
        assertEquals("{\"name\":\"vic\"}", body.get());
    }

    @Test
    public void executePostShouldSendFormParamsAsBody() {
        OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                .url(baseUrl)
                .method(ThirdHttpMethod.POST)
                .formParam("name", "vic")
                .formParam("page", 1)
                .responseType(String.class)
                .build();

        String response = new HttpOutboundExecutor().execute(request);

        assertEquals("ok", response);
        assertEquals("POST", method.get());
        assertEquals(null, query.get());
        assertEquals("name=vic&page=1", body.get());
    }

    private void handle(HttpExchange exchange) throws IOException {
        method.set(exchange.getRequestMethod());
        query.set(exchange.getRequestURI().getRawQuery());
        body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        byte[] response = "ok".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }
}
