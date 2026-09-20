# 代码质量检测规范

本文只记录当前项目已经确定的代码质量工具、父 POM 默认行为和子项目使用方式。

## 目录

- [统一原则](#统一原则)
- [工具职责](#工具职责)
- [Checkstyle](#checkstyle)
- [子项目例外](#子项目例外)
- [EditorConfig 与 IDEA](#editorconfig-与-idea)
- [SpotBugs](#spotbugs)
- [P3C](#p3c)
- [SonarQube](#sonarqube)

## 统一原则

- 所有继承 `lcxm-springboot-parent` 的项目使用同一套 Checkstyle 公共规则。
- 公共规则内嵌在父 POM 的 `checkstyleRules` 中，不依赖父仓库中的外部 XML 文件。
- 子项目不重新定义整套规则；确有必要时，只使用 suppression 声明局部例外。
- 规则以《阿里巴巴 Java 开发手册》为主要参考，结合项目实际情况整理，不宣称是阿里官方完整实现。
- 代码格式化由 `.editorconfig` 和 `config/idea/lcxm-idea-style.xml` 负责，Checkstyle 负责构建阶段的最终检查。
- 规则发现的问题应优先修改代码；排除规则必须限定范围并说明原因。

## 工具职责

| 工具 | 主要职责 | 当前定位 |
| --- | --- | --- |
| EditorConfig | 编码、换行、缩进、行尾空格 | 基础文件格式 |
| IDEA Code Style | IDEA 自动格式化和 import 优化 | 开发阶段格式化 |
| Checkstyle | Java 源码规范和结构检查 | Maven/CI 统一门禁 |
| SpotBugs | 基于字节码发现潜在缺陷 | Maven 静态缺陷分析 |
| P3C | 补充阿里规约中的语义和实践性检查 | IDEA 手动检查 |
| SonarQube | 汇总质量、安全、重复率、覆盖率和趋势 | 有统一服务端后再接入 |

## Checkstyle

### 父 POM 默认行为

父 POM 中的 `maven-checkstyle-plugin`：

- 版本由 `maven-checkstyle-plugin.version` 统一管理。
- 使用 `checkstyleRules` 内嵌公共规则。
- 在 `validate` 阶段执行。
- 同时检查 `main` 和 `test` 源码。
- 当前 `checkstyle.failOnViolation=false`，只报告存量问题，不阻断构建。
- 当前 `checkstyle.failsOnError=false`，规则配置或执行异常暂不阻断初期构建。
- 使用 `skip.checkstyle=true` 可以临时跳过检查。
- 规则稳定并完成存量问题治理后，再将失败属性改为 `true`。

父 POM中的规则主要包括：

- 类、方法、字段、参数、局部变量、常量和包名命名。
- 抽象类使用 `Abstract` 或 `Base` 前缀。
- `long` 字面量使用大写 `L`。
- 禁止 Tab，文件末尾保留换行，单行长度不超过 120 个字符。
- 大括号、括号、运算符和分隔符空格。
- `@Override`、修饰符顺序以及 `equals/hashCode` 配套检查。
- 普通和静态 import 均禁止使用 `*`。
- 禁止非法、重复和未使用 import。
- import 按第三方、`javax`、`java`、静态 import 分组并排序。
- 空 `catch` 仅允许使用 `expected` 或 `ignore` 表示明确忽略。
- 字符串字面量比较禁止使用 `==` 或 `!=`。

Checkstyle 不能可靠替代编译器、SpotBugs 或 P3C 来判断空指针、并发、数据库、事务和业务语义问题。

### 子项目例外

子项目不应覆盖完整 `checkstyleRules`。需要局部例外时，在子项目根目录创建：

```text
checkstyle-suppressions.xml
```

父 POM已经通过可选的 `SuppressionFilter` 引用该文件。没有该文件时，全部公共规则照常执行。

示例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suppressions PUBLIC
        "-//Checkstyle//DTD SuppressionFilter Configuration 1.2//EN"
        "https://checkstyle.org/dtds/suppressions_1_2.dtd">

<suppressions>
    <!-- 自动生成代码不检查行长度。 -->
    <suppress files="[/\\]generated[/\\]"
              checks="LineLength"/>

    <!-- 历史兼容类暂时保留旧方法名。 -->
    <suppress files="LegacyService\.java"
              checks="MethodName"/>
</suppressions>
```

排除规则的要求：

- 优先限定具体文件、目录和检查项。
- 不使用全局 `.*` 关闭一类规则。
- 必须写注释说明排除原因。
- 能修改源码时优先修改源码，不新增 suppression。

#### 代码内关闭行长度检查的预留方案

当前尚未启用代码内关闭规则的注释。以后遇到长正则表达式、SQL、JSON、HTML 或固定协议内容，确实不适合拆分时，可以在父 POM 中引入 `SuppressWithPlainTextCommentFilter`，并仅允许使用以下标记关闭 `LineLength`：

```java
// 内容由外部协议规定，拆分会降低可读性。
// CHECKSTYLE.LINE_LENGTH: OFF
String content = "确实不适合拆分的固定内容";
// CHECKSTYLE.LINE_LENGTH: ON
```

启用该方案时必须将过滤器的 `checkFormat` 限定为 `LineLength`。标记范围内只忽略行长度，其他 Checkstyle 规则继续执行。

不开放通用的 `CHECKSTYLE: OFF` 和 `CHECKSTYLE: ON`，避免整段代码绕过全部检查。关闭标记必须成对出现，范围应尽可能小，并在标记前说明不能正常换行的原因。

只有确实采用另一套代码规范的项目，才允许通过插件配置完全替换公共规则；这不属于普通项目的差异化方式。

### import 约束

当前不设置“超过多少个类才允许 `*`”的阈值。Checkstyle 直接禁止普通和静态星号 import。

IDEA中应将以下设置设为较大值，例如 `999`：

- `Class count to use import with '*'`
- `Names count to use static import with '*'`

同时清空 `Packages to Use Import with '*'`，避免 IDEA 自动格式化后生成 Maven 会拒绝的星号 import。

## EditorConfig 与 IDEA

仓库提供两份彼此配套的格式配置：

- `.editorconfig`：放入仓库即可生效，负责跨编辑器和文件级基础格式。
- `config/idea/lcxm-idea-style.xml`：可导入 IDEA，负责本机所有项目的完整 Java Code Style。

两份配置中重复的 Java 规则必须保持一致。项目存在 `.editorconfig` 时，其中已声明的选项优先于 IDEA Scheme 的对应选项。

根目录的 `.editorconfig` 统一：

- UTF-8。
- LF 换行。
- 文件末尾换行。
- 删除行尾空格。
- Java 使用空格缩进，缩进宽度为 4。
- Java 续行缩进为 8。
- Java 单行最多 120 个字符。
- IDEA 格式化时，对超长的参数列表、方法调用、链式调用、表达式、赋值、`for` 语句和注解参数按需换行。
- IDEA 不自动生成星号 import。
- import 按第三方、`javax`、`java`、静态 import 分组。

`.editorconfig` 只能在文件目录层级生效，不能通过 Maven Parent 继承。正式团队仓库建议复制该文件，以保证换电脑、换 IDE 或其他开发者检出后仍有一致的基础格式。

个人在 IDEA 中开发多个独立项目时，可以导入：

```text
config/idea/lcxm-idea-style.xml
```

IDEA 2025.3.4 的导入入口：

```text
Settings
→ Editor
→ Code Style
→ 齿轮图标
→ Import Scheme
→ IntelliJ IDEA code style XML
```

导入后选择 `LCXM` Scheme。这样，没有 `.editorconfig` 的独立项目也会使用 LCXM 的 Java 格式和 import 规则。特殊项目可以添加自己的 `.editorconfig`，只覆盖确实不同的选项。

IDEA Scheme 不负责完整约束 UTF-8、LF、文件末尾换行和删除行尾空格，因此它不能完全替代 `.editorconfig`。无论采用哪种格式配置，Maven Checkstyle 都是最终检查标准。

IDEA 2025.3 默认支持 EditorConfig。确认项目启用了 EditorConfig 后，执行 `Reformat Code` 可按 120 字符右边界拆分常见的 Java 代码结构，执行 `Optimize Imports` 可整理 import。长字符串、注释以及无法安全拆分的表达式仍需人工处理，因此 IDEA 格式化不能替代 Checkstyle。

import 设置可在以下位置核对：

```text
Settings
→ Editor
→ Code Style
→ Java
→ Imports
```

Maven 工具窗口执行 `validate` 时，如果中文消息乱码，在 IDEA 的：

```text
Settings
→ Build, Execution, Deployment
→ Build Tools
→ Maven
→ Runner
```

的 `VM Options` 中配置：

```text
-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8
```

## SpotBugs

SpotBugs 分析编译后的字节码，主要发现潜在程序缺陷，不负责排版和命名。

父 POM默认行为：

- 在 `compile` 阶段执行。
- 报告 `Medium` 及以上级别问题。
- `High` 级别问题使构建失败。
- 发布 profile 中通过 `skip.spotbugs=true` 跳过发布阶段检查。
- `spotbugs-annotations` 以 `provided` 依赖提供给子项目源码使用。

单个明确误报优先使用：

```java
@SuppressFBWarnings(
        value = "NP_NULL_ON_SOME_PATH",
        justification = "调用方保证该路径已完成初始化")
```

一组稳定的生成代码或框架误报可以使用 `spotbugs-exclude.xml`，但必须在插件配置中通过 `excludeFilterFile` 显式引用；仅创建文件不会自动生效。

## P3C

P3C 是 Alibaba Java Coding Guidelines 的配套检查工具，能补充集合、并发、异常、数据库和常见 Java 误用等语义规则。

当前不把 P3C Maven 插件作为父 POM的统一构建门禁。需要时可以在 IDEA 中使用 P3C 插件进行手动检查。Checkstyle 负责稳定、明确、适合统一构建执行的源码规范，P3C负责补充更偏语义的阿里规约。

## SonarQube

SonarQube 是集中式代码质量平台，可以汇总 Bug、安全问题、重复代码、复杂度、测试覆盖率、质量门禁和历史趋势。

当前项目没有统一的 SonarQube Server、项目标识、认证和 CI 流程，因此暂不接入父 POM。以后具备统一服务端和 CI 环境后，再确定质量门禁、报告导入和凭据管理方式。
