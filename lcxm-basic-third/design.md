# lcxm-basic-third 设计说明

## 边界

公共模块负责：

- 出站请求模型、执行器、响应解析、交换日志扩展点。
- 入站 token 签发、RSA 验签、token 校验、token 注销。
- Spring MVC 默认 controller、interceptor、配置基类。
- Redis/Caffeine 两类基础存储实现。

业务项目负责：

- 第三方参数来源。
- RedisTemplate、Caffeine 或其他存储选择。
- 出站厂商 Client 的具体 URL、header、参数、业务方法。
- 入站 appId、公钥、用户名白名单、TTL、时间窗配置。
- 业务 Controller、拦截路径、异常响应格式、日志落库。

## 关键结论

| 点 | 结论 |
| --- | --- |
| Spring 形态 | 当前主线面向 Spring Boot 3 / Spring 6 / JDK 21 |
| 配置基类 | 提供两个抽象配置基类，不加 `@Configuration`，项目继承类自己加 |
| Web 鉴权 | 使用 Spring MVC `HandlerInterceptor`，不使用 servlet filter |
| token 存储 | 抽象 `TokenStore`，内置 Redis 和 Caffeine |
| token 过期 | 默认 1 小时，可按 appId 配置 |
| token 传递 | 默认 header `X-Third-Token`，参数兼容 `token` |
| nonce | 可选，默认关闭 |
| RSA/HTTP | 使用 Hutool |
| 出站业务 | 只抽公共请求骨架，不沉淀具体厂商参数 |

## 配置基类

```text
AbstractThirdInboundConfiguration
  -> inboundTokenStore()                  # @Bean；按 useInboundRedisStore() 选择 Redis/Caffeine
  -> inboundNonceStore()                  # @Bean；按 useInboundRedisStore() 选择 Redis/Caffeine
  -> useInboundRedisStore()               # 默认 true；返回 false 使用 Caffeine 本地存储
  -> inboundRedisTemplate()               # 抽象方法；项目侧提供 RedisTemplate
  -> inboundTokenService(...)             # 聚合 InboundAppConfigRegistry + TokenStore + NonceStore
  -> inboundTokenController(...)
  -> inboundTokenInterceptor(...)
  -> inboundTokenWebMvcConfigurer(...)    # 提供 WebMvcConfigurer；内部按开关注册拦截器
  -> registerInboundTokenInterceptor()    # 默认 true；XML 手动注册时返回 false
  -> inboundTokenHeaderName()             # 默认 X-Third-Token
  -> inboundInterceptPathPatterns()
  -> inboundExcludePathPatterns()

AbstractThirdOutboundConfiguration
  -> thirdOptionsProvider()               # @Bean；委托 thirdClientOptions(thirdCode)
  -> thirdClientOptions(thirdCode)        # 默认返回 defaultThirdClientOptions()
  -> defaultThirdClientOptions()
  -> thirdExchangeLogger()                # @Bean；委托 recordThirdExchangeLog(log)
  -> recordThirdExchangeLog(log)          # 先按开关写 slf4j，再调用 saveThirdExchangeLog(log)
  -> printThirdExchangeLog()              # 默认 true
  -> saveThirdExchangeLog(log)            # 默认不处理；落库、MQ、审计时覆盖这里
  -> thirdOutboundExecutor(...)           # 创建 Hutool HTTP 执行器
```

配置基类不做自动配置。项目接入时显式继承，显式加 `@Configuration`，按需覆盖方法。

## ConditionalOnMissingBean

当前模块不使用 `@ConditionalOnMissingBean`。

原因：

- 当前是基础模块，不是 Spring Boot starter。
- 配置类需要项目显式继承，不会自动进入 Spring 容器。
- `@ConditionalOnMissingBean` 来自 Spring Boot autoconfigure，会增加 Boot 自动配置语义。
- 普通 Spring MVC 项目也可能接入，不应强绑定 Boot 条件注解。

如果后续拆出 `lcxm-basic-third-spring-boot-starter`，`@ConditionalOnMissingBean` 应放在 starter 的自动配置类中，而不是放在当前抽象配置基类中。

## 入站流程

```text
1. 第三方申请 token
   TokenApplyRequest(appId/timestamp/nonce/username/sign)
   -> InboundTokenController.obtainToken

2. 查询业务配置
   InboundTokenService
   -> 启动时聚合多个 InboundAppConfigRegistry
   -> 按 appId 查询 InboundAppConfig

3. 验签
   SignaturePayloadBuilder
   -> appId={appId}&timestamp={timestamp}&nonce={nonce}&username={username}
   RsaSignatureUtils
   -> SHA256withRSA 公钥验签

4. 签发 token
   InboundTokenService
   -> Hutool IdUtil.simpleUUID()
   -> TokenStore.put(token, TokenValue, ttl)

5. 访问业务接口
   InboundTokenInterceptor
   -> 从 header X-Third-Token 或 parameter token 读取
   -> InboundTokenService.checkToken
   -> 通过后进入业务 Controller

6. 注销 token
   InboundTokenController.revokeToken
   -> InboundTokenService.revokeToken
   -> TokenStore.remove
```

## 出站流程

```text
1. 项目配置类继承 AbstractThirdOutboundConfiguration
   -> 提供 ThirdOptionsProvider / ThirdExchangeLogger

2. 业务方编写某厂商 Client
   -> 继承 AbstractOutboundClient
   -> 实现 thirdIdentity/buildUrl/buildHeaders

3. 业务方法构建请求
   -> OutboundRequestInfoBuilder
   -> 设置 method/query/form/body/responseType

4. 执行请求
   -> HttpOutboundExecutor
   -> Hutool HTTP

5. 解析响应
   -> ObjectMapper 默认解析
   -> 特殊格式走 OutboundResponseParser

6. 记录日志
   -> ThirdExchangeLogger
```

## NonceStore

`NonceStore` 不是必须。

它只处理一个问题：在签名时间窗内阻止同一 `appId + nonce` 重复申请 token。

建议：

- 默认使用 Redis：项目提供 `inboundRedisTemplate()`。
- 对重放敏感：开启 `InboundAppConfig.nonceRequired=true`。
- 单机/测试：覆盖 `useInboundRedisStore()` 返回 `false`，使用 Caffeine 本地存储。
