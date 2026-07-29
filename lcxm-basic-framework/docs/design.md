# lcxm-basic-framework 设计说明

## 边界

`lcxm-basic-framework` 是 Spring / Spring Boot / Web 通用能力模块。

它负责：

- Spring Boot 自动配置入口。
- Web 全局异常、过滤器、拦截器。
- Jackson 序列化/反序列化扩展。
- AOP 通用增强。
- 枚举/业务选择项。
- Code2Text。
- 缓存、环境处理、运行时授权等框架能力。

它不负责：

- MyBatis-Plus 专项能力。
- Excel 导入导出。
- 第三方入站/出站协议。
- MQ 数据桥接。
- Quartz 任务模型。
- 代码生成器。
- 具体业务项目配置和业务逻辑。

## 启动入口

```text
Spring Boot
  -> META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
  -> FrameworkAutoConfiguration
      -> EnumSelectAutoConfiguration
      -> BusinessSelectAutoConfiguration
      -> LcxmJacksonAutoConfiguration
      -> Code2TextAutoConfiguration
```

同时保留 `spring.factories` 注册较早期的 Spring Boot 扩展点：

```text
spring.factories
|-- ApplicationListener
|   `-- WebServerListener
|-- SpringApplicationRunListener
|   |-- SpringBootApplicationPropertiesUtil
|   `-- StartupTimeListener
`-- EnvironmentPostProcessor
    |-- LcxmCommonPropertiesPostProcessor
    |-- DecryptEnvironmentPostProcessor
    `-- ExternalConfigProcessor
```

## 总开关

framework 总开关：

```properties
lcxm.framework.enabled=true
```

`FrameworkAutoConfiguration` 使用：

```text
@ConditionalOnProperty(prefix = "lcxm.framework", name = "enabled", havingValue = "true", matchIfMissing = true)
```

默认启用。

## 自动配置结构

```text
FrameworkAutoConfiguration
|-- ApplicationContextHolder
|-- RequestLoggerFilter
|-- EnumSelectAutoConfiguration
|-- BusinessSelectAutoConfiguration
|-- LcxmJacksonAutoConfiguration
`-- Code2TextAutoConfiguration
```

主要子开关：

| 能力 | 配置 |
| --- | --- |
| 枚举选择项 | `lcxm.framework.enum.select.enabled` |
| 业务选择项 | `lcxm.framework.business.select.enabled` |
| Jackson 扩展 | `lcxm.framework.jackson.enabled` |
| Code2Text | `lcxm.framework.code2text.enabled` |

以上能力默认启用，项目可按需关闭。

## Web 流程

```text
HTTP Request
  -> Filter
      -> RequestLoggerFilter
      -> SafetyRefererFilter / SafelyRedirectFilter / BasicAuthorizationFilter 等按项目注册
  -> HandlerInterceptor
      -> LogTraceIdInterceptor
      -> SaveRequestInterceptor
      -> PermissionCodeInterceptor 等按项目注册
  -> Controller
  -> GlobalExceptionHandler
  -> Jackson Serialization
```

说明：

- `GlobalExceptionHandler` 同时兼容 JSON 和页面错误响应。
- trace id 由 `LogTraceIdInterceptor` 创建和清理。
- 安全跳转、Referer、Basic Authorization 等过滤器属于可选能力，项目按需注册。

## Jackson 扩展

`LcxmJacksonAutoConfiguration` 默认处理：

- `LocalDateTime`、`LocalDate`、`LocalTime` 格式化。
- 关闭日期时间 timestamp 输出。
- 设置默认时区为 `Asia/Shanghai`。
- 枚举反序列化时允许空字符串。

```text
ObjectMapper
  -> Jackson2ObjectMapperBuilderCustomizer
      -> date/time serializer
      -> date/time deserializer
      -> NullableEnumDeserializer
```

## Select 流程

### 枚举选择项

```text
启动
  -> EnumSelectScanner
  -> 扫描配置包下的枚举
  -> 注册到 EnumSelectRegistry

请求
  -> EnumSelectController
  -> 返回 SelectOption

序列化
  -> EnumSelectableSerializer
  -> 追加 text 字段
```

枚举扫描包配置：

```properties
lcxm.framework.enum.scan-base-packages=cn.xuqiudong.demo
```

### 业务选择项

```text
业务项目
  -> 实现 BusinessSelectProvider
  -> BusinessSelectFacade 聚合 provider
  -> BusinessSelectController 对外提供查询入口
```

业务选择项适合处理数据库、远程接口或业务规则生成的下拉选项。

## Code2Text 流程

Code2Text 目标：在 JSON 序列化阶段自动完成 code -> text。

```text
Controller 返回对象
  -> Jackson 序列化
  -> Code2TextSerializer
  -> Code2TextResolverRegistry
  -> CachedResolverProxy
  -> Code2TextCacheManager
  -> CacheRegion(Caffeine / Redis / Composite)
  -> Code2TextResolver
  -> 追加 xxxText 字段
```

缓存失效：

```text
业务数据变化
  -> Code2TextCacheHelper
  -> Spring Event
  -> 本地缓存清理
  -> Redis Message
  -> 其他节点清理本地缓存
```

缓存预热：

```text
应用启动
  -> Code2TextPreloadRunner
  -> Code2TextPreloadable Resolver
  -> Code2TextCacheManager
  -> CacheRegion
```

详细说明见 [code2text.md](code2text.md)。

## 运行时授权

`runtime` 包提供轻量 license 校验能力：

```text
classpath:license
  -> LcRuntimeHelper
  -> Base62 解码
  -> LcPayload
  -> RSA 公钥验签
  -> RuntimeGuard
```

该能力适合内部项目、简单商业授权、过期提醒等场景，不属于强防破解方案。

详细说明见 [license.md](license.md)。

## 扩展原则

- 新增 Spring/Web 通用能力，可以放入 framework。
- 明确绑定专项领域的能力，应放入专项模块。
- 能力变重且需要自动装配时，优先考虑 starter。
- 自动配置 Bean 应优先使用 `@ConditionalOnMissingBean`，便于项目覆盖。
- 模块文档只记录公开能力、边界和接入流程，不记录无行为影响的内部细节。

## 维护约定

当以下内容变化时，需要同步更新本文档：

- 自动配置入口变化。
- `lcxm.framework.*` 配置开关变化。
- Web/Jackson/Select/Code2Text 主要流程变化。
- framework 与专项模块边界变化。
- 公开扩展点变化。
