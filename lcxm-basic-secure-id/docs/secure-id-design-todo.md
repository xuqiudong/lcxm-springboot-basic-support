# Secure ID 设计收口

本文档基于当前 `readme` 和你的逐条回应整理，目的是把方案收敛成三类：
- 已确认的约束和边界
- 可以丢弃的方向
- 仍然待确认的问题

后面实现时，优先遵守“已确认”部分。

## 1. 已确认的约束和边界

### 1.1 目标边界
- 这个组件只负责隐藏真实 ID、降低暴露面。
- 不负责数据权限控制本身。
- 主要作用对象是返回给前端的数据中的 ID，包括子对象里的 ID。

### 1.2 加密定位
- 方案必须可逆。
- 先不考虑更换算法。
- 这套机制本质是可逆混淆，不作为强安全边界使用。

### 1.3 盐值策略
- 优先使用运行时盐值。
- 盐值可来自当前用户上下文，比如 `sid`、`username` 这类和当前使用人相关的属性。
- 盐值变化后，旧的加密 ID 不要求兼容。

### 1.4 响应加密范围
- 响应侧重点是前端可见数据里的 ID。
- `IdEncryptable` 和 `@SecureId` 是两个维度，不是互斥关系。
- 当前只处理 ID 类型字段的加密，不扩展到普通业务字段。
- `@SecureId` 仍然用于额外的 ID 类型字段加密。
- 是否处理 `BaseResponse` 由子类决定，目的是控制影响范围。

### 1.5 请求解密范围
- 支持 query 参数、form、JSON body、multipart、文件上传附带字段。
- 支持单个 ID、多个 ID 分隔、长文本中局部替换。

### 1.6 切面规则
- `@EnableSecureId` 和自定义 pointcut 取并集。
- `@SkipIdSecure` 优先级最高。
- URL 白名单跳过响应加密是允许的。

### 1.7 遍历规则
- 响应对象遍历要有深度限制。
- 要加日志。
- 递归过程要保留子类扩展空间，也就是基础层只做通用遍历和通用 ID 处理，特殊对象类型由子类补充。

### 1.8 字段规则
- 只处理 ID 类型字段。
- 常见外键字段名可以配置，但属于业务项目扩展能力。

## 2. 可以丢弃的方向

- 后续切换更强算法，这一阶段不做。
- 旧盐值 / 旧版本加密 ID 的兼容，不做。
- 非 ID 普通字段统一加密，不做。
- 解密失败时强制抛异常作为默认策略，不做。
- 对所有未知对象类型做无差别支持，不做。

## 3. 建议方案与仍需确认的问题

### 3.1 配置开关方案
建议基础组件提供配置对象，业务项目可以通过配置文件覆盖；子类 config 负责把配置对象接入具体项目。

建议配置项：
- `enabled`：总开关，默认 `true`。
- `request-decrypt-enabled`：请求解密开关，默认跟随 `enabled`。
- `response-encrypt-enabled`：响应加密开关，默认跟随 `enabled`。
- `max-depth`：响应遍历最大深度，默认 `4`，子类 Advice 可覆盖。
- `skip-paths`：跳过路径，建议放在配置对象中，子类可追加。
- `common-id-fields`：常见 ID 字段名，建议放在配置对象中，子类可追加。

设计理由：
- 只靠子 config 写死，不利于不同环境快速开关。
- 只保留总开关不够，调试或灰度时经常需要只关请求解密或只关响应加密。
- Spring Boot 项目可以走 `@ConfigurationProperties`；老 Spring MVC 项目可以由子 config 手动构造同一个配置对象，保持核心代码不变。

### 3.2 `AbstractSecureIdAdvice` 的职责边界
基础层建议负责通用遍历框架，不负责绑定具体项目响应模型。

基础层默认处理：
- `IdEncryptable`：加密 `id`。
- `@SecureId` 字段：加密额外 ID 类型字段。
- `Collection` / 数组：遍历元素。
- `Map`：默认遍历 value；是否处理 key 暂不默认支持。
- 普通对象：遍历字段，处理 `@SecureId` 字段和子对象。
- 深度限制和循环引用保护。
- 普通对象字段列表按 `Class` 缓存，避免大量同类型对象重复反射扫描。

交给子类处理：
- 是否识别 `BaseResponse`，以及从哪里取 `data`。
- 项目分页对象，如 `PageInfo`、`IPage`、自定义 page model。
- 项目特殊 wrapper、树结构、第三方不可变对象。
- 项目常见外键字段名。

设计理由：
- 基础层做通用结构，保证组件可复用。
- 项目响应模型差异很大，放到子类里更稳。
- 子类扩展点不是重新实现整套递归，而是在关键节点接管特殊对象。

### 3.3 Filter 的集成方式
建议改成 `OncePerRequestFilter`。

原因：
- 请求解密是幂等性不完全可靠的操作，重复进入会增加误处理风险。
- 内部转发时，如果前一次已经包装和解密，后一次再处理 body/parameter 可能导致空 body、重复包装或参数状态不一致。
- Spring Web 项目中 `OncePerRequestFilter` 更适合这种“每个请求只做一次包装”的场景。

JSON 请求建议：
- 只在 content type 是 JSON 时读取 body。
- 读取后重建 body，交给 wrapper 支持重复读。
- 不建议通过 `contentLength` 固定长度读取，应按 input stream 读完。

form / query 参数建议：
- 基于 `getParameterMap()` 拷贝一份新 map。
- 对新 map 解密，避免修改容器原始数组。

multipart 建议：
- 只处理 multipart 中的普通 form field。
- 文件内容不解密、不读取、不修改。
- 不建议在 filter 里手动创建新的 multipart 配置；应优先复用容器已有 `MultipartResolver`。
- 如果当前请求已经是 `MultipartHttpServletRequest`，不要重复 resolve。

### 3.4 反射失败时的降级策略
默认策略建议：跳过当前字段或当前对象分支，保留原值，记录 debug/warn 日志，不中断整个响应。

具体规则：
- 单个字段读取失败：跳过该字段，继续处理其他字段。
- 单个字段写回失败：保留原值，继续处理其他字段。
- 某个对象类型不支持反射遍历：跳过该对象分支。
- 递归超深：跳过超深分支。
- 加密过程中出现异常：保留原值，记录日志。

设计理由：
- 响应加密是降低暴露面的增强能力，不应轻易破坏业务接口可用性。
- 局部失败时整体失败，风险大于收益。
- 日志要能定位对象类型、字段名、处理阶段，但不要打印敏感原始 ID。
 
## 4. 代码待办

### 4.1 `IdUtil`
- `replacementAllHanderString` 要做替换结果转义，避免特殊字符异常。
- 如果是单字段加解密，尽量走快速路径，减少正则替换开销。
- 补单元测试：
  - 单个 ID
  - 多个嵌入 ID
  - 空值
  - 非法值

### 4.2 `ParamDecryptFilter`
- 这块要重点重审。
- 检查 JSON 处理是否有重复读取 body 的问题。
- 检查文件上传和 multipart 的处理是否会污染原请求。
- 检查跳过分支是否会直接 `return` 导致请求中断。

### 4.3 `ParameterRequestWrapper`
- 补齐可重复读取语义：
  - `getInputStream()`
  - `getReader()`
  - `isFinished()`
  - `isReady()`
- 补充注释，说明参数合并和 body 重建逻辑。
- 查缺补漏，避免直接改原数组带来副作用。

### 4.4 `SecureIdAdvisor`
- `getAdvice()` 必须返回有效 `Advice`。
- 组合逻辑已经决定取并集，需要实现成正确行为。
- 明确空表达式时的默认行为。

### 4.5 `AbstractSecureIdAdvice`
- 已实现遍历主链路。
- 已提供子类可覆盖的模板点：
  - `extractData`
  - `encryptSpecialObject`
  - `commonIdFieldNames`
  - `maxDepth`
  - `shouldSkip`
- 已明确基础层处理通用结构，项目响应模型和特殊对象交给子类。
- 已增加 class 字段元数据缓存，降低普通对象递归时的反射扫描、注解判断和字段名判断成本。

### 4.6 `AbstractSecureIdConfig`
- 配置层需要再分析一次。
- 明确业务项目必须实现的 bean。
- 检查 `@PostConstruct` 设置 salt 的时机是否合理。

### 4.7 注解定义
- 保持 `@EnableSecureId`、`@SkipIdSecure`、`@SecureId` 三者职责清晰。

## 5. 建议的后续推进顺序
1. 先定 `AbstractSecureIdAdvice` 的职责边界。
2. 再定 `ParamDecryptFilter` 和 `ParameterRequestWrapper` 的请求链路。
3. 然后收口 `SecureIdAdvisor` 的切点行为。
4. 最后补 `IdUtil` 的替换细节和测试。
