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
| 查看可编译 demo | 仓库源码中的 `src/test/java/cn/xuqiudong/basic/third/demo` |

推荐阅读顺序：

```text
readme.md
  -> docs/design.md
  -> 按方向选择：
       入站：docs/开发流程-接入我方.md
       出站：docs/开发流程-调用他方.md
  -> 对外提供：docs/第三方入站接口文档.md
  -> 代码参考：仓库源码中的 src/test/java/cn/xuqiudong/basic/third/demo
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
|   |-- api
|   |   `-- ThirdApi                      # 厂商 API 枚举接口：apiName/path/method/successCode
|   |-- config
|   |   `-- OutboundPartnerConfig         # 厂商出站配置基类：host、通用 HTTP options
|   |-- client
|   |   |-- AbstractOutboundClient         # 出站 HTTP 执行底座
|   |   `-- AbstractOutboundPartner        # 厂商 Client 基类：配置、host、具体 API 枚举、常用 request 封装和 builder 高级入口
|   |-- builder
|   |   |-- OutboundRequestInfoBuilder     # 请求构建器
|   |   `-- OutboundParams                 # 普通 query/form 参数构建器
|   |-- executor
|   |   |-- OutboundExecutor               # HTTP 执行器接口
|   |   `-- HttpOutboundExecutor           # Hutool HTTP 默认实现
|   |-- model
|   |   |-- OutboundRequestInfo            # 出站请求模型
|   |   |-- OutboundRequestType            # JSON/FORM/MULTIPART/TEXT/BYTES/QUERY/NONE
|   |   |-- MultipartPart                  # multipart 显式字段模型，支持同名 field 多文件
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

## Demo

模块提供可编译 demo，放在 `src/test/java` 下，只作为仓库源码参考，不进入正式 jar。
业务项目只通过 Maven 依赖 `lcxm-basic-third` 时，看不到这些 demo 类。

```text
src/test/java/cn/xuqiudong/basic/third/demo
|-- inbound
|   |-- config
|   |   `-- DemoThirdInboundConfiguration     # 入站方向全局配置：store、token controller、interceptor
|   `-- partner                               # 入站厂商实现
|       `-- demo
|           |-- config
|           |   `-- DemoInboundAppConfigRegistry  # demo 第三方 appId、公钥、用户、TTL 配置
|           `-- controller
|               `-- DemoInboundBusinessController # demo 第三方请求我方的业务接口
`-- outbound
    |-- config
    |   `-- DemoThirdOutboundConfiguration    # 出站方向全局配置：logger、client bean 装配
    `-- partner                              # 出站厂商实现
        `-- demo
            |-- client
            |   |-- DemoOutboundClient        # demo 第三方出站 Client
            |   `-- DemoOutboundClientTest    # demo Client 直接运行测试
            |-- config
            |   `-- DemoOutboundConfig        # demo 第三方出站配置模型
            |-- enums
            |   `-- DemoApi                   # demo 第三方 API 枚举
            `-- model
                |-- DemoRequest
                |-- DemoResponse
                `-- DemoThirdResponse         # demo 第三方统一响应包装
```

说明：

- `inbound/config`、`outbound/config` 是方向级全局配置。
- `inbound/partner/demo`、`outbound/partner/demo` 是某个具体第三方实现。
- 方向内确实有多个 partner 共享的对象时，再增加 `common`；没有共享逻辑不要建空包。

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
- 每个第三方定义一个 API 枚举，实现 `ThirdApi`。
- 每个第三方定义一个配置模型，实现 `OutboundPartnerConfig`，至少提供 `host`。
- 每个第三方写一个 Client，优先继承 `AbstractOutboundPartner<C, A>`，其中 `A` 是具体 API 枚举。
- 实现 `thirdIdentity()` 和 `loadConfig()`，按需覆盖 `resolveApiPath(...)`、`buildHeaders(api)`、`afterResponse(...)`。
- `loadConfig()` 的返回结果默认使用 Caffeine 缓存 5 分钟；覆盖 `configCacheTtl()` 可调整，返回小于等于 0 表示不缓存。
- 简单请求可用 `requestJson(...)`、`requestForm(...)`、`requestMultipart(...)` 等终结方法。
- 参数较多但结构单一时，可用 `createParams()` 组织普通 query/form 参数，再传给终结方法或 builder。
- 需要混合 query/header/form/multipart/parser/timeout 时，从 `builder(api, responseType)` 开始构建请求，最后 `execute(api, request)`。
- `execute(api, request)` 中的 `api` 用于执行后的 `afterResponse(api, response)` 厂商响应校验。
- 文件上传使用 `requestMultipart(...)`，支持 `File`、`InputStream + fileName`；同一个 field 多个文件使用 builder 连续 `multipartFile("file", ...)` 或 `multipartFiles(...)`。
- 需要日志落库时覆盖 `customThirdExchangeLogger()` 返回项目侧 logger。
- 默认 slf4j 日志会裁剪请求体、响应体、异常信息，长度由配置基类方法 `thirdSlf4jExchangeLogTextMaxLength()` 控制。
- JSON/FORM/TEXT/BYTES 请求通过 `OutboundRequestType` 明确表达，builder 也会按 body/form/query 自动推断。
- 需要特殊响应解析时使用 `OutboundResponseParser`；包装泛型响应使用 `ResponseTypeUtils`。

## Token 过期

旧模块固定 60 分钟：`TOKEN_EXPIRE_SECONDS = 3600L`。

当前模块默认仍是 1 小时，但不是写死策略：

- 默认值：`InboundAppConfig.tokenTtl = Duration.ofHours(1)`。
- 每个 `appId` 可以通过 `InboundAppConfigRegistry` 返回不同 TTL。
- Redis 版本由 Redis TTL 控制过期；Caffeine 版本通过 `TokenValue.expireAt` 校验过期。
- Store 接口 TTL 使用秒值 `ttlSeconds`，兼容低版本 Spring Data Redis。

## 依赖策略

- 依赖 `lcxm-basic-core`，不依赖 `lcxm-basic-framework`。
- RSA、HTTP、Date、UUID 优先使用 Hutool。
- `spring-webmvc`、`spring-data-redis` 为 optional。
- 当前主线面向 Spring Boot 3 / Spring 6 / JDK 21，使用 `jakarta.servlet`。
- 低版本 Spring MVC 适配见 [Spring 低版本适配说明](docs/spring-version-adapter.md)。
