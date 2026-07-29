# lcxm-basic-framework

Spring / Spring Boot / Web 通用能力模块。

`lcxm-basic-framework` 位于 `lcxm-basic-core` 之上，面向使用 Spring 框架的项目，提供 Web、AOP、全局异常、缓存、Jackson、选择项、Code2Text、运行时授权等基础能力。

不使用 Spring / Spring Boot 的项目不建议依赖本模块。

## 主包 Tree

```text
cn.xuqiudong.basic.framework
|-- aspect                 # 请求日志、防重复提交、串行请求等切面
|-- authentication         # 权限注解、权限模型、拦截器、Thymeleaf 权限标签
|-- cache                  # 缓存 key、Redis/Jedis 工具
|-- captcha                # 验证码相关能力
|-- code2text              # code -> text 序列化增强、Resolver、缓存
|-- condition              # Spring 条件注解和条件判断
|-- constant               # framework 常量
|-- controller             # 通用 controller 支撑
|-- env                    # 环境变量、外部配置、配置解密处理
|-- exception              # framework 层异常
|-- handler                # JSON、XSS、Thymeleaf 等处理器
|-- jackson                # Jackson 自动配置和反序列化扩展
|-- listener               # 启动监听、WebServer 监听
|-- runtime                # 轻量运行时授权校验
|-- select                 # 枚举/业务选择项注册、查询、序列化
|-- service                # 通用 service 接口
|-- statistics             # 项目信息统计
|-- tool                   # Spring 上下文工具
|-- util                   # Web、Cookie、配置等工具
`-- web                    # 全局异常、过滤器、拦截器、trace
```

## 主要能力

| 能力 | 说明 |
| --- | --- |
| 全局异常处理 | `GlobalExceptionHandler` 统一处理常见 Web 异常。 |
| Web 拦截/过滤 | trace id、请求保存、安全跳转、Referer 校验、Basic Authorization 等。 |
| AOP 增强 | 请求日志、防重复提交、串行请求等。 |
| Code2Text | JSON 序列化阶段自动完成 code -> text 扩展字段。 |
| Select | 枚举选择项、业务选择项注册和查询。 |
| Jackson 扩展 | XSS 字符串处理、枚举容错反序列化、字段追加序列化等。 |
| 缓存支持 | Caffeine / Redis 相关基础能力和 Code2Text 缓存。 |
| 环境处理 | 外部配置加载、配置解密、环境属性处理。 |
| 权限辅助 | 权限注解、权限模型、权限拦截器、Thymeleaf 权限标签。 |
| 运行时授权 | 轻量 license 读取、验签和过期校验。 |

## Maven

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-framework</artifactId>
    <version>3.5.0-jdk21-3.0.0</version>
</dependency>
```

## 依赖边界

适合放入 `lcxm-basic-framework`：

- Spring / Spring Boot / Web 通用能力。
- 通用切面、过滤器、拦截器。
- Jackson、环境、缓存、选择项等通用框架能力。
- 可被多个 Spring 项目复用的轻量扩展。

不适合放入 `lcxm-basic-framework`：

- MyBatis-Plus 专项能力。
- Excel 导入导出实现。
- 第三方对接协议。
- MQ 数据桥接。
- Quartz 任务模型。
- 代码生成器。
- 具体业务项目配置和业务逻辑。

专项能力应放入对应模块或 starter。

## 文档

| 文档 | 说明 |
| --- | --- |
| [docs/README.md](docs/README.md) | framework 模块文档索引。 |
| [docs/design.md](docs/design.md) | framework 设计边界、自动配置和主要流程。 |
| [docs/code2text.md](docs/code2text.md) | Code2Text 设计和使用说明。 |
| [docs/license.md](docs/license.md) | 轻量运行时授权校验说明。 |

## 注意点

- 本模块会引入 Spring Boot、Spring Web、Redis、Caffeine、SpringDoc、验证码等运行时能力；业务项目应确认确实需要 framework 后再依赖。
- Redisson、Thymeleaf、Jedis 等部分能力是可选或场景化能力，具体项目按需补充配置。
- 不应为了短期复用把专项能力继续放入 framework。
- 自动配置和开箱即用能力如果变重，优先考虑下沉到具体 starter。
