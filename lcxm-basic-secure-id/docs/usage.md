# Secure ID 使用说明

`lcxm-basic-secure-id` 用于隐藏接口中的真实 ID，降低平行越权的暴露面。

注意：数据权限仍然必须由业务系统自己控制。本组件只负责把前端可见的真实 ID 替换成可逆的安全 ID，并在请求进入业务代码前解密。

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

不处理：
- 文件内容
- 路径参数，如 `/api/user/{id}`
- 普通业务字段的结构化解析

路径参数解密后续单独设计。

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
}
```

注意：
- `saltSupplier()` 应返回当前请求上下文相关的盐值，如 `sid`、`username`
- 如果盐值来自登录上下文，`paramDecryptFilterOrder()` 必须晚于设置登录上下文的 Filter
- 项目必须存在 `MultipartResolver` bean，因为文件表单普通字段解密依赖它

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

## 6. 注意事项

- 本组件不是权限控制组件，不能替代数据权限校验。
- 安全 ID 是可逆混淆，不是强密码学安全边界。
- 盐值变化后，旧安全 ID 不保证可解密。
- `commonIdFieldNames()` 应保持稳定，不建议做请求级动态值。
- Filter 依赖 `MultipartResolver`，业务项目必须正确配置文件上传解析器。
- multipart 解析通常只能可靠执行一次，禁止重复解析上传请求。
- 路径参数解密当前不支持，后续单独设计。

## 7. 验证

运行测试：

```bash
mvn test -Dskip.test=false
```

如果 Windows forked JVM 输出通道异常，可单独运行：

```bash
mvn -Dtest=AbstractSecureIdAdviceTest test -Dskip.test=false -DforkCount=0
```
