package cn.xuqiudong.basic.third.outbound.executor;

import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.log.model.OutboundExchangeLog;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestType;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Minimal HTTP behavior tests for Hutool based executor.
 */
public class HttpOutboundExecutorTest {

    private HttpServer server;

    private String baseUrl;

    private final AtomicReference<String> method = new AtomicReference<>();

    private final AtomicReference<String> query = new AtomicReference<>();

    private final AtomicReference<String> body = new AtomicReference<>();

    private final AtomicReference<String> contentType = new AtomicReference<>();

    private final AtomicReference<String> accept = new AtomicReference<>();

    @Before
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/echo", this::handle);
        server.createContext("/error", this::handleError);
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
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("name", "vic");
        OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                .url(baseUrl)
                .method(ThirdHttpMethod.POST)
                .queryParam("access_token", "token")
                .body(bodyMap)
                .responseType(String.class)
                .build();

        String response = new HttpOutboundExecutor().execute(request);

        assertEquals("ok", response);
        assertEquals("POST", method.get());
        assertEquals("access_token=token", query.get());
        assertEquals("{\"name\":\"vic\"}", body.get());
        assertTrue(contentType.get().startsWith("application/json"));
        assertEquals("application/json", accept.get());
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
        assertTrue(contentType.get().startsWith("application/x-www-form-urlencoded"));
    }

    @Test
    public void executePostShouldSendMultipartInputStream() {
        CloseAwareInputStream inputStream =
                new CloseAwareInputStream("hello multipart".getBytes(StandardCharsets.UTF_8));
        OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                .url(baseUrl)
                .method(ThirdHttpMethod.POST)
                .multipartParam("path", "/apps/demo.txt")
                .multipartFile("file", "demo.txt", inputStream)
                .responseType(String.class)
                .build();

        String response = new HttpOutboundExecutor().execute(request);

        assertEquals("ok", response);
        assertEquals("POST", method.get());
        assertEquals(OutboundRequestType.MULTIPART, request.getRequestType());
        assertTrue(contentType.get().startsWith("multipart/form-data"));
        assertTrue(body.get().contains("name=\"file\""));
        assertTrue(body.get().contains("filename=\"demo.txt\""));
        assertTrue(body.get().contains("hello multipart"));
        assertTrue(inputStream.isClosed());
    }

    @Test
    public void executePostShouldSendMultipleFilesWithSameFieldName() throws IOException {
        File first = File.createTempFile("third-upload-a", ".txt");
        File second = File.createTempFile("third-upload-b", ".txt");
        try {
            Files.write(first.toPath(), "first file".getBytes(StandardCharsets.UTF_8));
            Files.write(second.toPath(), "second file".getBytes(StandardCharsets.UTF_8));
            OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                    .url(baseUrl)
                    .method(ThirdHttpMethod.POST)
                    .multipartFile("file", first, "a.txt")
                    .multipartFile("file", second, "b.txt")
                    .responseType(String.class)
                    .build();

            String response = new HttpOutboundExecutor().execute(request);

            assertEquals("ok", response);
            assertEquals(2, count(body.get(), "name=\"file\""));
            assertTrue(body.get().contains("filename=\"a.txt\""));
            assertTrue(body.get().contains("filename=\"b.txt\""));
            assertTrue(body.get().contains("first file"));
            assertTrue(body.get().contains("second file"));
        } finally {
            first.delete();
            second.delete();
        }
    }

    @Test
    public void executeShouldFailWhenHttpStatusIsError() {
        OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                .url("http://127.0.0.1:" + server.getAddress().getPort() + "/error")
                .method(ThirdHttpMethod.GET)
                .responseType(String.class)
                .build();

        try {
            new HttpOutboundExecutor().execute(request);
            fail("http error status should throw ThirdException");
        } catch (ThirdException e) {
            assertEquals("third http status failed: 500", e.getMessage());
        }
    }

    @Test
    public void executeShouldKeepFullExchangeLogTextForCustomLogger() {
        AtomicReference<OutboundExchangeLog> logRef = new AtomicReference<>();
        ThirdClientOptions options = new ThirdClientOptions();
        OutboundRequestInfo<String> request = OutboundRequestInfo.<String>builder(ThirdIdentity.of("demo"))
                .url(baseUrl)
                .method(ThirdHttpMethod.POST)
                .body("1234567890")
                .responseType(String.class)
                .build();

        new HttpOutboundExecutor(options, null, logRef::set).execute(request);


        assertEquals("1234567890", logRef.get().getRequestBody());
        assertEquals("ok", logRef.get().getResponseBody());
    }

    private void handle(HttpExchange exchange) throws IOException {
        method.set(exchange.getRequestMethod());
        query.set(exchange.getRequestURI().getRawQuery());
        contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
        accept.set(exchange.getRequestHeaders().getFirst("Accept"));
        body.set(readToString(exchange.getRequestBody()));
        byte[] response = "ok".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }

    private void handleError(HttpExchange exchange) throws IOException {
        byte[] response = "failed".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(500, response.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response);
        }
    }

    private String readToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, length);
        }
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    private int count(String source, String target) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(target, index)) >= 0) {
            count++;
            index += target.length();
        }
        return count;
    }

    private static class CloseAwareInputStream extends ByteArrayInputStream {

        private boolean closed;

        private CloseAwareInputStream(byte[] buf) {
            super(buf);
        }

        @Override
        public void close() throws IOException {
            this.closed = true;
            super.close();
        }

        private boolean isClosed() {
            return closed;
        }
    }
}
