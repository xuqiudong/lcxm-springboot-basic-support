# lcxm-basic-third

第三方对接基础模块，覆盖：

- 出站：我们请求第三方。
- 入站：第三方请求我们，通过 `sign -> token -> interceptor` 鉴权。

模块只提供通用机制，不维护具体项目的第三方参数、缓存配置、厂商接口、业务 URL 和日志落库策略。

## 如何阅读

| 你要做什么 | 先看 |
| --- | --- |
| 快速了解模块结构和核心类 | 本文的 [主包 Tree](#主包-tree) |
| 判断设计边界、配置基类、请求流程 | [设计说明](docs/design.md) |
| 开发“第三方请求我方”的项目代码 | [开发流程：第三方接入我方](docs/开发流程-接入我方.md) |
| 开发“我方调用第三方”的项目代码 | [开发流程：我方调用第三方](docs/开发流程-调用他方.md) |
| 给第三方系统看的接口协议 | [第三方入站接口文档](docs/第三方入站接口文档.md) |
| 迁移到 Spring MVC 低版本 / JDK 8 | [Spring 低版本适配说明](docs/spring-version-adapter.md) |

推荐阅读顺序：

```text
readme.md
  -> docs/design.md
  -> 按方向选择：
       入站：docs/开发流程-接入我方.md
       出站：docs/开发流程-调用他方.md
  -> 对外提供：docs/第三方入站接口文档.md
  -> 低版本迁移：docs/spring-version-adapter.md
```

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
|   |-- parser
|   |   `-- OutboundResponseParser         # 特殊响应解析扩展点
|   `-- util
|       `-- ResponseTypeUtils              # 泛型响应 JavaType 构建工具
|
|-- inbound                               # 第三方请求我们
|   |-- config
|   |   `-- InboundAppConfig               # appId、公钥、username、tokenTtl、时间窗、nonce 开关
|   |-- registry
|   |   `-- InboundAppConfigRegistry       # 入站配置注册点；一个第三方一个实现
|   |-- model
|   |   |-- TokenApplyRequest              # 获取 token 请求
|   |   |-- TokenSignPayload               # 签名字段
|   |   `-- TokenValue                     # token 存储值
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
        |-- CompositeThirdExchangeLogger   # 组合 slf4j 和项目自定义 logger
        `-- Slf4jThirdExchangeLogger       # slf4j 默认实现
```

## 接入摘要

入站方向，业务项目通常只做这些事：

- 写配置类继承 `AbstractThirdInboundConfiguration`，并在子类加 `@Configuration`。
- 默认使用 Redis 存储 token 和 nonce，项目需要提供 `inboundRedisTemplate()`。
- 如需切换为 Caffeine 本地存储，覆盖 `useInboundRedisStore()` 返回 `false`。
- 每个第三方实现一个 `InboundAppConfigRegistry`。
- 按需覆盖 nonce 开关、拦截路径、token header 名称、拦截器自动注册开关。
- 编写业务 Controller。

默认 token header 为 `X-Third-Token`，参数兼容 `token`。

出站方向，业务项目通常只做这些事：

- 写配置类继承 `AbstractThirdOutboundConfiguration`，并在子类加 `@Configuration`。
- 每个第三方写一个 Client，继承 `AbstractOutboundClient`。
- 实现 `thirdIdentity()`、`buildUrl(String path)`、`buildHeaders()`。
- 第三方参数由项目侧自行读取并组装为 `ThirdClientOptions`。
- 需要日志落库时覆盖 `customThirdExchangeLogger()` 返回项目侧 logger。
- 默认 slf4j 日志会裁剪请求体、响应体、异常信息，长度由配置基类方法 `thirdSlf4jExchangeLogTextMaxLength()` 控制。
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
- 低版本 Spring MVC 适配见 [Spring 低版本适配说明](docs/spring-version-adapter.md)。


019fa7df-c155-77c2-afd1-23a57deafc43