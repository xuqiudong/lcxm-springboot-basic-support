# Secure ID 开发手册

本文档面向业务开发人员，说明日常开发接口时需要做什么。

## 1. 返回给前端的对象

### 1.1 主 ID

如果对象有主键 ID，并且会返回给前端，实现 `IdEncryptable`。

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

框架会在响应返回前把 `id` 加密。

### 1.2 额外 ID 字段

如果对象里还有其他 ID 类型字段，用 `@SecureId` 标注。

```java
public class UserVO implements IdEncryptable {

    private String id;

    @SecureId
    private String deptId;

    @SecureId
    private String tenantId;
}
```

适合标注：
- `userId`
- `deptId`
- `tenantId`
- `orderId`
- `parentId`
- 其他会暴露真实 ID 的字段

不建议标注：
- 普通名称、标题、备注
- 手机号、邮箱等非 ID 字段
- 已经是业务编码且不需要 secure-id 的字段

## 2. Controller 入参

前端传回来的安全 ID，业务代码中仍然按真实 ID 使用。

### 2.1 query 参数

```java
@GetMapping("/users/detail")
public UserVO detail(@RequestParam("id") Long id) {
    ...
}
```

请求：

```text
GET /users/detail?id=WdLPex...xgIyHU
```

Controller 中拿到的是解密后的真实 ID。

### 2.2 JSON body

```java
@PostMapping("/users/update")
public void update(@RequestBody UserUpdateDTO dto) {
    //...
}
```

JSON 中只要包含 secure-id 包裹值，Filter 会在进入 Controller 前替换成真实 ID。

### 2.3 form 参数

```java
@PostMapping("/users/update-name")
public void updateName(@RequestParam("id") Long id, @RequestParam("name") String name) {
    //...
}
```

form 参数中的安全 ID 会自动解密。

### 2.4 路径参数

```java
@GetMapping("/users/{userId}")
public UserVO detail(@PathVariable("userId") Long userId) {
    //...
}
```

路径参数会在 Spring MVC 绑定 Controller 参数时解密。

注意：
- 不需要把参数名固定写成 `id`。
- `userId`、`orderId`、`tenantId` 等普通路径参数都会尝试解密。
- 明文路径参数会原样返回，兼容历史接口。

## 3. 文件上传接口

文件上传时，框架只处理 multipart 普通字段，不读取或修改文件内容。

```java
@PostMapping("/files/upload")
public void upload(@RequestParam("bizId") Long bizId,
                   @RequestParam("file") MultipartFile file) {
    //...
}
```

需要关注：
- `bizId` 这类普通字段会解密。
- `MultipartFile` 文件内容不处理。
- 新增或改造上传接口后，要用真实上传流程验证文件绑定正常。

## 4. 跳过加密

如果某个接口明确不需要响应 ID 加密，可以加 `@SkipIdSecure`。

```java
@SkipIdSecure
@GetMapping("/public/config")
public Object publicConfig() {
    //...
}
```

常见场景：
- 公开配置接口。
- 不返回业务 ID 的接口。
- 历史接口暂时不能改变返回格式。

## 5. 分页和统一响应

业务项目的 `AbstractSecureIdAdvice` 子类负责提取真实 data。

开发分页接口时，需要确认项目 Advice 已支持对应分页对象。

例如：
- `BaseResponse.data`
- `PageInfo.list`
- `IPage.records`
- 自定义分页模型的数据列表

如果新增了一种统一包装对象或分页对象，需要同步扩展项目 Advice。

## 6. 开发自查

提交接口前，开发人员至少检查：

- 返回给前端的主 ID 是否实现了 `IdEncryptable`。
- 额外暴露的 ID 字段是否加了 `@SecureId`。
- 新增分页或包装类型是否已被项目 Advice 支持。
- 请求参数中的 ID 是否继续按真实 ID 类型编写业务代码。
- 路径参数是否使用了明确的 `@PathVariable("xxx")` 名称。
- 文件上传接口是否验证过文件和普通字段同时可用。
- 不需要加密的接口是否明确使用 `@SkipIdSecure` 或 URL skip。

## 7. 仍然要做权限校验

Secure ID 只隐藏真实 ID，不替代数据权限。

业务代码仍然必须校验：
- 当前用户是否能访问该数据。
- 当前租户是否匹配。
- 当前组织、角色、数据范围是否允许操作。
