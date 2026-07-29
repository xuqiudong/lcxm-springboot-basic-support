# Changelog

本文件记录对使用者有影响的变化。

格式参考 Keep a Changelog，版本号遵循当前工程约定：

```text
{spring-boot-version}-jdk{jdk-version}-{project-version}
```

## Unreleased

### Added

- `lcxm-basic-third`: 新增第三方入站/出站对接基础模块，包含 token 签发校验、Spring MVC 拦截器、Hutool HTTP 出站执行器、交换日志扩展点和接入文档。
- `docs`: 新增项目级文档规范、架构说明、依赖与版本策略、发版流程。

### Changed

- `readme.md`: 重写为项目级产品入口，补充模块列表、Quick Start、文档导航、版本和发版说明。
- `lcxm-basic-third`: 简化第三方对接设计，删除无必要 provider/result/noop 结构，按入站、出站、日志、配置等子包重新组织。

## [3.5.0-jdk21-3.0.0] - 2026-04-25

### Changed

- 升级当前主线到 JDK 21。
- 根 POM 统一维护 Spring Boot 3.5.x、JDK 21、Jakarta EE 相关依赖和插件版本。

## [3.5.0-jdk17-2.0.0] - 2025-09-08

### Changed

- 拆分原通用模块，形成更清晰的按需依赖结构。
- 新增/整理 `lcxm-basic-core`、`lcxm-basic-framework`、`lcxm-basic-mybatis-plus`、`lcxm-basic-srpc` 等基础模块。
- 业务项目可按功能依赖具体模块，减少大而全依赖。

## [3.5.0-jdk17-1.0.0] - 2025-05-27

### Changed

- 从 Spring Boot 2.7.3 / JDK 8 迁移到 Spring Boot 3.5.x / JDK 17 主线。
- 从 `javax.servlet` 迁移到 `jakarta.servlet`。
- 升级 MyBatis、PageHelper、SpringDoc、Redisson、Caffeine、POI 等主要依赖。
- Spring Boot starter 自动装配文件迁移到 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`。

详细迁移记录见 [docs/CHANGELOG/202505-springboot3.5_jdk17.md](docs/CHANGELOG/202505-springboot3.5_jdk17.md)。

## [1.0.0] - 2026-06-06

### Changed

- `lcxm-basic-framework`: 将 Code2Text 从“子注解 + SPI 分类体系”简化为“单注解 + type 策略模式”，保留子项目覆写和动态扩展能力。

> 原历史版本号记录为 `[1,0.0] 20260-6-06`，本次仅规范格式；具体版本归属后续发版前可再校准。
