package cn.xuqiudong.basic.third.outbound.client;

import cn.xuqiudong.basic.third.common.model.ThirdIdentity;
import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;
import cn.xuqiudong.basic.third.outbound.api.ThirdApi;
import cn.xuqiudong.basic.third.outbound.config.OutboundPartnerConfig;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutor;
import cn.xuqiudong.basic.third.outbound.executor.OutboundExecutorFactory;
import cn.xuqiudong.basic.third.outbound.model.OutboundRequestInfo;
import cn.xuqiudong.basic.third.outbound.model.ThirdHttpMethod;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link AbstractOutboundPartner} Runtime 生命周期测试。
 *
 * @author Vic.xu
 * @since 2026-11-17
 */
class AbstractOutboundPartnerRuntimeTest {

    /**
     * 验证构造阶段不加载 Runtime，首次访问后复用同一个完整快照。
     */
    @Test
    void runtimeShouldInitializeLazilyAndReuseCachedSnapshot() {
        CountingExecutorFactory factory = new CountingExecutorFactory();
        TestPartner partner = new TestPartner(factory, Duration.ofMinutes(5));

        assertEquals(0, partner.getLoadCount());
        assertEquals(0, factory.getCreateCount());

        TestConfig config = partner.readConfig();
        ThirdClientOptions options = partner.readOptions();
        CapturingExecutor executor = partner.readExecutor();

        assertEquals(1, partner.getLoadCount());
        assertEquals(1, partner.getResolveOptionsCount());
        assertEquals(1, factory.getCreateCount());
        assertEquals("https://partner-1.example.com", config.getHost());
        assertSame(options, executor.getOptions());
        assertSame(config, partner.readConfig());
        assertSame(options, partner.readOptions());
        assertSame(executor, partner.readExecutor());
    }

    /**
     * 验证主动清理缓存后，配置、HTTP 配置和执行器会作为整体刷新。
     */
    @Test
    void clearRuntimeCacheShouldRefreshWholeSnapshot() {
        CountingExecutorFactory factory = new CountingExecutorFactory();
        TestPartner partner = new TestPartner(factory, Duration.ofMinutes(5));

        TestConfig firstConfig = partner.readConfig();
        ThirdClientOptions firstOptions = partner.readOptions();
        CapturingExecutor firstExecutor = partner.readExecutor();

        partner.clearRuntime();

        TestConfig secondConfig = partner.readConfig();
        ThirdClientOptions secondOptions = partner.readOptions();
        CapturingExecutor secondExecutor = partner.readExecutor();

        assertNotSame(firstConfig, secondConfig);
        assertNotSame(firstOptions, secondOptions);
        assertNotSame(firstExecutor, secondExecutor);
        assertEquals("https://partner-2.example.com", secondConfig.getHost());
        assertEquals(Duration.ofSeconds(2), secondOptions.getRequestTimeout());
        assertEquals("2", secondOptions.getDefaultHeaders().get("X-Config-Version"));
        assertFalse(secondOptions.isExchangeLogEnabled());
        InetSocketAddress proxyAddress = (InetSocketAddress) secondOptions.getProxy().address();
        assertEquals("proxy-2.example.com", proxyAddress.getHostString());
        assertEquals(8002, proxyAddress.getPort());
        assertSame(secondOptions, secondExecutor.getOptions());
        assertEquals(2, partner.getLoadCount());
        assertEquals(2, factory.getCreateCount());
    }

    /**
     * 验证非正数 TTL 表示固定 Runtime，不会在后续访问中自动重建。
     */
    @Test
    void nonPositiveTtlShouldKeepFixedRuntime() {
        CountingExecutorFactory factory = new CountingExecutorFactory();
        TestPartner partner = new TestPartner(factory, Duration.ZERO);

        CapturingExecutor firstExecutor = partner.readExecutor();
        CapturingExecutor secondExecutor = partner.readExecutor();

        assertSame(firstExecutor, secondExecutor);
        assertEquals(1, partner.getLoadCount());
        assertEquals(1, factory.getCreateCount());
    }

    /**
     * 验证正数 TTL 到期后，下一次访问会延迟创建新 Runtime。
     *
     * @throws InterruptedException 测试等待被中断时抛出
     */
    @Test
    void positiveTtlShouldRefreshRuntimeAfterExpiration() throws InterruptedException {
        CountingExecutorFactory factory = new CountingExecutorFactory();
        TestPartner partner = new TestPartner(factory, Duration.ofMillis(5));

        CapturingExecutor firstExecutor = partner.readExecutor();
        Thread.sleep(30);
        CapturingExecutor secondExecutor = partner.readExecutor();

        assertNotSame(firstExecutor, secondExecutor);
        assertEquals(2, partner.getLoadCount());
        assertEquals(2, factory.getCreateCount());
    }

    /**
     * 验证并发首次访问只加载一个有效 Runtime。
     *
     * @throws Exception 并发任务执行失败时抛出
     */
    @Test
    void concurrentFirstAccessShouldLoadSingleRuntime() throws Exception {
        CountingExecutorFactory factory = new CountingExecutorFactory();
        TestPartner partner = new TestPartner(factory, Duration.ofMinutes(5));
        int taskCount = 12;
        CountDownLatch startGate = new CountDownLatch(1);
        ExecutorService threadPool = Executors.newFixedThreadPool(taskCount);
        List<Future<CapturingExecutor>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < taskCount; i++) {
                futures.add(threadPool.submit(() -> {
                    startGate.await();
                    return partner.readExecutor();
                }));
            }

            startGate.countDown();
            CapturingExecutor expected = futures.get(0).get();
            for (Future<CapturingExecutor> future : futures) {
                assertSame(expected, future.get());
            }
        } finally {
            threadPool.shutdownNow();
        }

        assertEquals(1, partner.getLoadCount());
        assertEquals(1, partner.getResolveOptionsCount());
        assertEquals(1, factory.getCreateCount());
    }

    /**
     * Runtime 测试用 Partner，可观察配置、HTTP 配置和执行器生命周期。
     *
     * @author Vic.xu
     * @since 2026-11-17
     */
    private static final class TestPartner extends AbstractOutboundPartner<TestConfig, TestApi> {

        /**
         * 测试指定的 Runtime 缓存时间。
         */
        private final Duration cacheTtl;

        /**
         * 厂商配置加载次数。
         */
        private final AtomicInteger loadCount = new AtomicInteger();

        /**
         * HTTP 配置解析次数。
         */
        private final AtomicInteger resolveOptionsCount = new AtomicInteger();

        /**
         * 创建测试 Partner。
         *
         * @param executorFactory 执行器工厂
         * @param cacheTtl       Runtime 缓存时间
         */
        private TestPartner(OutboundExecutorFactory executorFactory, Duration cacheTtl) {
            super(executorFactory);
            this.cacheTtl = cacheTtl;
        }

        @Override
        protected ThirdIdentity thirdIdentity() {
            return ThirdIdentity.of("runtime-test");
        }

        @Override
        protected TestConfig loadConfig() {
            int version = loadCount.incrementAndGet();
            ThirdClientOptions options = new ThirdClientOptions()
                    .setRequestTimeout(Duration.ofSeconds(version))
                    .setExchangeLogEnabled(version % 2 != 0)
                    .setProxy(Proxy.Type.HTTP, "proxy-" + version + ".example.com", 8000 + version)
                    .addDefaultHeader("X-Config-Version", String.valueOf(version));
            return new TestConfig("https://partner-" + version + ".example.com", options);
        }

        @Override
        protected ThirdClientOptions resolveOptions(TestConfig config) {
            resolveOptionsCount.incrementAndGet();
            return super.resolveOptions(config);
        }

        @Override
        protected Duration configCacheTtl() {
            return cacheTtl;
        }

        /**
         * 暴露当前厂商配置供测试断言。
         *
         * @return 当前厂商配置
         */
        private TestConfig readConfig() {
            return getConfig();
        }

        /**
         * 暴露当前 HTTP 配置供测试断言。
         *
         * @return 当前 HTTP 配置
         */
        private ThirdClientOptions readOptions() {
            return options();
        }

        /**
         * 暴露当前执行器供测试断言。
         *
         * @return 当前测试执行器
         */
        private CapturingExecutor readExecutor() {
            return (CapturingExecutor) executor();
        }

        /**
         * 清理当前 Runtime 供测试触发刷新。
         */
        private void clearRuntime() {
            clearRuntimeCache();
        }

        /**
         * 获取配置加载次数。
         *
         * @return 配置加载次数
         */
        private int getLoadCount() {
            return loadCount.get();
        }

        /**
         * 获取 HTTP 配置解析次数。
         *
         * @return HTTP 配置解析次数
         */
        private int getResolveOptionsCount() {
            return resolveOptionsCount.get();
        }
    }

    /**
     * Runtime 测试用厂商配置。
     *
     * @author Vic.xu
     * @since 2026-11-17
     */
    private static final class TestConfig implements OutboundPartnerConfig {

        /**
         * 测试服务地址。
         */
        private final String host;

        /**
         * 测试 HTTP 配置。
         */
        private final ThirdClientOptions clientOptions;

        /**
         * 创建测试配置。
         *
         * @param host          服务地址
         * @param clientOptions HTTP 配置
         */
        private TestConfig(String host, ThirdClientOptions clientOptions) {
            this.host = host;
            this.clientOptions = clientOptions;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public ThirdClientOptions getClientOptions() {
            return clientOptions;
        }
    }

    /**
     * Runtime 测试用执行器工厂，记录执行器创建次数。
     *
     * @author Vic.xu
     * @since 2026-11-17
     */
    private static final class CountingExecutorFactory extends OutboundExecutorFactory {

        /**
         * 执行器创建次数。
         */
        private final AtomicInteger createCount = new AtomicInteger();

        /**
         * 创建不依赖真实 HTTP 组件的测试工厂。
         */
        private CountingExecutorFactory() {
            super(null, false, 4000, null);
        }

        @Override
        public OutboundExecutor create(ThirdClientOptions options) {
            createCount.incrementAndGet();
            return new CapturingExecutor(options);
        }

        /**
         * 获取执行器创建次数。
         *
         * @return 执行器创建次数
         */
        private int getCreateCount() {
            return createCount.get();
        }
    }

    /**
     * Runtime 测试用执行器，保留创建时绑定的 HTTP 配置。
     *
     * @author Vic.xu
     * @since 2026-11-17
     */
    private static final class CapturingExecutor implements OutboundExecutor {

        /**
         * 执行器创建时绑定的 HTTP 配置。
         */
        private final ThirdClientOptions options;

        /**
         * 创建测试执行器。
         *
         * @param options 当前 Runtime 的 HTTP 配置
         */
        private CapturingExecutor(ThirdClientOptions options) {
            this.options = options;
        }

        @Override
        public <T> T execute(OutboundRequestInfo<T> request) {
            return null;
        }

        @Override
        public byte[] executeBytes(OutboundRequestInfo<?> request) {
            return new byte[0];
        }

        /**
         * 获取执行器绑定的 HTTP 配置。
         *
         * @return HTTP 配置
         */
        private ThirdClientOptions getOptions() {
            return options;
        }
    }

    /**
     * Runtime 测试占位 API。
     *
     * @author Vic.xu
     * @since 2026-11-17
     */
    private enum TestApi implements ThirdApi {

        /**
         * 唯一测试接口。
         */
        TEST;

        @Override
        public String getApiName() {
            return "Runtime 测试";
        }

        @Override
        public String getPath() {
            return "/runtime";
        }

        @Override
        public ThirdHttpMethod getMethod() {
            return ThirdHttpMethod.GET;
        }
    }
}
