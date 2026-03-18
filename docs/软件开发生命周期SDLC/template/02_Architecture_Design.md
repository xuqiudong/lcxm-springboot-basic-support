# 🏗️ 系统架构设计文档 (Lite)

## 1. 技术栈决策 (Architecture Decision Record - ADR)
| 层次 | 技术选型 | 理由/备注 |
| :--- | :--- | :--- |
| **前端** | Vue3 + TypeScript + ElementPlus | 类型安全，组件丰富，适合中后台 |
| **样式** | TailwindCSS | 快速构建，减少 CSS 文件体积 |
| **后端** | Java 17/21 + SpringBoot 3 | 生态成熟，个人擅长 |
| **数据库** | MySQL 8.0 | 关系型数据首选 |
| **缓存** | Redis | 缓存热点数据，Session 存储 |
| **接口文档** | Knife4j (Swagger) | 自动生成 TS 类型，减少联调成本 |

## 2. 系统架构图 (C4 Container Level)
*(在此处插入架构图：展示 Browser <-> Nginx <-> SpringBoot <-> MySQL/Redis)*
> **架构说明**：
> - 前后端分离，通过 RESTful API 通信。
> - 静态资源由 Nginx 托管 (或 SpringBoot 内置)。
> - 敏感操作需经过拦截器/过滤器鉴权。

## 3. 数据库设计 (ER Core)
*(在此处插入 ER 图截图)*
### 核心表结构简述
- **t_user**: 用户基础信息 (idx_username 唯一索引)
- **t_order**: 订单主表 (status 字段枚举：0-新建, 1-支付...)
- **t_order_item**: 订单明细 (FK: order_id)
> **设计难点与对策**：
> - 问题：订单状态流转复杂。
> - 对策：使用状态机模式或在 Service 层严格校验状态变更。

## 4. 核心业务时序图 (Sequence Diagram)
*(针对最复杂的业务，例如：创建订单并扣减库存)*
*(在此处插入时序图：User -> Frontend -> Controller -> Service(A) -> DB)*
> **关键点**：
> 1. 事务边界：在 Service 层开启 @Transactional。
> 2. 并发控制：使用数据库乐观锁 (version 字段) 防止超卖。

## 5. 接口契约 (API Contract)
> **原则**：先定义接口，再写实现。
> - 所有接口通过 **Knife4j** 管理。
> - 响应统一格式：`{ code: 200, msg: "success", data: {} }`
> - 错误码规范：200(成功), 401(未授权), 500(系统异常), 业务错误码 10001-19999

## 6. 目录结构规划
```text
src/main/java/com/example
 ├── config      # 配置类
 ├── controller  # 接口层 (薄)
 ├── service     # 业务逻辑层 (厚)
 ├── mapper      # DAO 层
 ├── domain      # 实体类 (DO/DTO/VO 分离)
 └── common      # 通用工具/异常处理