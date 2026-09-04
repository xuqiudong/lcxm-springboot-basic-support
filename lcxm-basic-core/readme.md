# lcxm-basic-core

基础核心模块，提供通用模型、异常、统一响应、常用工具、JSON、校验、反射、线程等能力。

`lcxm-basic-core` 是本项目的底层模块。它不承载具体业务能力，也不承载 MyBatis-Plus、第三方对接、MQ、Excel 等专项能力。

## 主包 Tree

```text
cn.xuqiudong.basic.core
|-- annotation              # 通用注解
|-- base                    # 基础类型
|-- context                 # 轻量上下文
|-- craw                    # 简单抓取/连接工具
|-- enums                   # 通用结果枚举
|-- exception               # 通用运行时异常
|-- lookup                  # lookup 基础模型
|-- model                   # BaseResponse、BaseEntity、PageInfo、SelectOption 等模型
|-- request                 # 通用请求接口和请求模型
|-- util                    # 字符串、日期、JSON、加密、集合、反射、SQL、线程等工具
`-- vo                      # 通用 VO
```

## 主要能力

| 能力 | 说明 |
| --- | --- |
| 统一响应 | `BaseResponse`、`ResultMsg`、`CommonMsgEnum`。 |
| 通用异常 | `CommonException`、`BadParamException`、`UnauthorizedException`。 |
| 基础模型 | `BaseEntity`、`PageInfo`、`SelectOption`、`Remind`、`Lookup`。 |
| 请求模型 | `BaseApiRequest`、`CheckNotRepeatRequest`。 |
| JSON 工具 | Jackson 相关通用封装。 |
| 加密/编码 | AES、RSA、Base62、摘要、编码工具。 |
| 集合/差异计算 | 集合差异、实体关系差异计算。 |
| 反射/比较 | 反射工具、对象字段比较工具。 |
| 线程工具 | 批量执行、线程工厂等。 |
| 校验工具 | Hibernate Validator 辅助工具。 |

## 依赖边界

适合放入 `lcxm-basic-core`：

- 跨模块复用的模型、异常、枚举。
- 不绑定具体业务场景的工具类。
- 轻量、稳定、下层通用的基础能力。

不适合放入 `lcxm-basic-core`：

- Spring Web 业务组件。
- MyBatis-Plus 注解、Mapper、插件。
- Redis / MQ / 数据库访问实现。
- Excel 导入导出实现。
- 第三方对接协议和业务逻辑。
- Starter 自动配置。

这些能力应放入对应专项模块。

## Maven

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-core</artifactId>
    <version>4.0.0</version>
</dependency>
```

## 注意点

- 当前主线面向 JDK 21 / Spring Boot 3.5。
- 本模块是底层通用模块，但当前仍包含少量 Spring Core、Servlet API、Validator 等基础依赖；新增能力时应避免继续扩大运行时依赖面。
- 如果能力依赖 Web、Redis、MyBatis、MQ、Excel、第三方协议等明显专项环境，应放到专项模块。
- 模块拆分的历史笔记已移动到根项目 `docs/notes/refactor.md`。
