package cn.xuqiudong.basic.third.demo.outbound.partner.demo.client;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.config.DemoOutboundConfig;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoRequest;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoResponse;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoThirdResponse;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutorFactory;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * demo 出站 Client 可直接运行的参考测试。
 */
public class DemoOutboundClientTest {

    @Test
    public void submitOrderShouldBuildPartnerRequest() {
        AtomicReference<OutboundRequestInfo<?>> requestRef = new AtomicReference<>();
        DemoOutboundClient client = new DemoOutboundClient(demoFactory(new DemoExecutor(requestRef, true)));

        DemoRequest request = new DemoRequest();
        request.setOrderNo("O-001");
        request.setAmount("100");

        DemoThirdResponse<DemoResponse> response = client.submitOrder(request);

        assertEquals("0000", response.getCode());
        assertEquals("O-001", response.getData().getOrderNo());
        assertEquals("SUBMITTED", response.getData().getStatus());
        assertEquals("https://third.example.com/order/submit", requestRef.get().getUrl());
        assertEquals("提交订单", requestRef.get().getOperation());
        assertEquals(OutboundRequestType.JSON, requestRef.get().getRequestType());
        assertEquals("Bearer demo-client-id:demo-client-secret",
                requestRef.get().getHeaders().get("Authorization"));
    }

    @Test
    public void submitOrderShouldThrowWhenPartnerResponseFailed() {
        DemoOutboundClient client = new DemoOutboundClient(
                demoFactory(new DemoExecutor(new AtomicReference<>(), false)));

        try {
            client.submitOrder(new DemoRequest());
            fail("partner failed response should throw ThirdException");
        } catch (ThirdException e) {
            assertEquals("demo third api failed: 提交订单, failed", e.getMessage());
        }
    }

    @Test
    public void queryOrderShouldSupportBuilderRequest() {
        AtomicReference<OutboundRequestInfo<?>> requestRef = new AtomicReference<>();
        DemoOutboundClient client = new DemoOutboundClient(demoFactory(new DemoExecutor(requestRef, true)));

        DemoThirdResponse<DemoResponse> response = client.queryOrder("O-001");

        assertEquals("0000", response.getCode());
        assertEquals("https://third.example.com/order/query", requestRef.get().getUrl());
        assertEquals("O-001", requestRef.get().getQueryParams().get("orderNo"));
        assertEquals("query-order", requestRef.get().getHeaders().get("X-Demo-Trace"));
        assertEquals(OutboundRequestType.QUERY, requestRef.get().getRequestType());
    }

    @Test
    public void queryOrderByFormShouldBuildFormRequest() {
        AtomicReference<OutboundRequestInfo<?>> requestRef = new AtomicReference<>();
        DemoOutboundClient client = new DemoOutboundClient(demoFactory(new DemoExecutor(requestRef, true)));

        DemoThirdResponse<DemoResponse> response = client.queryOrderByForm("O-001", "T-001");

        assertEquals("0000", response.getCode());
        assertEquals("https://third.example.com/order/query-form", requestRef.get().getUrl());
        assertEquals("O-001", requestRef.get().getFormParams().get("orderNo"));
        assertEquals("T-001", requestRef.get().getFormParams().get("tenantId"));
        assertEquals(OutboundRequestType.FORM, requestRef.get().getRequestType());
    }

    @Test
    public void uploadOrderFileShouldBuildMultipartRequest() throws IOException {
        AtomicReference<OutboundRequestInfo<?>> requestRef = new AtomicReference<>();
        DemoOutboundClient client = new DemoOutboundClient(demoFactory(new DemoExecutor(requestRef, true)));
        File file = File.createTempFile("demo-order", ".txt");
        try {
            Files.write(file.toPath(), "order file".getBytes(StandardCharsets.UTF_8));

            DemoThirdResponse<DemoResponse> response = client.uploadOrderFile("O-001", file);

            assertEquals("0000", response.getCode());
            assertEquals("https://third.example.com/order/upload", requestRef.get().getUrl());
            assertEquals("O-001", requestRef.get().getQueryParams().get("orderNo"));
            assertEquals(2, requestRef.get().getMultipartParts().size());
            assertEquals(OutboundRequestType.MULTIPART, requestRef.get().getRequestType());
        } finally {
            file.delete();
        }
    }

    @Test
    public void submitOrderShouldReuseCachedConfig() {
        CountingDemoOutboundClient client = new CountingDemoOutboundClient(
                demoFactory(new DemoExecutor(new AtomicReference<>(), true)));

        client.submitOrder(new DemoRequest());
        client.submitOrder(new DemoRequest());

        assertEquals(1, client.getLoadCount());
    }

    private static class DemoExecutor implements OutboundExecutor {

        private final AtomicReference<OutboundRequestInfo<?>> requestRef;

        private final boolean success;

        private DemoExecutor(AtomicReference<OutboundRequestInfo<?>> requestRef, boolean success) {
            this.requestRef = requestRef;
            this.success = success;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T execute(OutboundRequestInfo<T> request) {
            requestRef.set(request);
            DemoThirdResponse<DemoResponse> response = new DemoThirdResponse<>();
            response.setCode(success ? "0000" : "9999");
            response.setMessage(success ? "ok" : "failed");
            DemoResponse data = new DemoResponse();
            data.setOrderNo("O-001");
            data.setStatus("SUBMITTED");
            response.setData(data);
            return (T) response;
        }

        @Override
        public byte[] executeBytes(OutboundRequestInfo<?> request) {
            requestRef.set(request);
            return new byte[0];
        }
    }

    private static OutboundExecutorFactory demoFactory(OutboundExecutor executor) {
        return new OutboundExecutorFactory(null, true, 4000, null) {
            @Override
            public OutboundExecutor create(ThirdClientOptions options) {
                return executor;
            }
        };
    }

    private static class CountingDemoOutboundClient extends DemoOutboundClient {

        private final AtomicInteger loadCount = new AtomicInteger();

        private CountingDemoOutboundClient(OutboundExecutorFactory executorFactory) {
            super(executorFactory);
        }

        @Override
        protected DemoOutboundConfig loadConfig() {
            loadCount.incrementAndGet();
            return super.loadConfig();
        }

        private int getLoadCount() {
            return loadCount.get();
        }
    }
}
