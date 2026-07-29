# 发版流程

## 目标

发版流程需要保证三件事：

- 代码、版本号、CHANGELOG 一致。
- Maven Central 发布顺序正确。
- Git tag 和 Release Notes 可追溯。

## 版本号

当前工程版本格式暂按：

```text
{spring-boot-version}-jdk{jdk-version}-{project-version}
```

示例：

```text
3.5.0-jdk21-3.0.0
```

版本含义：

| 部分 | 说明 |
| --- | --- |
| `3.5.0` | Spring Boot 主线版本 |
| `jdk21` | JDK 主线版本 |
| `3.0.0` | 本项目版本 |

当 Spring Boot 或 JDK 主线变化时，版本号应明确体现。

## CHANGELOG

根 `CHANGELOG.md` 是 Release Notes 的来源。

平时只维护 `Unreleased`：

```md
## Unreleased

### Added
- lcxm-basic-third: 新增第三方入站/出站对接基础模块。

### Changed
- lcxm-basic-framework: 调整 xxx。

### Fixed
- lcxm-basic-core: 修复 xxx。

### Removed
- lcxm-xxx: 移除 xxx。
```

发布时将 `Unreleased` 改为具体版本：

```md
## [3.5.0-jdk21-3.1.0] - 2026-07-29
```

记录原则：

- 只记录对使用者有影响的变化。
- 每条变更建议带模块名前缀。
- 不记录纯格式化、注释调整、无行为影响的内部重构。

## 发布前检查

发布前至少检查：

- 根 `pom.xml` 版本正确。
- 子模块版本继承正常。
- 根 `CHANGELOG.md` 已更新。
- 根 `readme.md` 当前版本、模块列表、文档链接正确。
- 受影响模块的 `readme.md` 和 `docs/` 已同步。
- 不应发布的试验模块已确认处理。

建议执行：

```bash
mvn clean install
```

或按模块执行：

```bash
mvn -pl lcxm-basic-third -am clean install
```

## 修改版本号

统一修改父工程和子模块版本：

```bash
mvn versions:set -DnewVersion="3.5.0-jdk21-3.1.0" -DprocessAllModules=true -DgenerateBackupPoms=false
```

确认版本：

```bash
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```

## Git 提交

建议发版提交只包含与本次发版相关的代码和文档。

```bash
git status
git add .
git commit -m "release: 3.5.0-jdk21-3.1.0"
```

## Git Tag

正式发布 tag 推荐直接使用版本号：

```bash
git tag -a v3.5.0-jdk21-3.1.0 -m "Release 3.5.0-jdk21-3.1.0"
git push origin v3.5.0-jdk21-3.1.0
```

如果是架构拆分、迁移前后保留基线，可以使用语义 tag：

```bash
git tag -a lcxm-basic-pre-split-3.5.0-jdk17-1.0.0 -m "拆分前基线"
git tag -a lcxm-basic-post-split-3.5.0-jdk17-1.1.0 -m "拆分后版本"
```

语义 tag 用于内部迁移定位，不替代正式版本 tag。

## Maven Central 发布

发布使用 `deploy` profile。

父 POM 单独发布：

```bash
mvn deploy -N -Pdeploy
```

基础模块按依赖顺序发布：

```bash
mvn clean deploy -pl lcxm-basic-core,lcxm-basic-excel,lcxm-basic-framework,lcxm-basic-mybatis-plus,lcxm-basic-srpc,lcxm-basic-third -Pdeploy
```

starter 模块发布：

```bash
mvn clean deploy -pl lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter,lcxm-spring-boot-starters/lcxm-mq-data-bridge-spring-boot-starter,lcxm-spring-boot-starters/lcxm-quartz-spring-boot-starter -Pdeploy
```

注意：

- 发布基础模块时不要随意加 `-am`，避免重复部署父 POM。
- `lcxm-spring-boot-starters` 是聚合模块，本身配置了跳过部署，业务使用具体 starter。
- 如遇 SpotBugs 阻塞但本次确认不需要执行，可临时加 `-Dskip.spotbugs=true`，但应在发版记录中说明。

## Release Notes

GitHub / Gitee Release 内容从 `CHANGELOG.md` 当前版本复制。

Release Notes 应包含：

- 版本号。
- 发布时间。
- Added / Changed / Fixed / Removed。
- 兼容性说明。
- 迁移注意事项。

不建议 Release Notes 另写一套内容，避免和 CHANGELOG 不一致。

## 发布后检查

发布后检查：

- Maven Central 是否能搜索到新版本。
- 关键模块依赖是否能正常解析。
- Git tag 是否已推送。
- Release Notes 是否已发布。
- 根 README 中的版本是否仍正确。

可用一个临时项目验证依赖解析：

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-core</artifactId>
    <version>3.5.0-jdk21-3.1.0</version>
</dependency>
```

## 文档维护

发版流程变化时，需要同步更新：

- 本文档。
- 根 `readme.md` 的 Release 部分。
- `docs/documentation-guideline.md` 中的发版文档约定。
- 如命令变化明显，合并或更新 `docs/deploy.md`。
