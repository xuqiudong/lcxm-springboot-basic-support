# lcxm-springboot-basic-support

[![Maven Central](https://img.shields.io/maven-central/v/cn.xuqiudong.basic/lcxm-springboot-parent.svg)](https://central.sonatype.com/artifact/cn.xuqiudong.basic/lcxm-springboot-parent)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

`lcxm-springboot-basic-support` 是一组面向 Spring Boot 项目的可复用基础能力模块，提供核心工具、Web 基础框架、MyBatis-Plus 扩展、Excel 工具、第三方对接、RPC 和常用 Spring Boot Starter。

当前主线：

- JDK 21
- Spring Boot 3.5.x
- Spring Framework 6.x
- Jakarta EE / `jakarta.servlet`

## Features

- 基础模型、异常、统一响应、常用工具。
- Spring / Spring Boot / Web 通用增强。
- MyBatis-Plus 通用实体、Mapper、Service 和数据权限扩展。
- Excel 读取、写入基础能力。
- 第三方入站/出站对接基础模块。
- Quartz 定时任务、代码生成器、MQ 数据桥接等 starter。
- Maven 父 POM 统一管理版本、插件和发布配置。

## Modules

| Module | Description |
| --- | --- |
| `lcxm-basic-core` | 基础核心模块，提供通用模型、异常、工具、JSON、校验等能力。 |
| `lcxm-basic-framework` | Spring / Spring Boot / Web 通用能力，包含 AOP、全局异常、Web 支撑、缓存等。 |
| `lcxm-basic-mybatis-plus` | MyBatis-Plus 增强，包含实体基类、通用 Mapper/Service、字段级数据权限等。 |
| `lcxm-basic-excel` | Excel 读取、写入工具，基于 FastExcel / POI。 |
| `lcxm-basic-third` | 第三方对接基础模块，覆盖第三方请求我方和我方请求第三方。 |
| `lcxm-basic-srpc` | 自定义 Simple RPC 基础能力，包含注解、代理、序列化和协议通信。 |
| `lcxm-spring-boot-starters` | Spring Boot starter 聚合模块。 |
| `lcxm-trial` | 试验、验证和样例模块，不建议业务项目直接依赖。 |

Starter 子模块：

| Starter | Description |
| --- | --- |
| `lcxm-quartz-spring-boot-starter` | 基于 Quartz 的定时任务 starter。 |
| `lcxm-generator-spring-boot-starter` | 基于 MyBatis-Plus / 数据库结构的代码生成器 starter。 |
| `lcxm-mq-data-bridge-spring-boot-starter` | 基于 RabbitMQ 的系统间数据顺序交互 starter。 |

## Quick Start

按需依赖具体模块，不建议业务项目直接依赖所有模块。

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-core</artifactId>
    <version>3.5.0-jdk21-3.0.0</version>
</dependency>
```

例如使用第三方对接模块：

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-third</artifactId>
    <version>3.5.0-jdk21-3.0.0</version>
</dependency>
```

本地构建：

```bash
mvn clean install
```

只构建某个模块及其依赖：

```bash
mvn -pl lcxm-basic-third -am clean install
```

## Documentation

| 你要做什么 | 文档 |
| --- | --- |
| 了解项目整体架构和模块边界 | [docs/architecture.md](docs/architecture.md) |
| 查看依赖与版本策略 | [docs/dependency.md](docs/dependency.md) |
| 查看发版流程 | [docs/release.md](docs/release.md) |
| 查看文档编写规范 | [docs/documentation-guideline.md](docs/documentation-guideline.md) |
| 查看项目级文档目录 | [docs/README.md](docs/README.md) |
| 查看版本变化 | [CHANGELOG.md](CHANGELOG.md) |
| 查看部署说明 | [docs/deploy.md](docs/deploy.md) |
| 查看基础规范 | [docs/specification.md](docs/specification.md) |
| 使用 Spring/Web 基础框架模块 | [lcxm-basic-framework/readme.md](lcxm-basic-framework/readme.md) |
| 使用第三方对接模块 | [lcxm-basic-third/readme.md](lcxm-basic-third/readme.md) |
| 使用 MyBatis-Plus 扩展 | [lcxm-basic-mybatis-plus/readme.md](lcxm-basic-mybatis-plus/readme.md) |
| 使用 Excel 工具模块 | [lcxm-basic-excel/readme.md](lcxm-basic-excel/readme.md) |
| 使用 Simple RPC 模块 | [lcxm-basic-srpc/readme.md](lcxm-basic-srpc/readme.md) |
| 使用 Quartz starter | [lcxm-spring-boot-starters/lcxm-quartz-spring-boot-starter/readme.md](lcxm-spring-boot-starters/lcxm-quartz-spring-boot-starter/readme.md) |
| 使用代码生成器 starter | [lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter/readme.md](lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter/readme.md) |
| 使用 MQ 数据桥接 starter | [lcxm-spring-boot-starters/lcxm-mq-data-bridge-spring-boot-starter/readme.md](lcxm-spring-boot-starters/lcxm-mq-data-bridge-spring-boot-starter/readme.md) |

## Version And Release

当前版本：`3.5.0-jdk21-3.0.0`

版本命名暂按当前工程约定：

```text
{spring-boot-version}-jdk{jdk-version}-{project-version}
```

发布前建议检查：

- 更新根 `CHANGELOG.md` 的当前版本。
- 确认受影响模块的 `readme.md` 和 `docs/` 已同步。
- 执行 Maven 构建和必要测试。
- 创建 Git tag。
- 发布 Maven Central。
- 使用本版本 CHANGELOG 生成 Release Notes。

## Changelog

所有对使用者有影响的变更记录在 [CHANGELOG.md](CHANGELOG.md)。

CHANGELOG 只记录公开能力、依赖、行为、兼容性和 bug 修复，不记录无行为影响的内部细节调整。

## License

本项目使用 [Apache License 2.0](LICENSE)。
