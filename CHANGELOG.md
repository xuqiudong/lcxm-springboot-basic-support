# Changelog

本文件记录对使用者有影响的变化。

格式参考 Keep a Changelog。自 `4.0.0` 起，版本号遵循 Semantic Versioning 2.0.0；
Spring Boot 和 JDK 兼容信息单独记录，不编码在 Maven artifact version 中。

## Unreleased

### Added
 - lcxm-basic-mybatis-plus: 新增固定 ID 类型父类Entity,解决批量保存时因泛型丢失找不到TypeHandler

## [4.0.0] - 2026-09-04

> 下一次计划发布版本：`4.0.0`。发布前状态为“待发布”；确认所有目标 artifact
> 已同步到 Maven Central 后，将本节改为具体版本并标注“已发布至 Maven Central”。

### Added

- `lcxm-basic-third`: 新增第三方入站/出站对接基础模块，包含 token 签发校验、Spring MVC 拦截器、Hutool HTTP 出站执行器、交换日志扩展点和接入文档。
- `lcxm-basic-secure-id`: 新增 Secure ID 基础组件，支持响应 ID 加密和请求参数、路径参数解密。
- `docs`: 新增项目级文档规范、架构说明、依赖与版本策略、发版流程。

### Changed

- `readme.md`: 重写为项目级产品入口，补充模块列表、Quick Start、文档导航、版本和发版说明。
- `lcxm-basic-third`: 简化第三方对接设计，删除无必要 provider/result/noop 结构，按入站、出站、日志、配置等子包重新组织。
-  **2026-08-14 依赖版本升级**:详细版本和兼容性边界见 [父 POM 依赖与插件升级记录](docs/CHANGELOG/202608-parent-dependency-plugin-upgrade.md)。
  - `lcxm-springboot-parent`: Spring Boot 3.5 补丁线升级至 3.5.16，并同步升级 SpringDoc、Redisson、MyBatis、PageHelper、Sa-Token 等 Boot 3 兼容线依赖。
  - `lcxm-springboot-parent`: 升级 Commons、Hutool、HttpClient 4 及 Maven 编译、测试、打包、发布相关插件；Servlet API、Caffeine、Commons Codec/Lang3、FreeMarker 改为跟随 Spring Boot BOM。
  - `lcxm-springboot-parent`: 删除未被父项目、profile 或子模块引用的 Flatten Maven Plugin 配置。
  - `lcxm-spring-boot-starters`: 修正两级子目录 starter 的父 POM `relativePath`，避免构建时回退到本地仓库中的旧父 POM。
  - `lcxm-spring-boot-starters`: 删除 generator 的空 Jar Plugin 声明和聚合 POM 中与 `maven.deploy.skip` 重复的 Deploy Plugin 配置。

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
