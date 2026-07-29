# lcxm-basic-third

第三方对接基础模块，覆盖：

- 出站：我们请求第三方。
- 入站：第三方请求我们，通过 `sign -> token -> interceptor` 鉴权。

模块只提供通用机制，不维护具体项目的第三方参数、缓存配置、厂商接口、业务 URL 和日志落库策略。

## 主包 Tree

```text
cn.xuqiudong.basic.third
|-- common
|   |-- exception
|   |   `-- ThirdException                 # 模块统一运行时异常
|   `-- model
|       `-- ThirdIdentity                  # 第三方标识：code/name
|
|-- config
|   |-- model
|   |   `-- ThirdClientOptions             # 出站通用参数：超时、默认 header、代理、日志开关
|   |-- provider
|   |   |-- ThirdOptionsProvider           # 按 thirdCode 提供出站配置
|   |   `-- DefaultThirdOptionsProvider    # 默认出站配置 provider
|   `-- spring
|       |-- AbstractThirdInboundConfiguration  # 入站 Spring 配置基类；不加 @Configuration
|       `-- AbstractThirdOutboundConfiguration # 出站 Spring 配置基类；不加 @Configuration
|
|-- outbound                              # 我们请求第三方
|   |-- client
|   |   `-- AbstractOutboundClient         # 第三方 Client 基类
|   |-- builder
|   |   `-- OutboundRequestInfoBuilder     # 请求构建器
|   |-- executor
|   |   |-- OutboundExecutor               # HTTP 执行器接口
|   |   `-- HttpOutboundExecutor           # Hutool HTTP 默认实现
|   |-- model
|   |   |-- OutboundRequestInfo            # 出站请求模型
|   |   `-- ThirdHttpMethod                # HTTP method
|   `-- parser
|       `-- OutboundResponseParser         # 特殊响应解析扩展点
|
|-- inbound                               # 第三方请求我们
|   |-- config
|   |   `-- InboundAppConfig               # appId、公钥、username、tokenTtl、时间窗、nonce 开关
|   |-- registry
|   |   `-- InboundAppConfigRegistry       # 入站配置注册点；一个第三方一个实现
|   |-- model
|   |   |-- TokenApplyRequest              # 获取 token 请求
|   |   |-- TokenSignPayload               # 签名字段
|   |   |-- TokenValue                     # token 存储值
|   |   |-- TokenIssueResult               # token 签发结果
|   |   `-- TokenCheckResult               # token 校验/注销结果
|   |-- service
|   |   `-- InboundTokenService            # 验签、签发、校验、注销
|   |-- store
|   |   |-- TokenStore                     # token 存储接口
|   |   |-- RedisTokenStore                # Redis token 存储
|   |   |-- CaffeineTokenStore             # 本地 token 存储；测试/单机可自行声明
|   |   |-- NonceStore                     # nonce 防重接口；可选
|   |   |-- RedisNonceStore                # Redis nonce 防重
|   |   `-- CaffeineNonceStore             # 本地 nonce 防重
|   `-- web
|       |-- controller
|       |   `-- InboundTokenController     # 默认 token 接口：obtain-token、revoke-token
|       `-- interceptor
|           `-- InboundTokenInterceptor    # Spring MVC token 拦截器
|
|-- security
|   |-- RsaSignatureUtils                  # Hutool RSA SHA256withRSA
|   `-- SignaturePayloadBuilder            # 固定顺序构建签名原文
|
`-- log
    |-- model
    |   |-- ThirdExchangeLog               # 出站交换日志模型
    |   `-- ThirdExchangeStatus            # SUCCESS/FAILED
    `-- service
        |-- ThirdExchangeLogger            # 日志处理接口
        `-- Slf4jThirdExchangeLogger       # slf4j 默认实现
```

## 入站接入

业务项目通常只做这些事：

- 写配置类继承 `AbstractThirdInboundConfiguration`，并在子类加 `@Configuration`。
- 默认使用 Redis 存储 token 和 nonce，项目需要提供 `inboundRedisTemplate()`。
- 如需切换为 Caffeine 本地存储，覆盖 `useInboundRedisStore()` 返回 `false`。
- 每个第三方实现一个 `InboundAppConfigRegistry`。
- 按需覆盖 nonce 开关、拦截路径、token header 名称、拦截器自动注册开关。
- 编写业务 Controller。

默认 token header 为 `X-Third-Token`，参数兼容 `token`。

## 出站接入

业务项目通常只做这些事：

- 写配置类继承 `AbstractThirdOutboundConfiguration`，并在子类加 `@Configuration`。
- 每个第三方写一个 Client，继承 `AbstractOutboundClient`。
- 实现 `thirdIdentity()`、`buildUrl(String path)`、`buildHeaders()`。
- 需要按厂商配置参数时覆盖 `thirdClientOptions(String thirdCode)`。
- 需要日志落库时覆盖 `saveThirdExchangeLog(ThirdExchangeLog log)`。
- 需要特殊响应解析时使用 `OutboundResponseParser`。

## Token 过期

旧模块固定 60 分钟：`TOKEN_EXPIRE_SECONDS = 3600L`。

当前模块默认仍是 1 小时，但不是写死策略：

- 默认值：`InboundAppConfig.tokenTtl = Duration.ofHours(1)`。
- 每个 `appId` 可以通过 `InboundAppConfigRegistry` 返回不同 TTL。
- Redis 版本由 Redis TTL 控制过期；Caffeine 版本通过 `TokenValue.expireAt` 校验过期。

## 依赖策略

- 依赖 `lcxm-basic-core`，不依赖 `lcxm-basic-framework`。
- RSA、HTTP、Date、UUID 优先使用 Hutool。
- `spring-webmvc`、`spring-data-redis` 为 optional。
- 当前主线面向 Spring Boot 3 / Spring 6 / JDK 21，使用 `jakarta.servlet`。
- 低版本 Spring MVC 适配见 [spring-version-adapter.md](spring-version-adapter.md)。

## 文档

- [第三方入站接口文档](docs/第三方入站接口文档.md)
- [开发流程：第三方接入我方](docs/开发流程-接入我方.md)
- [开发流程：我方调用第三方](docs/开发流程-调用他方.md)
