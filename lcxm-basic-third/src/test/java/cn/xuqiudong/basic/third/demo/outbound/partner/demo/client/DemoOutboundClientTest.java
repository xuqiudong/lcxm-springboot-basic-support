package cn.xuqiudong.basic.third.demo.outbound.partner.demo.client;

import java.util.concurrent.atomic.AtomicReference;

import cn.xuqiudong.basic.third.common.exception.ThirdException;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.config.DemoOutboundConfigProvider;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoRequest;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoResponse;
import cn.xuqiudong.basic.third.demo.outbound.partner.demo.model.DemoThirdResponse;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
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
        DemoOutboundClient client = new DemoOutboundClient(
                new DemoOutboundConfigProvider(),
                new ThirdClientOptions(),
                new DemoExecutor(requestRef, true));

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
                new DemoOutboundConfigProvider(),
                new ThirdClientOptions(),
                new DemoExecutor(new AtomicReference<>(), false));

        try {
            client.submitOrder(new DemoRequest());
            fail("partner failed response should throw ThirdException");
        } catch (ThirdException e) {
            assertEquals("demo third api failed: 提交订单, failed", e.getMessage());
        }
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
}
