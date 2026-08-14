# 父 POM 依赖与插件升级记录

> 日期：2026-08-14  
> 环境：JDK 21.0.10 / Maven 3.9.9  
> 范围：仅根 `lcxm-springboot-parent`，不进入子模块

## 升级目标

- 将 Spring Boot 3.5.0 升级到当前 3.5 补丁线，获得累计缺陷和安全修复。
- 保持 Spring Boot 3 / Spring Framework 6.2 / Jakarta EE 10 / JDK 21 兼容边界。
- 移除对 Spring Boot BOM 已管理组件的不必要覆盖。
- 将早于 JDK 21 的 Maven 插件升级到 Spring Boot 3.5.16 使用的稳定版本。

## Spring 与中间件

| 依赖或 BOM | 升级前 | 升级后 | 说明 |
| --- | --- | --- | --- |
| Spring Boot Dependencies | 3.5.0 | 3.5.16 | 保持 3.5 补丁线 |
| SpringDoc BOM | 2.8.8 | 2.9.0 | 2.x 对应 Spring Boot 3；不升级到面向 Boot 4 的 3.x |
| Redisson Spring Boot Starter | 3.50.0 | 3.52.0 | 保持 3.x 兼容线 |
| MyBatis Spring Boot Starter | 3.0.4 | 3.0.5 | 保持 Boot 3 兼容线 |
| MyBatis-Plus BOM | 3.5.14 | 3.5.14 | 3.5.17 存在 API 不兼容，本次保持原版本；JSqlParser 继续使用 5.2 |
| PageHelper Spring Boot Starter | 2.1.0 | 2.1.1 | 不跨到面向新主线的 4.x |
| Sa-Token Boot 3 Starter | 1.43.0 | 1.45.0 | 保持 Boot 3 starter |

## 通用依赖

| 依赖 | 升级前 | 升级后 |
| --- | --- | --- |
| Hutool | 5.8.38 | 5.8.47 |
| Commons Validator | 1.7 | 1.11.0 |
| Commons FileUpload | 1.3.1 | 1.6.0 |
| Commons IO | 2.4 | 2.22.0 |
| Commons Text | 1.9 | 1.15.0 |
| Commons Collections 4 | 4.1 | 4.6.0 |
| Commons Configuration 2 | 2.7 | 2.15.1 |
| Commons BeanUtils | 1.9.4 | 1.11.0 |
| Apache HttpClient 4 | 4.5.13 | 4.5.14 |
| Lombok | 1.18.38 | 1.18.46 |

Apache HttpClient 4.5.14 是 4.x 维护线的最终版本。本次只做兼容升级，后续可单独评估迁移到 HttpClient 5。

## 改由 Boot BOM 管理

以下依赖不再由父 POM 重复声明，版本直接由导入的 Spring Boot BOM 管理：

| 依赖 | 升级前显式版本 | Boot 3.5.16 管理版本 | 处理原因 |
| --- | --- | --- | --- |
| Jakarta Servlet API | 6.1.0 | 6.0.0 | 与 Boot 3.5 / Tomcat 10.1 的 Jakarta EE 10 组合保持一致 |
| Caffeine | 3.2.0 | 3.2.4 | 跟随 Boot 测试组合 |
| Commons Codec | 1.9 | 1.18.0 | 删除严重过旧的降级覆盖 |
| Commons Lang 3 | 3.17.0 | 3.17.0 | 版本相同，删除重复定义 |
| FreeMarker | 2.3.34 | 2.3.34 | 版本相同，删除重复定义 |

## Maven 插件

| 插件 | 升级前 | 升级后 | 版本来源 |
| --- | --- | --- | --- |
| Spring Boot Maven Plugin | 3.5.0 | 3.5.16 | 跟随 Spring Boot |
| Maven Compiler Plugin | 3.9.0 | 3.14.1 | Spring Boot 3.5.16 |
| Maven Surefire Plugin | 2.22.2 | 3.5.6 | Spring Boot 3.5.16 |
| Maven Javadoc Plugin | 3.4.0 | 3.11.3 | Spring Boot 3.5.16 |
| Maven Jar Plugin | 3.2.2 | 3.4.2 | Spring Boot 3.5.16 |
| Maven Source Plugin | 3.2.1 | 3.3.1 | Spring Boot 3.5.16 |
| Maven Resources Plugin | 3.1.0 | 3.3.1 | Spring Boot 3.5.16 |
| SpotBugs Maven Plugin | 4.9.8.2 | 4.10.3.0 | Maven Central 稳定版 |
| Central Publishing Maven Plugin | 0.4.0 | 0.11.0 | Maven Central 稳定版 |
| Maven GPG Plugin | 1.5 | 3.2.8 | Maven Central 稳定版 |

SpotBugs 核心版本仍为 4.9.8，仅升级 Maven 插件本身。

## 清理项

删除了已由 Boot BOM 接管或未被插件配置使用的版本属性：

- `caffeine.version`
- `jakarta.servlet`
- `javax.servlet`
- `validation.version`
- `commons.lang3.version`
- `commons.codec.version`
- `freemarker.version`
- `maven-release-plugin.version`
- `maven-pmd-plugin.version`
- `p3c-pmd.version`

同时删除了未被父项目、profile 或子模块引用的 Flatten Maven Plugin 配置。该插件原本只存在于 `pluginManagement`，不会实际执行。

子模块中还清理了两处无有效行为的插件声明：

- generator starter 中只包含注释的 Maven Jar Plugin 空配置。
- starter 聚合 POM 中与 `maven.deploy.skip=true` 重复的 Maven Deploy Plugin 配置。

## 暂未处理

以下项目涉及依赖替换、运行环境或下游兼容性，不与本次补丁升级混合处理：

- `commons-collections:commons-collections:3.2.2`、`commons-lang:commons-lang:2.6` 和 Velocity 1.7 等旧 API 的移除或替换。
- `ojdbc8` 迁移到适合 JDK 21 的 `ojdbc11` 新版本。
- Apache HttpClient 4 迁移到 HttpClient 5。
- MyBatis-Plus 3.5.17 API 适配。Reactor 编译确认其 `SqlHelper.executeBatch` 回调和 `SqlMethod` API 与当前模块代码不兼容。
- 父项目版本 `3.5.0-jdk21-3.0.0` 的发布版本调整；正式发布时需按工程版本规则统一处理。
- 子模块测试和运行时兼容性验证。

当前仓库内没有模块或源码引用以下依赖管理项，可在确认外部继承项目未使用后，于下一主版本删除：

- `com.meilisearch.sdk:meilisearch-java`
- `commons-validator:commons-validator`
- `commons-fileupload:commons-fileupload`
- `commons-lang:commons-lang`
- `commons-collections:commons-collections`
- `org.apache.velocity:velocity`

## Reactor 构建修正

`lcxm-generator-spring-boot-starter` 和 `lcxm-mq-data-bridge-spring-boot-starter` 位于两级子目录，原 POM 未指定父 POM 的 `relativePath`。Maven 因默认的 `../pom.xml` 坐标不匹配而回退到本地仓库中的旧父 POM，导致两个模块仍使用旧版构建插件。

两个模块均已补充：

```xml
<relativePath>../../pom.xml</relativePath>
```

## 验证结果

父 POM 验证命令：

```bash
mvn -N validate
mvn -N -Dskip.spotbugs=true verify
```

两条命令均为 `BUILD SUCCESS`。`verify` 已实际解析并运行父级构建生命周期中的新版插件；父 POM 没有业务代码，因此未执行模块测试。

依赖升级后又执行了完整 reactor 编译：

```bash
mvn compile
```

12 个 reactor 项目全部 `BUILD SUCCESS`。编译包含新版 Compiler、Resources 和 SpotBugs 插件；SpotBugs 报告的现有 Medium 级问题未达到父 POM 配置的失败阈值。测试和运行时启动验证不在本次范围内。
