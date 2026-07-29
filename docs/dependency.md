# 依赖与版本策略

## 当前主线

当前主线以根 `pom.xml` 为准：

| 项 | 当前版本 |
| --- | --- |
| JDK | 21 |
| Spring Boot | 3.5.x |
| Spring Framework | 6.x |
| Servlet API | `jakarta.servlet` |
| MyBatis-Plus | 3.5.x |
| Hutool | 5.8.x |

主线不直接兼容 JDK 8 / Spring Boot 2.x / `javax.servlet`。

需要低版本支持时，优先新增适配分支或适配模块，不建议在主线里同时维护两套 Spring/Servlet API。

## 版本管理

根 `lcxm-springboot-parent` 负责统一管理：

- JDK 编译版本。
- Spring Boot BOM。
- MyBatis-Plus BOM。
- 常用第三方依赖版本。
- Maven 编译、测试、源码包、Javadoc、发布相关插件。

业务模块通常不单独指定公共依赖版本，除非该依赖不在父 POM 管理范围内，或模块确实需要固定特殊版本。

## 模块依赖方向

依赖方向应保持单向：

```text
starter -> basic domain module -> framework/core -> core
```

核心约束：

- `lcxm-basic-core` 尽量保持底层通用，不依赖业务模块。
- `lcxm-basic-framework` 可以依赖 `lcxm-basic-core`，不依赖 `third`、`starter` 等专项模块。
- `lcxm-basic-mybatis-plus`、`lcxm-basic-excel`、`lcxm-basic-third`、`lcxm-basic-srpc` 按领域独立。
- `lcxm-spring-boot-starters` 只聚合 starter，业务项目依赖具体 starter。
- `lcxm-trial` 只用于试验和验证，不作为正式依赖。

## optional / provided / runtime

### optional

以下情况优先使用 `optional=true`：

- 模块只提供扩展支持，不希望把实现强传递给业务项目。
- 能力只有部分项目需要，例如 Redisson、Thymeleaf、某些 Web UI 能力。
- 基础模块需要暴露接口或适配点，但运行时由业务项目自行选择实现。

示例：

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <optional>true</optional>
</dependency>
```

### provided

以下情况优先使用 `provided`：

- Servlet API。
- 编译期需要、运行时由容器或业务项目提供的能力。

示例：

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <scope>provided</scope>
</dependency>
```

### runtime

以下情况优先使用 `runtime`：

- 数据库驱动。
- 编译期不需要，运行时由具体部署环境决定的依赖。

## Spring Boot Starter 依赖

starter 模块可以引入明确的 Spring Boot starter，并提供自动配置。

basic 模块应谨慎引入 Spring Boot starter：

- `lcxm-basic-framework` 是 Spring/Web 基础框架模块，可以引入 Spring Boot / Web 相关依赖。
- `lcxm-basic-mybatis-plus` 可以引入 MyBatis-Plus starter，因为它的职责就是 MP 增强。
- `lcxm-basic-third` 当前只依赖 `spring-webmvc`、`spring-data-redis` 等 optional 能力，不绑定 Spring Boot 自动配置。
- 自动装配、条件装配、配置属性优先放到 starter，而不是 basic 模块。

## Core 模块依赖边界

`lcxm-basic-core` 是最底层模块，应尽量避免放入这些依赖：

- Web MVC 运行时能力。
- Redis / MQ / 数据库访问实现。
- MyBatis-Plus 注解和插件。
- 第三方业务对接逻辑。
- 具体 starter 自动配置。

可以放入：

- 通用模型。
- 通用异常。
- 通用工具。
- JSON、字符串、日期、集合、反射、线程等基础能力。
- 少量通用注解和轻量接口。

如果某能力依赖明显的运行时环境，应放到更具体的模块。

## Framework 模块依赖边界

`lcxm-basic-framework` 面向 Spring/Web 项目。

适合放入：

- 全局异常处理。
- Web 拦截器、过滤器、Controller 基础能力。
- AOP 切面。
- Jackson、环境、缓存、字典/枚举选择等 Spring 通用能力。

不适合放入：

- 第三方对接。
- MyBatis-Plus 专项能力。
- MQ 数据桥接。
- Quartz 任务业务模型。
- 代码生成器。

专项能力应继续保持独立模块，业务项目按需依赖。

## 低版本适配

当前主线使用：

```text
JDK 21
Spring Boot 3.x
Spring Framework 6.x
jakarta.servlet
```

迁移到 JDK 8 / Spring Boot 2.x / Spring MVC 4.x 时，需要重点处理：

- `jakarta.servlet` 改为 `javax.servlet`。
- Spring Boot 3.x 依赖降为 Spring Boot 2.x 或 Spring 4/5 对应依赖。
- 选择仍支持 JDK 8 的 Hutool、Jackson、Caffeine、Lombok、MyBatis-Plus 等版本。
- Java 9+ API，例如 `Map.of`、`List.of`、`InputStream.readAllBytes`，需要替换。
- Java 11+ API，例如 `String.isBlank`，需要替换为 Hutool `StrUtil.isBlank`。
- Java 16+ 语法，例如 `instanceof Xxx x`，需要改为普通写法。

如果低版本项目较多，建议新增：

```text
lcxm-basic-xxx-springmvc4-adapter
lcxm-basic-xxx-jdk8-adapter
```

不要在主线代码中堆叠大量兼容判断。

## 新增依赖规则

新增依赖前需要确认：

- 是否已有同类能力。
- 是否会传递大量运行时依赖。
- 是否会污染 `core` 或 `framework` 的边界。
- 是否应该设为 `optional` 或 `provided`。
- 是否需要放到父 POM 统一管理版本。
- 是否影响 JDK / Spring Boot 主线。

原则：

- 公共依赖版本放父 POM。
- 专项依赖放专项模块。
- 运行时成本明显的依赖不进入 `core`。
- 非所有项目都需要的能力不进入 `framework`。
