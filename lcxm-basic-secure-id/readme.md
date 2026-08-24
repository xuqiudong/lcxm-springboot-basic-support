# 平行越权 Secure ID 基础组件

本组件用于隐藏接口中的真实 ID，降低平行越权的暴露面。

> 平行越权的根本解决方案仍然是业务系统的数据权限控制。
> 本组件只负责 ID 的可逆隐藏和请求侧解密，不替代权限校验。

## 核心能力

- 响应返回前加密 ID
- 请求进入 Controller 前解密 ID
- 支持 `IdEncryptable#getId()`
- 支持 `@SecureId` 标注额外 ID 字段
- 支持 query、form、JSON body、multipart 普通字段解密
- 支持普通 `@PathVariable` 路径参数解密
- 支持响应对象递归、集合、数组、Map value
- 支持分页等特殊对象由业务子类扩展
- 支持运行时盐值
- 支持自定义切点和注解切点

## 快速入口

- 使用说明：[docs/usage.md](docs/usage.md)
- 开发手册：[docs/developer-guide.md](docs/developer-guide.md)
- 设计收口：[docs/secure-id-design-todo.md](docs/secure-id-design-todo.md)
- 请求解密链路设计：[docs/param-decrypt-filter-design.md](docs/param-decrypt-filter-design.md)

## 当前不处理

- 数据权限本身
- 文件内容解密
- 普通业务字段加密
- 旧盐值兼容
