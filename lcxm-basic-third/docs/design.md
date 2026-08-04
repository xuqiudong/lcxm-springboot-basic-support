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
| 出站厂商基类 | `AbstractOutboundPartner<C, A>` 负责 host、具体 API 枚举、配置短缓存、常用请求封装 |
| 出站高级入口 | `builder(api, responseType)` 负责复杂请求构建，最后 `execute(api, request)` |
| 普通参数构建 | `createParams()` 返回 `OutboundParams`，只处理 query/form 普通键值参数 |
| 出站响应钩子 | `execute(api, request)` 中的 `api` 用于 `afterResponse(api, response)` |
| 出站请求类型 | `OutboundRequestType` 明确 JSON/FORM/MULTIPART/TEXT/BYTES/QUERY/NONE，默认可推断 |
| multipart 上传 | 使用 `MultipartPart` 列表表达字段，支持 `File`、`InputStream + fileName`、同名 field 多文件 |
| 出站响应 | 提供 JavaType 构建工具，不强制统一厂商响应基类 |
| 出站日志 | `HttpOutboundExecutor` 直接执行默认 slf4j 和项目自定义 logger；请求体、响应体、异常信息默认裁剪 |

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
  -> printOutboundExchangeLog()              # 默认 true
  -> outboundSlf4jExchangeLogTextMaxLength() # 默认 4000；只影响 slf4j 打印，不影响日志模型
  -> customOutboundExchangeLogger()          # 默认 null；落库、MQ、审计时返回项目侧 logger
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
   -> TokenStore.put(token, TokenValue, ttlSeconds)

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
   -> 配置 slf4j 打印开关和项目自定义 OutboundExchangeLogger
   -> 具体第三方 Client 声明时注入 OutboundExecutor

2. 业务方定义厂商 API 和配置
   -> enum XxxApi implements ThirdApi
   -> XxxConfig implements OutboundPartnerConfig
   -> 配置来源由项目自行处理，可来自配置文件/DB/Redis/配置中心

3. 业务方编写某厂商 Client
   -> 继承 AbstractOutboundPartner<XxxConfig, XxxApi>
   -> 实现 thirdIdentity
   -> 实现 loadConfig
   -> 默认 Caffeine 缓存配置 5 分钟；按需覆盖 configCacheTtl
   -> 按需覆盖 resolveApiPath/buildHeaders/afterResponse

4. 业务方法构建请求
   -> 简单请求使用 requestJson/requestForm/requestMultipart 等终结方法
   -> 参数较多但结构单一时，使用 createParams() 组织 query/form 普通参数
   -> 复杂请求使用 OutboundRequestInfoBuilder
   -> 设置 method/query/form/jsonBody/textBody/bytesBody/multipartFile/responseType
   -> OutboundRequestType 显式指定或自动推断

5. 执行请求
   -> HttpOutboundExecutor
   -> Hutool HTTP
   -> 按 OutboundRequestType 补 Content-Type/Accept 并写入请求体

6. 解析响应
   -> ObjectMapper 默认解析
   -> ResponseTypeUtils 构建包装泛型 JavaType
   -> 特殊格式走 OutboundResponseParser

7. 记录日志
   -> HttpOutboundExecutor 生成完整 OutboundExchangeLog
   -> Slf4jOutboundExchangeLogger 按配置裁剪打印内容
   -> 项目自定义 OutboundExchangeLogger 获取完整模型，自行决定是否裁剪入库
```

## NonceStore

`NonceStore` 不是必须。

它只处理一个问题：在签名时间窗内阻止同一 `appId + nonce` 重复申请 token。

建议：

- 默认使用 Redis：项目提供 `inboundRedisTemplate()`。
- 对重放敏感：开启 `InboundAppConfig.nonceRequired=true`。
- 单机/测试：覆盖 `useInboundRedisStore()` 返回 `false`，使用 Caffeine 本地存储。
