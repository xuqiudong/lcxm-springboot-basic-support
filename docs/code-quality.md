# 代码质量检测

本文记录个人 Java 项目的代码质量基线，以及 Checkstyle、P3C、SpotBugs 和 SonarQube 等工具的职责与配置原则。

## 基本原则

代码质量工具按职责分层使用，不要求某一个工具覆盖所有问题：

| 层次 | 工具 | 定位 |
| --- | --- | --- |
| 文件格式 | EditorConfig | 统一编码、换行、缩进和行尾空格 |
| 编译检查 | `javac -Xlint` | 检查泛型、废弃 API 等编译期问题 |
| 编码规范 | Checkstyle | 执行统一、可复用的 Java 编码规范 |
| 阿里规约补充 | P3C IDEA 插件 | 补充检查 Checkstyle 不适合表达的阿里规约 |
| 缺陷分析 | SpotBugs | 分析字节码中的潜在程序缺陷 |
| 质量平台 | SonarQube | 汇总质量、安全、重复率、覆盖率和历史趋势 |

当前确定的方向如下：

1. 个人维护的 Java 项目使用同一套基础 Checkstyle 规则。
2. 公共规则参考《阿里巴巴 Java 开发手册》，但不宣称是阿里官方规则或完整实现。
3. 公共规则独立发布为 Maven 规则包，不把整套 XML 内嵌到父 POM。
4. 父 POM 负责统一接入工具、锁定版本并提供可覆盖的配置属性。
5. 子项目默认继承公共规则，只对确有必要的差异进行局部配置。
6. 规则排除必须说明范围和原因，不能把排除文件当作清空告警的手段。

## EditorConfig

`.editorconfig` 是随项目保存的基础文本格式配置，IDEA 原生支持。它适合统一：

- UTF-8 编码。
- LF 换行符。
- 使用空格而不是 Tab。
- 缩进宽度。
- 删除行尾空格。
- 文件末尾保留换行。

EditorConfig 不检查类名、方法名、import、异常处理或潜在缺陷。它负责最基础的文件格式，不能替代 Checkstyle。

## Checkstyle

### 职责

Checkstyle 检查 Java 源码是否符合约定，适合处理：

- 类、方法、字段和参数的命名。
- import 的使用、分组和顺序。
- 大括号、空格、换行和行长度。
- 修饰符顺序。
- `@Override` 的使用。
- `equals()` 与 `hashCode()` 是否配套。
- 一部分可以通过源码结构判断的编码规范。

Checkstyle 主要回答“代码是否符合约定”，不负责代替编译器和 SpotBugs 判断程序是否正确。

### 配置架构

公共规则、父 POM 和业务项目的关系为：

```text
lcxm-checkstyle-rules
  checkstyle.xml
  default-suppressions.xml
          |
          v
lcxm-springboot-parent
  统一插件版本、执行阶段和默认属性
          |
          v
业务项目
  默认继承公共规则
  可覆盖少量属性
  可提供 checkstyle-suppressions.xml
```

`lcxm-checkstyle-rules` 是独立发布、有版本号的 Maven JAR。规则文件作为 JAR 资源发布，因此继承父 POM 的其他仓库不需要通过文件路径访问本仓库。

规则包中的 `default-suppressions.xml` 只保存对所有项目都成立的公共例外。业务项目自己的 `checkstyle-suppressions.xml` 只保存该项目的局部例外，它会补充公共排除，而不是替换公共规则或公共排除。

父 POM 只保存 Maven Checkstyle Plugin 的接入配置、规则包依赖和少量公共属性。这样既能统一执行方式，也不会因为内嵌大量规则而使 POM 难以维护。

规则包和父 POM 应分别版本化：

- 规则包版本表示代码规范本身的变化。
- 父 POM 选择经过验证的规则包版本。
- 业务项目通常跟随父 POM 升级，不直接选择任意规则版本。

### 子项目差异化

子项目按以下优先级处理差异：

1. 覆盖父 POM 预留的属性，例如是否检查测试代码、是否跳过检查。
2. 使用 `checkstyle-suppressions.xml` 精确排除指定规则和文件。
3. 只有项目确实采用另一套规范时，才使用完整的 `checkstyle.xml` 替换公共规则。

三者的含义不同：

| 配置 | 作用 | 是否保留公共规则 |
| --- | --- | --- |
| 属性覆盖 | 调整父 POM 预留的少量行为 | 是 |
| `checkstyle-suppressions.xml` | 在指定范围内关闭某些公共规则 | 是 |
| 完整 `checkstyle.xml` | 定义另一套完整规则 | 否 |

`checkstyle-suppressions.xml` 是豁免清单，不会增加或修改规则。例如：

```xml
<?xml version="1.0"?>
<!DOCTYPE suppressions PUBLIC
        "-//Checkstyle//DTD SuppressionFilter Configuration 1.2//EN"
        "https://checkstyle.org/dtds/suppressions_1_2.dtd">

<suppressions>
    <suppress checks="JavadocType"
              files="[/\\]generated[/\\]"/>
    <suppress checks="MethodName"
              files="LegacyService\.java"/>
</suppressions>
```

公共 `checkstyle.xml` 必须加载公共 suppression，并为可选的项目 suppression 预留入口；父 POM 负责把子项目的 suppression 文件位置传给 Checkstyle。仅在项目中创建文件不会自动生效。具体实现时还要保证没有项目 suppression 文件的项目也能正常执行。

完整覆盖规则会脱离公共基线，应作为少数例外，而不是普通的个性化方式。

### 规则来源

根目录 `plugins.txt` 中的 Checkstyle 配置只能作为整理规则时的参考草案，不能认定为阿里官方配置。当前能确认的情况包括：

- 文件虽引用 Alibaba P3C，但仓库和 Git 历史无法证明其来源。
- 配置包含多个尚未实现的 `TODO`。
- import 分组包含其他项目的 `com.kjlink` 包名。
- `ParameterName ` 模块名末尾存在多余空格。
- Checkstyle 无法表达《阿里巴巴 Java 开发手册》的全部规则。

因此，公共规则的准确定位是：

> 参考《阿里巴巴 Java 开发手册》，结合个人项目实践整理的 Checkstyle 规则。

整理时应逐条确认规则用途、当前 Checkstyle 版本是否支持，以及是否符合现有项目习惯。不能因为配置中写有“符合阿里规范”就直接全部启用。

### IDEA 与 Maven

Maven Checkstyle Plugin 是构建和 CI 的统一执行入口，IDEA 的 CheckStyle-IDEA 插件用于开发阶段提前发现问题。两者应尽量使用同一份 `checkstyle.xml` 和兼容的 Checkstyle 版本。

IDEA 自带的 Sun Checks 和 Google Checks 是另外两套风格，不是本项目的规则来源。启用自定义规则后，不再把它们作为项目检查标准。

## P3C

P3C 是 Alibaba Java Coding Guidelines 的配套检查工具。它不仅检查排版，还覆盖集合、并发、异常、数据库和常见 Java 误用等规约，其中一些规则无法由 Checkstyle 表达。

本项目在 2024-06-03 移除了 P3C Maven 集成，POM 记录的原因是当时官方 `p3c-pmd` Wiki 仍标注 TODO。仓库历史没有更详细的决定依据，因此不补充未经证实的原因。

当前定位是：

- 不把 P3C Maven 插件作为统一构建门禁。
- 可以使用 P3C IDEA 插件进行手动补充检查。
- Checkstyle 负责稳定、明确且适合自动执行的公共编码规范。

## SpotBugs

SpotBugs 分析编译后的 Java 字节码，适合发现空指针、错误比较、内部可变对象泄露、序列化和异常处理等潜在缺陷。

当前父 POM 已启用 SpotBugs。处理告警时按以下顺序进行：

1. 判断是否为真实缺陷，能修复代码时优先修复。
2. 单个代码点的明确误报使用 `@SuppressFBWarnings`，并填写 `justification`。
3. 生成代码、框架约束或一组稳定误报使用 `spotbugs-exclude.xml`。
4. 只有整个检测器确实不适用时，才考虑 `omitVisitors`。

仅创建 `spotbugs-exclude.xml` 不会生效，Maven 插件必须通过 `excludeFilterFile` 引用它。排除应同时限定类、方法和具体 Bug Pattern，避免按整个项目或整个问题类别大范围忽略。

示例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<FindBugsFilter>
    <Match>
        <Class name="cn.xuqiudong.basic.example.ExampleService"/>
        <Method name="load"/>
        <Bug pattern="NP_NULL_ON_SOME_PATH"/>
    </Match>
</FindBugsFilter>
```

源码使用 `@SuppressFBWarnings` 时，项目依赖中还需要提供 `spotbugs-annotations`。Maven 插件自身的依赖不能供业务源码编译使用。

## SonarQube

SonarQube 是集中式代码质量平台，用于汇总 Bug、安全问题、重复代码、复杂度、覆盖率、质量门禁和历史趋势。Maven 中的 Sonar 插件负责扫描和上传，并不等于本地部署了 SonarQube Server。

当前项目没有统一的 SonarQube Server、项目标识、认证和覆盖率流程，因此暂不接入父 POM。将来具备统一 CI 和服务端环境后，再明确以下内容：

- 哪些检查作为 Quality Gate。
- Checkstyle 和 SpotBugs 的报告是否导入 SonarQube。
- 本地构建与 CI 各自负责哪些门禁。
- Token 等凭据如何通过 CI Secret 提供。

## 父 POM 清理计划

接入 Checkstyle 前先整理父 POM。清理过程逐项确认，每次只处理已经确认的项目，并在修改后执行相应的 Maven 验证。

状态说明：

- `待确认`：尚未决定是否修改。
- `已确认`：已经决定方案，等待实施。
- `已完成`：已经修改并通过验证。
- `保留`：经过确认，不作修改。

| 编号 | 检查项 | 当前情况 | 建议 | 状态 |
| --- | --- | --- | --- | --- |
| POM-01 | 测试默认策略 | 父 POM 通过 `skip.test=false` 默认执行测试 | 需要时由命令或特定场景显式跳过 | 已完成 |
| POM-02 | 发布时 SpotBugs | `deploy` profile 设置 `skip.spotbugs=true` | 发布前由开发者单独完成检测，发布阶段继续跳过以控制耗时 | 保留 |
| POM-03 | ClassFinal 公共配置 | 父 POM 曾包含默认密码、业务包名和旧版本 JAR 名称 | 已删除这组仅用于测试的父 POM 配置；如有需要，在实际使用项目中单独配置 | 已完成 |
| POM-04 | 无效和历史配置 | 存在未使用的 `skip.pmd`、已移除 P3C 的注释及注释掉的旧配置 | 已删除无效属性、P3C 历史注释和注释掉的旧配置 | 已完成 |
| POM-05 | Java 编译配置 | 同时配置 `java.version`、`source`、`target` 和 `release` | 已统一使用 `java.version` 和编译插件的 `release` | 已完成 |
| POM-06 | 编码属性 | 同时存在四个 UTF-8 相关属性 | 已保留 Maven 通用的构建和报告编码属性，编译插件直接引用构建编码 | 已完成 |
| POM-07 | 未使用的依赖版本管理 | 父 POM 同时管理当前模块和子项目可能使用的版本 | 已删除确认废弃的 Velocity、Commons Lang 2.x 和 Commons Collections 3.x；其他版本管理继续作为子项目约束保留 | 已完成 |
| POM-08 | SCM 与项目地址 | SCM 地址的组织名不一致，仓库名通过 `${project.name}` 间接生成 | 已固定父项目仓库名并修正 Gitee SSH、HTTPS 和 Issue 地址；子项目可覆盖仓库名 | 已完成 |
| POM-09 | SpotBugs 注解依赖 | 插件依赖中声明的注解不能供源码编译使用，部分模块存在重复声明 | 已在父 POM 普通依赖中以 `provided` 统一提供，并删除插件和子模块中的重复声明 | 已完成 |
| POM-10 | 注释、空行和命名 | 存在重复说明、失效注释、多余空行及 `odbc.version` 命名问题 | 已精简注释和空行，将属性修正为 `ojdbc.version`，未改变构建行为 | 已完成 |

处理每一项时应记录最终决定。涉及默认测试、发布检查、依赖兼容或发布信息的项目，必须先确认行为影响；纯格式整理也应单独提交，避免与功能变更混在一起。

## 落地顺序

后续按以下顺序实施：

1. 逐项完成父 POM 清理计划。
2. 从 `plugins.txt` 提取候选规则，逐条审核并形成首版公共规则。
3. 建立并发布 `lcxm-checkstyle-rules`。
4. 在 `lcxm-springboot-parent` 中接入规则包和 Maven Checkstyle Plugin。
5. 为子项目定义统一的 suppression 文件约定和可覆盖属性。
6. 扫描现有代码，区分真实问题、历史兼容和误报。
7. 规则稳定后再决定绑定的 Maven 生命周期阶段及失败级别。
8. 配置 IDEA 使用同一份规则，作为编码阶段的即时反馈。

任何规则和排除项都应能够回答三个问题：检查什么、为什么需要、为什么可以例外。
