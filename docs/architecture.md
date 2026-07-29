# 架构说明

## 项目定位

`lcxm-springboot-basic-support` 是面向 Spring Boot 项目的基础能力集合。

它不是单一业务框架，而是一组可按需依赖的模块：

- 底层通用能力放在 `lcxm-basic-core`。
- Spring/Web 相关能力放在 `lcxm-basic-framework`。
- 数据访问、Excel、第三方对接、RPC 等能力按领域拆成独立模块。
- 自动配置和开箱即用能力放在 `lcxm-spring-boot-starters`。

## 模块分层

```text
业务项目
  |
  |-- 按需依赖 basic 模块
  |-- 按需依赖 starter 模块
  |
lcxm-spring-boot-starters
  |-- lcxm-quartz-spring-boot-starter
  |-- lcxm-generator-spring-boot-starter
  `-- lcxm-mq-data-bridge-spring-boot-starter
  |
领域基础模块
  |-- lcxm-basic-framework
  |-- lcxm-basic-mybatis-plus
  |-- lcxm-basic-excel
  |-- lcxm-basic-third
  `-- lcxm-basic-srpc
  |
lcxm-basic-core
```

依赖方向原则：

```text
starter -> basic domain module -> framework/core -> core
```

不允许反向依赖：

```text
core 不依赖 framework
core 不依赖 mybatis-plus
framework 不依赖 third
framework 不依赖具体 starter
basic 模块不依赖业务项目
```

## 模块职责

| Module | 职责 | 依赖建议 |
| --- | --- | --- |
| `lcxm-basic-core` | 通用模型、异常、工具、JSON、校验、反射、线程等基础能力。 | 通常可以作为最底层依赖。 |
| `lcxm-basic-framework` | Spring / Spring Boot / Web 通用增强，包括 AOP、全局异常、Web 支撑、缓存、字典/枚举选择等。 | Spring Web 项目按需依赖。 |
| `lcxm-basic-mybatis-plus` | MyBatis-Plus 增强，包括实体基类、通用 Mapper/Service、自动填充、字段级数据权限等。 | 使用 MyBatis-Plus 的项目依赖。 |
| `lcxm-basic-excel` | Excel 读取、写入工具，基于 FastExcel / POI。 | 有 Excel 导入导出需求时依赖。 |
| `lcxm-basic-third` | 第三方入站/出站对接基础模块。 | 有第三方对接需求时依赖，不放入 framework。 |
| `lcxm-basic-srpc` | 自定义 Simple RPC 注解、代理、序列化和协议通信。 | 使用该 RPC 体系的项目依赖。 |
| `lcxm-spring-boot-starters` | starter 聚合模块，本身不作为业务依赖。 | 业务项目依赖具体 starter。 |
| `lcxm-trial` | 试验、验证和样例模块。 | 不建议业务项目依赖。 |

## Starter 与 Basic 模块

`basic` 模块提供可复用能力。

`starter` 模块负责把一组能力包装成 Spring Boot 自动配置。

```text
lcxm-basic-xxx
  -> 提供模型、接口、工具、默认实现

lcxm-xxx-spring-boot-starter
  -> 提供自动配置、配置属性、条件装配、开箱即用入口
```

设计约束：

- 能按需手动配置的公共能力优先放在 `basic` 模块。
- 自动配置语义放在 `starter` 模块。
- `@ConditionalOnMissingBean` 等 Boot 自动配置逻辑优先放在 starter。
- 不为了方便把所有能力塞进 `lcxm-basic-framework`。

## 依赖选择

业务项目应按功能选择依赖。

只需要通用模型和工具：

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-core</artifactId>
</dependency>
```

需要 Spring Web 通用能力：

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-framework</artifactId>
</dependency>
```

需要 MyBatis-Plus 扩展：

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-mybatis-plus</artifactId>
</dependency>
```

需要第三方对接：

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-third</artifactId>
</dependency>
```

不建议业务项目直接依赖聚合模块：

```text
lcxm-springboot-parent
lcxm-spring-boot-starters
lcxm-trial
```

## 当前技术基线

当前主线：

- JDK 21
- Spring Boot 3.5.x
- Spring Framework 6.x
- Jakarta EE / `jakarta.servlet`
- MyBatis-Plus 3.5.x
- Hutool 5.8.x

低版本 Spring Boot 2.x / JDK 8 不在当前主线中直接兼容。

如果需要低版本适配，建议新建适配分支或适配模块，不建议在主线代码中同时兼容多套 Spring/Servlet API。

## 设计约束

- 公共模块只沉淀稳定机制，不沉淀具体业务项目参数。
- 模块边界优先于短期复用便利。
- 依赖向下流动，避免循环依赖和大而全模块。
- Web、Redis、MyBatis、MQ 等有明显运行时成本的依赖应按模块隔离。
- 可选能力优先通过接口、抽象配置类或 starter 自动配置扩展。
- 文档跟随公开能力维护，不记录无行为影响的内部细节。

## 文档维护

当以下内容变化时，需要同步更新本文档：

- 新增或删除模块。
- 模块职责发生变化。
- 模块依赖方向发生变化。
- JDK / Spring Boot / Servlet API 主线版本变化。
- basic 模块和 starter 模块的边界变化。

模块内部实现细节变化优先更新对应模块 `readme.md` 或模块 `docs/`。
