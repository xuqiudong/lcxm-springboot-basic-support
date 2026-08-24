# Secure ID 使用说明

`lcxm-basic-secure-id` 用于隐藏接口中的真实 ID，降低平行越权的暴露面。

注意：数据权限仍然必须由业务系统自己控制。本组件只负责把前端可见的真实 ID 替换成可逆的安全 ID，并在请求进入业务代码前解密。

业务开发人员日常写接口时需要遵守的规则见：[开发手册](developer-guide.md)。

## 1. 组件做了什么

### 1.1 响应 ID 加密
Controller 返回后，通过切面对响应数据中的 ID 做加密。

已支持：
- 实现 `IdEncryptable` 的对象：加密 `getId()` / `setId()` 对应的 `id`
- 标注 `@SecureId` 的字段：加密额外 ID 字段，如 `userId`、`projectId`
- 子对象递归处理
- `Collection` / 数组递归处理
- `Map` value 递归处理
- `Map` 中约定 ID key 的字符串 value 加密
- 最大递归深度限制
- 循环引用保护
- 字段元数据缓存，减少反射扫描成本
- `@SkipIdSecure` 跳过响应加密
- URL 跳过响应加密

由业务项目扩展：
- 从统一响应对象中提取真实 data，如 `BaseResponse.data`
- 处理分页对象，如 `PageInfo`、`IPage`、自定义分页模型
- 配置常见 ID 字段名
- 配置最大递归深度
- 配置跳过 URL

### 1.2 请求 ID 解密
请求进入 Controller 前，通过 Filter 解密请求参数中的安全 ID。

已支持：
- query 参数
- form 参数
- JSON body
- multipart/form-data 普通字段
- 文件上传请求中的普通字段
- 普通 `@PathVariable` 路径参数

不处理：
- 文件内容
- `@PathVariable Map<String, String>`，此类参数继续交给 Spring 默认解析器
- 普通业务字段的结构化解析

路径参数解密不改写 request URI，而是在 Spring MVC 完成路由匹配后、调用 Controller 前解密入参。

## 2. 快速接入

业务项目需要继承 `AbstractSecureIdConfig`，提供盐值、响应加密 Advice、切点、Filter 注册参数。

示例：

```java
@Configuration
public class SecureIdConfig extends AbstractSecureIdConfig {

    @Override
    public Supplier<String> saltSupplier() {
        return () -> LoginContext.getUsername();
    }

    @Override
    public AbstractSecureIdAdvice buildSecureIdAdvice() {
        return new ProjectSecureIdAdvice();
    }

    @Override
    public String secureIdPointCutExpression() {
        return "execution(* com.example..controller..*(..))";
    }

    @Override
    public List<String> paramDecryptFilterUrlPatterns() {
        return Collections.singletonList("/*");
    }

    @Override
    public int paramDecryptFilterOrder() {
        return 100;
    }

    @Override
    public boolean paramDecryptFilterEnabled() {
        return true;
    }

    @Override
    public boolean pathVariableDecryptEnabled() {
        return true;
    }

}
```

注意：
- `saltSupplier()` 应返回当前请求上下文相关的盐值，如 `sid`、`username`
- 如果盐值来自登录上下文，`paramDecryptFilterOrder()` 必须晚于设置登录上下文的 Filter
- 项目必须存在 `MultipartResolver` bean，因为文件表单普通字段解密依赖它
- 路径参数解密只提供总开关，不提供参数名白名单，避免遗漏业务命名

### 2.1 启用开关

如果项目需要通过配置控制 secure-id 是否启用，建议在业务项目自己的配置类上加条件。

示例：

```java
@Configuration
@ConditionalOnProperty(
    prefix = "lcxm.secure-id",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class SecureIdConfig extends AbstractSecureIdConfig {
    ...
}
```

不建议：
- 在 `AbstractSecureIdConfig` 抽象父类上加条件注解，并依赖它对子类一定生效
- 使用无关配置名，如 `spring.quartz.enabled`

如果只想控制请求解密 Filter，可以只在业务项目自己的 Filter 注册配置上做条件控制。

## 3. 实现响应加密 Advice

业务项目需要继承 `AbstractSecureIdAdvice`。

示例：

```java
public class ProjectSecureIdAdvice extends AbstractSecureIdAdvice {

    @Override
    protected Object extractData(Object returnValue, Method method, Object[] args, Object target) {
        if (returnValue instanceof BaseResponse) {
            return ((BaseResponse<?>) returnValue).getData();
        }
        return null;
    }

    @Override
    protected boolean encryptSpecialObject(Object value, SecureIdContext context) {
        if (value instanceof PageInfo) {
            encryptValue(((PageInfo<?>) value).getList(), context);
            return true;
        }
        return false;
    }

    @Override
    protected Set<String> skipUrls() {
        return Collections.emptySet();
    }

    @Override
    protected Set<String> commonIdFieldNames() {
        return new HashSet<>(Arrays.asList("userId", "projectId", "orgId"));
    }

    @Override
    protected int maxDepth() {
        return 4;
    }
}
```

## 4. 标注对象和字段

主 ID：

```java
public class UserVO implements IdEncryptable {
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
```

额外 ID 字段：

```java
public class UserVO implements IdEncryptable {
    private String id;

    @SecureId
    private String deptId;
}
```

跳过某个接口：

```java
@SkipIdSecure
@GetMapping("/public")
public BaseResponse<?> publicApi() {
    ...
}
```

显式启用某个接口：

```java
@EnableSecureId
@GetMapping("/user/detail")
public BaseResponse<UserVO> detail() {
    ...
}
```

`@EnableSecureId` 和自定义 pointcut 取并集；`@SkipIdSecure` 优先级最高。

## 5. 请求解密说明

请求参数中的安全 ID 会在进入 Controller 前被解密。

示例：

```text
GET /api/user/detail?id=WdLPex...xgIyHU
```

Controller 中拿到：

```text
id=真实ID
```

JSON 示例：

```json
{
  "userId": "WdLPex...xgIyHU",
  "ids": "WdLPex...xgIyHU,WdLPex...xgIyHU"
}
```

Filter 会对整个 JSON 字符串做局部替换，然后重建 body，保证后续 `@RequestBody` 可读取。

multipart 示例：
- 普通字段会解密
- 文件内容不会读取或修改

路径参数示例：

```java
@GetMapping("/users/{userId}")
public UserVO detail(@PathVariable("userId") Long userId) {
    ...
}
```

请求：

```text
GET /users/WdLPex...xgIyHU
```

Controller 中拿到：

```text
userId=真实ID对应的 Long
```

路径参数处理规则：
- 只处理普通 `@PathVariable` 参数。
- 不按参数名白名单判断，避免遗漏业务命名。
- 未加密值会原样返回，兼容历史明文路径。
- `@PathVariable Map<String, String>` 不接管，继续走 Spring 默认解析器。
- 不改写 request URI，不影响 Spring MVC 路由匹配。
- 如需极特殊的路径变量排除策略，可以覆盖 `SecurePathVariableArgumentResolver` bean，而不是在基础配置中增加大而全的判断项。

## 6. 注意事项

- 本组件不是权限控制组件，不能替代数据权限校验。
- 安全 ID 是可逆混淆，不是强密码学安全边界。
- 盐值变化后，旧安全 ID 不保证可解密。
- `commonIdFieldNames()` 应保持稳定，不建议做请求级动态值。
- Filter 依赖 `MultipartResolver`，业务项目必须正确配置文件上传解析器。
- multipart 解析通常只能可靠执行一次，禁止重复解析上传请求。
- 路径参数解密依赖 Spring MVC `HandlerMethodArgumentResolver` 机制；当前代码保持 `jakarta.servlet.*` 口径，低版本适配时只处理包路径差异。

## 7. 验证

### 7.1 业务项目接入后验证清单

接入业务项目后，建议至少验证这些接口场景：

- 响应加密：普通 VO 的 `IdEncryptable#id` 会加密。
- 响应加密：`@SecureId` 标注的额外 ID 字段会加密。
- 响应加密：列表、数组、嵌套对象中的 ID 会加密。
- 响应加密：业务统一响应对象和分页对象能被项目 Advice 正确提取 data。
- 请求解密：query 参数能解密，如 `/detail?id=WdLPex...xgIyHU`。
- 请求解密：form 参数能解密。
- 请求解密：JSON body 中的安全 ID 能解密，且 Controller 的 `@RequestBody` 仍能正常读取。
- 请求解密：multipart 普通字段能解密。
- 文件上传：文件内容不被读取或修改，Controller 中的 `MultipartFile` 仍能正常绑定。
- 路径参数：普通 `@PathVariable` 能解密并转换成目标类型，如 `Long userId`。
- 路径参数：历史明文路径参数仍能正常访问。
- 跳过规则：`@SkipIdSecure` 和 URL skip 能按预期跳过响应加密。
- 盐值顺序：如果盐值依赖登录上下文，确认登录上下文 filter 早于 `ParamDecryptFilter`。

重点关注：
- 文件上传接口要用真实业务上传流程测一遍，确认没有 multipart 重复解析问题。
- JSON body 读取链路如果项目里还有日志、签名、body cache filter，需要一起验证 filter 顺序。
- 路径参数解密依赖 Spring MVC 参数解析器替换，建议至少测一个 `@PathVariable String` 和一个 `@PathVariable Long`。

### 7.2 模块单元测试

运行测试：

```bash
mvn test -Dskip.test=false
```

如果 Windows forked JVM 输出通道异常，可单独运行：

```bash
mvn -Dtest=AbstractSecureIdAdviceTest test -Dskip.test=false -DforkCount=0
```
