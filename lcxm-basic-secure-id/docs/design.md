# Secure ID 设计边界

本文档记录当前稳定设计，不记录讨论过程。

## 1. 目标

`lcxm-basic-secure-id` 只负责隐藏前端可见的真实 ID，并在请求进入业务代码前把安全 ID 解密回真实 ID。

它不替代业务权限校验。业务系统仍然必须校验当前用户、租户、组织、角色和数据范围是否允许访问目标数据。

## 2. 能力范围

响应加密：
- `IdEncryptable#getId()` 主 ID 加密。
- `@SecureId` 标注的额外 ID 字段加密。
- 支持对象递归、集合、数组、Map value。
- 支持最大递归深度和循环引用保护。
- 字段元数据按 Class 缓存，减少重复反射扫描。
- 统一响应、分页对象、特殊 wrapper 由业务 Advice 子类提取数据。

请求解密：
- query 参数。
- form 参数。
- JSON body 字符串局部替换，读取后重建 body。
- multipart 普通字段。
- 普通 `@PathVariable`。

明确不做：
- 不做数据权限控制。
- 不读取、不修改文件内容。
- 不统一加密普通业务字段。
- 不兼容旧盐值。
- 不按路径参数名配置 ID 白名单。
- 不接管 `@PathVariable Map<String, String>`。

## 3. 核心设计

### 3.1 响应加密

响应加密由 `AbstractSecureIdAdvice` 负责。

基础类处理通用结构：
- `IdEncryptable`
- `@SecureId`
- `Iterable`
- 数组
- `Map`
- 普通对象字段递归

业务项目通过子类处理项目差异：
- 从统一响应对象中提取 data。
- 从分页对象中提取 records/list。
- 配置常见 ID 字段名。
- 配置最大递归深度。
- 配置跳过 URL。

### 3.2 请求参数解密

`ParamDecryptFilter` 在请求进入 Controller 前处理 query、form、JSON body 和 multipart 普通字段。

关键约束：
- 使用 `OncePerRequestFilter`，每个请求只处理一次。
- 参数 Map 深拷贝后再解密，不修改容器原始数组。
- JSON body 读取到 EOF 后重建，保证后续 `@RequestBody` 可读。
- multipart 使用项目已有 `MultipartResolver`，不创建新的解析器。
- multipart 文件内容不进入解密流程。

### 3.3 路径参数解密

路径参数不在 Filter 中处理。

`SecurePathVariableArgumentResolver` 继承 Spring 默认 `PathVariableMethodArgumentResolver`，只在默认取值后增加 `IdUtil.decrypt()`。

`SecurePathVariableArgumentResolverPostProcessor` 在 `RequestMappingHandlerAdapter` 初始化后，用 secure-id resolver 替换 Spring 默认普通路径参数 resolver。

这样可以保留 Spring 默认的 required、空值、参数名解析和类型转换逻辑，同时增加解密能力。

## 4. 配置入口

业务项目继承 `AbstractSecureIdConfig`，需要提供：
- `saltSupplier()`：运行时盐值来源。
- `realSecureIdAdvice()`：项目响应加密 Advice。
- `secureIdPointCutExpression()`：响应加密切点。
- `paramDecryptFilterUrlPatterns()`：请求解密 Filter 拦截路径。
- `paramDecryptFilterOrder()`：请求解密 Filter 顺序。
- `paramDecryptFilterEnabled()`：是否启用请求参数解密。
- `pathVariableDecryptEnabled()`：是否启用路径参数解密。

如果盐值依赖登录上下文，`paramDecryptFilterOrder()` 必须晚于设置登录上下文的 Filter。

## 5. 需要业务项目验证

模块单测已覆盖核心逻辑。接入业务项目后仍建议验证：
- 统一响应和分页对象是否能被项目 Advice 正确提取 data。
- 真实文件上传接口中，普通字段能解密且 `MultipartFile` 能正常绑定。
- 项目中存在日志、签名、body cache filter 时，JSON body 读取顺序是否正确。
- salt 依赖登录上下文时，Filter 顺序是否正确。
- `@PathVariable String` 和 `@PathVariable Long` 是否都能正常解密。

## 6. 验证命令

```bash
mvn test -Dskip.test=false -DforkCount=0
```
