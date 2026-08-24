# 请求参数解密链路设计

本文档用于收口 `ParamDecryptFilter` 和 `ParameterRequestWrapper` 的修改方案。

## 1. 目标

请求进入 controller 前，将请求参数中的安全 ID 解密为真实 ID。

需要支持：
- query 参数
- form 参数
- JSON body
- multipart/form-data 中的普通字段
- 文件上传请求中的普通字段

不处理：
- multipart 中的文件内容
- 非请求参数语义的数据流
- 普通业务字段的强制解析
- 路径参数中的加密 ID，如 `/api/users/{id}` 中的 `{id}`

## 2. 核心原则

### 2.1 不破坏原请求语义
- 解密只发生在参数值层面。
- 文件内容不读取、不修改、不替换。
- 原始参数 Map 不直接修改，必须拷贝后处理。
- JSON body 读取后必须重建，保证后续 controller / message converter 可读。

### 2.2 尽量少猜测数据格式
- form/query 只按 servlet 已解析出的参数处理。
- JSON 不做对象结构解析，先采用字符串级局部替换。
- multipart 只处理普通 form field，不进入文件内容。

### 2.3 解密逻辑保持宽容
- 没有加密 ID 的值保持原样。
- 文本中嵌入加密 ID 时只替换命中的部分。
- 解密失败时优先保留原值并记录日志，避免破坏请求链路。

## 3. 当前代码风险

### 3.1 `ParamDecryptFilter`
- `multipartResolver` 在 `init` 中强制获取，缺失时会直接失败。
- 对 multipart 请求直接 `resolveMultipart`，如果请求已被解析，可能重复解析。
- JSON body 读取依赖 `contentLength`，遇到 chunked 或长度未知时可能读不到。
- GET JSON 分支用 `queryString` 做字符集转换和 `%22` 替换，逻辑不通用。
- 过滤器每次进入都可能再次包装请求，内部转发时存在重复处理风险。

### 3.2 `ParameterRequestWrapper`
- `params = new HashMap<>(newParams)` 是浅拷贝，数组值仍然来自原请求，后续修改会影响原数组。
- `getInputStream().isFinished()` / `isReady()` 固定返回 false，不符合可读流语义。
- `getParameter` / `getParameterValues` 中存在无意义类型判断，因为 Map 类型已经是 `Map<String, String[]>`。
- `renewParameterMap` 手动 split query string，未做 URL decode，容易处理错复杂参数。
- 如果 JSON body 为空，wrapper body 默认为空数组；如果原请求 body 未读取但被包装，可能导致后续读取为空。

## 4. 设计方案

### 4.1 Filter 类型
建议改成 `OncePerRequestFilter`。

原因：
- 请求解密不是完全幂等操作。
- 内部转发重复进入会导致重复包装或 body 状态不一致。
- Spring Web 里这种请求包装类过滤器更适合每请求执行一次。

如果需要兼容非 Spring Web 环境，可以保留普通 `Filter` 版本，但当前组件已有 Spring 依赖，优先按 Spring Web 方案处理。

### 4.2 query/form 参数处理
方案：
- 使用 `request.getParameterMap()` 作为参数来源。
- 深拷贝 `Map<String, String[]>`。
- 对每个数组值逐个调用 `IdUtil.decrypt`。
- 不再手动解析 `queryString`。

原因：
- servlet 容器已经负责 query/form 参数解析和 URL decode。
- 手动 split 复杂度高，容易处理错编码、重复 key、空值、多值。

### 4.3 JSON body 处理
方案：
- 只在 `Content-Type` 包含 `application/json` 时读取 body。
- 使用 input stream 循环读到 EOF，不依赖 `contentLength`。
- 使用 request character encoding，缺省 UTF-8。
- 对整个 JSON 字符串调用 `IdUtil.decrypt`，做局部替换。
- 将解密后的字符串写入 wrapper body，保证后续可重复读。

暂不做：
- JSON AST 解析。
- 按字段名判断是否解密。

原因：
- 当前 ID 有明确包裹前后缀，字符串级局部替换足够覆盖嵌入场景。
- AST 解析会引入对 JSON 格式和依赖的额外约束，先不增加复杂度。

### 4.4 multipart 处理
方案：
- 如果请求已经是 `MultipartHttpServletRequest`，直接使用。
- 如果不是，则在 `multipartResolver.isMultipart(request)` 为 true 时调用 `resolveMultipart`。
- 只从 multipart request 的 `getParameterMap()` 获取普通字段并解密。
- 不读取 `MultipartFile` 内容，不修改文件流。
- resolver 由配置类注入，启用此 filter 时必须存在；缺失时启动失败，避免文件表单普通字段未解密却静默放行。

需要特别注意：
- 由 filter 主动 `resolveMultipart` 后，是否需要在请求结束后调用 `cleanupMultipart`。
- 如果 Spring MVC 自己后续还要解析 multipart，提前解析是否会影响 controller 参数绑定。
- `lcxm-basic-framework` 中 `WebCommonUtils.checkMultipart` 已说明历史问题：multipart 解析会读取 request input stream，通常只能解析一次；提前解析后 DispatcherServlet 后续不应再次解析，重复解析可能读不到数据。

建议：
- 优先复用容器/Spring 已解析的 multipart request。
- 如果当前 filter 顺序早于 Spring multipart 解析，需要谨慎测试上传接口。
- multipart 处理不能只看“能否拿到普通参数”，还必须验证 controller 上传文件参数是否仍能正常绑定。
- 如果 filter 主动调用 `resolveMultipart`，chain 执行完成后调用 `cleanupMultipart` 清理临时资源。

### 4.5 Wrapper 行为
Wrapper 应该持有：
- 深拷贝并解密后的参数 Map。
- 可重复读取的 body 字节数组。
- 原请求 character encoding。

必须覆盖：
- `getParameter`
- `getParameterMap`
- `getParameterNames`
- `getParameterValues`
- `getInputStream`
- `getReader`

`ServletInputStream` 行为：
- `isFinished()` 根据 `ByteArrayInputStream.available()` 判断。
- `isReady()` 返回 true。
- `setReadListener()` 可先不支持异步读取，但需要有明确注释。

## 5. 分阶段实现

### 阶段 1：先修 Wrapper
- 已处理：参数 Map 深拷贝。
- 已处理：移除无意义类型判断。
- 已处理：修正 input stream 状态。
- 已处理：明确 body 构造规则，只有 filter 已读取 JSON body 时才接管 body；普通 query/form 包装继续委托原 request body。
- 待处理：补 query/form/json wrapper 单测。

### 阶段 2：再修 Filter 普通请求
- 已处理：跳过分支继续 filter chain。
- 已处理：JSON body 按 stream 读到 EOF，不依赖 contentLength。
- 已处理：GET 不再走 JSON body 特殊处理，统一走 parameterMap。
- 已处理：Filter 改为 `OncePerRequestFilter`。
- 已处理：Filter 通过 `FilterRegistrationBean` 注册，拦截路径、order、enabled 由子类配置。
- 待处理：补 JSON body 可重复读测试。

### 阶段 3：最后处理 multipart
- 已处理：resolver 由配置类注入，启用 filter 时强制要求存在。
- 已处理：请求已经是 `MultipartHttpServletRequest` 时不重复 resolve。
- 已处理：只通过 `getParameterMap()` 处理普通字段，不读取文件内容。
- 已处理：filter 主动解析 multipart 后，在 chain 结束后调用 `cleanupMultipart`。
- 待处理：补 multipart 普通字段测试。
- 待处理：补文件上传参数绑定测试。

## 6. 待确认点

- 解密失败时是否需要记录 warn，还是 debug 即可。

## 6.1 当前处理状态

已处理：
- `ParameterRequestWrapper` 参数 Map 深拷贝。
- `ParameterRequestWrapper` 参数读取逻辑简化。
- `ParameterRequestWrapper` body 只在 JSON 已读取时接管，普通 query/form 不影响原 request body。
- `ParameterRequestWrapper` 的 `isFinished()` / `isReady()` 已修正。
- `ParamDecryptFilter` 已改为 `OncePerRequestFilter`。
- `ParamDecryptFilter` 已通过配置类注册，子类需明确提供拦截路径、order、enabled。
- 已移除和 secure-id 职责不一致的 `US_OTHERS` 历史安全规则。
- JSON body 已改为读取 input stream 到 EOF。
- GET 请求不再走 JSON body 逻辑。

暂未处理：
- multipart 文件参数绑定验证。
- query/form/json wrapper 单测。
- JSON body 可重复读单测。
- multipart 普通字段解密和文件上传单测。

## 8. 配置类预留点

`AbstractSecureIdConfig` 当前要求子类明确提供：
- `saltSupplier()`：运行时盐值来源。
- `buildSecureIdAdvice()`：项目响应加密 Advice。
- `secureIdPointCutExpression()`：响应加密自定义切点。
- `paramDecryptFilterUrlPatterns()`：请求解密过滤器拦截路径。
- `paramDecryptFilterOrder()`：请求解密过滤器顺序。
- `paramDecryptFilterEnabled()`：是否启用请求解密过滤器。

其中 `paramDecryptFilterOrder()` 需要特别注意：
- 如果 salt 依赖用户上下文 filter，应晚于该 filter。
- 如果需要在 controller 参数绑定前解密，应早于 Spring MVC handler 处理。
- 如果项目有安全框架、日志链路、body 缓存 filter，需要按实际链路确认顺序。

## 7. 后续单独设计：路径参数解密

路径参数示例：

```text
/api/users/WdLPex...xgIyHU/detail
```

当前 Filter 阶段先不处理路径参数解密。

原因：
- path variable 不在 `getParameterMap()` 中。
- 直接改 `requestURI` 风险较高，可能影响 Spring MVC 路由匹配。
- 正确处理可能需要同时覆盖 `getRequestURI()`、`getRequestURL()`、`getServletPath()`、`getPathInfo()`。
- Spring MVC 可能在某些阶段缓存路径匹配信息，Filter 顺序会影响可行性。

后续可选方案：
- 基于 `HandlerMethodArgumentResolver` 处理 `@PathVariable`。
- 增加类似 `@SecurePathVariable` 的注解。
- 在 Controller 参数绑定后做统一转换。

处理顺序：
1. 先完成 query/form/JSON/multipart 普通字段解密。
2. 再单独评估路径参数解密方案。
