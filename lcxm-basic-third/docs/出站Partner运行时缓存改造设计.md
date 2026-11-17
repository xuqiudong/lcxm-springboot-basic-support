# 出站 Partner 运行时缓存改造设计

## 文档状态

- 状态：评审通过，已实现
- 日期：2026-11-17
- 范围：`AbstractOutboundClient`、`AbstractOutboundPartner` 及其运行时配置生命周期
- 本文只定义设计和兼容性取舍，不包含代码实现

## 背景

当前 `AbstractOutboundPartner` 缓存的是 `OutboundPartnerConfig`，而父类
`AbstractOutboundClient` 会在第一次使用时分别保存 `ThirdClientOptions` 和
`OutboundExecutor`：

```java
private ThirdClientOptions options;

private OutboundExecutor executor;
```

配置缓存过期后，`buildUrl()` 可以通过新的配置取得新 host，但已初始化的
`options` 和 `executor` 不会刷新。`HttpOutboundExecutor` 又持有初始化时传入的
`ThirdClientOptions`，因此可能出现以下组合：

```text
新 host
+ 新 requestTimeout/defaultHeaders
+ 旧 proxy/exchangeLogEnabled
```

配置和执行环境不属于同一个版本，配置缓存的刷新语义不完整。

## 改造目标

1. 将厂商配置、HTTP 配置和执行器作为同一个运行时快照创建和缓存。
2. Runtime 缓存未命中或过期时，统一重新加载配置并创建执行器。
3. `options()` 保持为现有代码的稳定访问入口，业务调用方式不变。
4. 构造阶段不读取数据库、Redis 或配置中心，不调用可被子类覆盖的解析方法，不创建默认执行器。
5. 删除当前没有实际使用价值的固定配置模式，降低生命周期模型的认知负担。
6. 当前框架明确定位为 HTTP 出站框架；未来非 HTTP 协议使用独立抽象，不通过当前双参数构造器预留。

## 非目标

1. 本次不修改请求模型、响应解析、交换日志内容和业务响应钩子。
2. 本次不引入 SFTP、MQ、WebSocket 等非 HTTP 协议抽象。
3. 本次不修改具体 Partner 的业务配置结构。
4. 本次不要求中断或迁移已经开始执行的旧请求。

## 设计结论

### 运行时模型

新增内部 Partner Runtime，统一持有同一版本的三个对象：

```java
final class OutboundPartnerRuntime<C extends OutboundPartnerConfig> {

    private final C config;

    private final ThirdClientOptions options;

    private final OutboundExecutor executor;
}
```

Runtime 是生命周期容器，不取代对象的解析和创建扩展点：

```text
loadConfig()
  -> 产生厂商配置

resolveOptions(config)
  -> 从本次厂商配置产生 HTTP 配置

resolveExecutor(options)
  -> 使用本次 HTTP 配置创建执行器

OutboundPartnerRuntime
  -> 将三者作为一个版本保存
```

### 缓存模型

当前缓存：

```java
Cache<String, C> configCache;
```

调整为：

```java
Cache<String, OutboundPartnerRuntime<C>> runtimeCache;
```

一个 Partner 的缓存中只保存一个当前 Runtime。缓存刷新后，正在执行的请求可能仍然引用旧对象；
新请求使用新 Runtime。旧请求完成并释放引用后，旧 Runtime 由 JVM 正常回收。

### 延迟创建

`AbstractOutboundPartner` 构造阶段只保存执行器工厂，不创建 Caffeine 缓存，也不执行以下操作：

```text
loadConfig()
resolveOptions(config)
resolveExecutor(options)
```

第一次请求访问 Runtime 时先延迟创建 Caffeine 缓存，再执行 Runtime 创建流程：

```text
currentRuntime()
  -> 按 configCacheTtl() 延迟创建 runtimeCache
  -> 查询 runtimeCache
  -> 未命中
  -> loadConfig()
  -> resolveOptions(config)
  -> resolveExecutor(options)
  -> 组装 OutboundPartnerRuntime
  -> 写入缓存
```

缓存本身通过双重检查安全发布，避免父类构造阶段调用子类覆盖的 `configCacheTtl()`；
Caffeine 的原子加载负责协调同一个缓存 key 的并发 Runtime 初始化，不再分别同步 `options` 和 `executor`。

## 类职责调整

### AbstractOutboundClient

改造后只负责 HTTP 请求构建和执行的公共流程，不再持有配置及执行器状态：

```java
public abstract class AbstractOutboundClient {

    /**
     * 获取当前 HTTP 配置。
     *
     * @return 当前 HTTP 配置
     */
    protected abstract ThirdClientOptions options();

    /**
     * 获取当前出站执行器。
     *
     * @return 当前出站执行器
     */
    protected abstract OutboundExecutor executor();
}
```

现有调用位置继续使用：

```java
options().getRequestTimeout();
executor().execute(request);
```

不要求业务代码直接感知 Runtime。

### AbstractOutboundPartner

`AbstractOutboundPartner` 成为 Runtime 生命周期的唯一管理者：

```java
protected final C getConfig() {
    return currentRuntime().getConfig();
}

@Override
protected ThirdClientOptions options() {
    return currentRuntime().getOptions();
}

@Override
protected OutboundExecutor executor() {
    return currentRuntime().getExecutor();
}
```

Runtime 获取入口：

```java
private OutboundPartnerRuntime<C> currentRuntime() {
    return runtimeCache().get(RUNTIME_CACHE_KEY, key -> createRuntime());
}
```

`runtimeCache()` 通过 `volatile` 和双重检查完成缓存本身的延迟创建，确保
`configCacheTtl()` 在子类构造完成后才被调用。

Runtime 创建入口：

```java
private OutboundPartnerRuntime<C> createRuntime() {
    C config = checkConfig(loadConfig());
    ThirdClientOptions options = resolveOptions(config);
    OutboundExecutor executor = resolveExecutor(options);
    return new OutboundPartnerRuntime<>(config, options, executor);
}
```

### 配置解析扩展点

保留 `resolveExecutor(ThirdClientOptions)`，因为它仍然是 Runtime 创建执行器的来源，并支持：

- 使用 `OutboundExecutorFactory` 创建标准 HTTP 执行器；
- HTTP 客户端实现替换；
- 测试执行器注入；
- 项目级执行器定制。

原无参数方法：

```java
protected ThirdClientOptions resolveOptions()
```

调整为带当前配置参数的方法：

```java
protected ThirdClientOptions resolveOptions(C config) {
    return config.getClientOptions();
}
```

必须传入 `config`，避免 Runtime 创建阶段通过 `getConfig()` 再次进入 `currentRuntime()` 形成递归。

`OutboundPartnerConfig#getClientOptions()` 的接口契约保证返回默认配置，因此 Runtime 创建过程不再重复处理
`ThirdClientOptions` 为空的分支。

## API 精简

### 拟删除内容

`AbstractOutboundClient`：

```java
private ThirdClientOptions options;
private OutboundExecutor executor;

protected AbstractOutboundClient(
        ThirdClientOptions options,
        OutboundExecutor executor);

protected ThirdClientOptions resolveOptions();
protected OutboundExecutor resolveExecutor(ThirdClientOptions options);
```

其中 `resolveExecutor` 不再由 `AbstractOutboundClient` 提供默认实现，但在
`AbstractOutboundPartner` 中继续作为有效扩展点保留。

`AbstractOutboundPartner`：

```java
@Deprecated
protected ThirdClientOptions thirdClientOptions();

@Override
protected ThirdClientOptions resolveOptions();
```

### 拟保留内容

```java
protected AbstractOutboundClient();

protected AbstractOutboundPartner(OutboundExecutorFactory executorFactory);

protected abstract C loadConfig();

protected ThirdClientOptions resolveOptions(C config);

protected OutboundExecutor resolveExecutor(ThirdClientOptions options);

protected Duration configCacheTtl();

protected void clearRuntimeCache();

protected final C getConfig();
```

### 删除双参数构造器的理由

当前已确认的引用项目均通过 `AbstractOutboundPartner` 接入，没有发现直接继承
`AbstractOutboundClient` 并调用双参数构造器的场景。

该构造器表达的是固定 `options/executor` 模式，与 Partner 动态 Runtime 是两种不同生命周期。
保留它会持续增加使用者对状态来源的判断成本。

当前请求模型、配置模型和默认执行器均为 HTTP 语义，该构造器也不足以构成合理的非 HTTP 扩展点。
未来出现非 HTTP 接入需求时，应针对对应协议建立独立抽象。

## 请求流程

### 缓存命中

```text
业务方法
  -> builder/requestJson/requestForm/...
  -> getConfig()/options()/executor()
  -> currentRuntime()
  -> 命中同一个缓存 Runtime
  -> 使用其中的 config/options/executor
```

### 缓存未命中或过期

```text
业务请求
  -> currentRuntime()
  -> 缓存未命中
  -> loadConfig()
  -> resolveOptions(config)
  -> resolveExecutor(options)
  -> 创建新 Runtime
  -> 放入缓存
  -> 执行请求
```

### 主动清理

```java
protected void clearRuntimeCache() {
    Cache<String, OutboundPartnerRuntime<C>> currentCache = runtimeCache;
    if (currentCache != null) {
        currentCache.invalidate(RUNTIME_CACHE_KEY);
    }
}
```

原 `clearConfigCache()` 删除并改名为 `clearRuntimeCache()`，使方法名与实际清理对象一致。

## 一致性与并发边界

### 已保证的一致性

1. Runtime 不会先更新 config、再单独更新 options 或 executor。
2. 新 Runtime 只有在 config、options、executor 全部创建成功后才写入缓存。
3. Runtime 创建失败时不替换已有的有效缓存值。
4. 缓存刷新不修改旧 Runtime，避免影响正在执行的旧请求。
5. `ThirdClientOptions` 进入 Runtime 后按只读配置使用，不支持原地并发修改来实现动态刷新。

### 单次请求跨越刷新边界

当前 builder 和 execute 是两个阶段。请求如果恰好在构建 URL 后、执行前遇到 Runtime 过期或主动清理，
理论上可能出现请求模型使用旧配置、执行器使用新 Runtime 的情况。

本期建议采用最终一致性语义，不为极小的时间边界向 `OutboundRequestInfo` 写入 Runtime 或执行器引用，原因是：

- 默认缓存时间为分钟级，请求构建到执行通常为毫秒级；
- 将 Runtime 绑定到请求会扩大请求模型与 Client 生命周期的耦合；
- 当前目标是解决执行器永久持有旧 HTTP 配置的问题。

如果业务要求单次请求绝对绑定同一配置版本，需要另行设计请求级执行上下文，不在本期范围内。

## Runtime 缓存 TTL 语义

当前约定 `configCacheTtl() <= 0` 表示不缓存。Runtime 模型下，如果每次调用
`getConfig()`、`options()`、`executor()` 都重新创建 Runtime，会在一次请求中多次加载配置和创建执行器，
既不能形成完整快照，也会产生不必要的执行器实例。

改造后统一采用以下语义：

| `configCacheTtl()` 返回值 | Runtime 生命周期 |
| --- | --- |
| `null` | 使用默认 TTL 5 分钟 |
| 正数 | 按指定 TTL 自动过期并在下次访问时延迟重建 |
| 0 或负数 | 固定 Runtime，不自动过期 |

固定 Runtime 仍然是延迟创建：第一次访问时加载配置并创建执行器，之后一直复用同一个 Runtime，
不会在构造阶段初始化。需要刷新时显式调用 `clearRuntimeCache()`，下一次访问再创建新 Runtime。

需要近实时自动刷新时，应配置较短的正数 TTL。该调整将原来的“不缓存”语义改为“固定缓存”，
属于行为不兼容变更，需要在引用项目升级检查中明确确认。

## 异常处理

以下情况在 Runtime 创建阶段快速失败：

- `loadConfig()` 返回 `null`；
- `OutboundExecutorFactory` 为空，无法创建 HTTP 执行器；
- 配置或执行器创建过程中抛出异常。

`resolveOptions(config)` 直接使用 `OutboundPartnerConfig` 保证非空的默认 HTTP 配置：

```java
protected ThirdClientOptions resolveOptions(C config) {
    return config.getClientOptions();
}
```

`resolveExecutor(options)` 默认调用 `OutboundExecutorFactory#create(options)`；当前工厂创建逻辑保证返回
`HttpOutboundExecutor`，无需额外设计空执行器分支。

## 兼容性影响

本次属于有意的源码不兼容调整。以下引用方式需要迁移：

```java
extends AbstractOutboundClient

super(options, executor)

@Override
protected ThirdClientOptions thirdClientOptions()

@Override
protected ThirdClientOptions resolveOptions()
```

正常继承 `AbstractOutboundPartner<C, A>`，并实现 `loadConfig()` 的项目不需要改变业务请求方法；
如果覆盖了无参数 `resolveOptions()`，需调整为：

```java
@Override
protected ThirdClientOptions resolveOptions(C config) {
    // 使用本次加载的 config 解析 options
}
```

建议按破坏性版本发布，并在所有引用项目中执行完整的 `clean compile/test`，不能只依赖启动检查。

## 引用项目检查项

升级前在所有引用项目中搜索：

```text
extends AbstractOutboundClient
extends AbstractOutboundPartner
super(options
super(clientOptions
thirdClientOptions(
resolveOptions(
resolveExecutor(
configCacheTtl(
```

重点确认：

1. 是否存在直接继承 `AbstractOutboundClient` 的 Client。
2. 是否调用已删除的双参数构造器。
3. 是否覆盖 `thirdClientOptions()` 或无参数 `resolveOptions()`。
4. 是否自定义 `resolveExecutor()`，并假定执行器在应用生命周期内只创建一次。
5. 自定义执行器是否持有线程池、连接池等需要主动关闭的资源。
6. 是否将 `configCacheTtl()` 设置为 0 或负数，并确认升级后采用固定 Runtime 语义。
7. 是否在配置进入缓存后原地修改 `ThirdClientOptions`。

## 测试与验收

### 单元测试

1. 构造 Partner 时不调用 `loadConfig()`、`resolveOptions()` 和 `resolveExecutor()`。
2. 第一次请求只创建一次 Runtime。
3. 缓存有效期内多次请求复用同一个 Runtime。
4. 缓存失效后重新加载 config、options 和 executor。
5. `clearRuntimeCache()` 后下一次请求创建新 Runtime。
6. 并发首次访问时，同一个缓存 key 不产生多份有效 Runtime。
7. 新 Runtime 使用新的 host、proxy、timeout、defaultHeaders 和 exchangeLogEnabled。
8. 旧请求持有的旧 Runtime 不被原地修改。
9. `OutboundPartnerConfig` 的默认 HTTP 配置能够直接创建 Runtime。
10. 非正数 TTL 创建固定 Runtime，且不会自动过期。

### 回归测试

1. JSON、FORM、MULTIPART、TEXT、BYTES、QUERY 请求正常执行。
2. 默认 Header 与请求 Header 的覆盖顺序不变。
3. 请求级 timeout 覆盖 Partner 默认 timeout 的规则不变。
4. 自定义响应解析和 `afterResponse()` 行为不变。
5. slf4j 和自定义交换日志行为不变。
6. 所有现有测试通过，Checkstyle 无新增违规。

### 引用项目验收

1. 所有引用项目完成依赖升级并通过 `clean compile`。
2. 有测试的项目通过完整测试。
3. 至少选择一个项目验证配置刷新前后的 host 和 proxy 同步切换。
4. 至少选择一个项目验证调用 `clearRuntimeCache()` 后的延迟重建行为。

## 评审结论

1. 删除 `AbstractOutboundClient` 双参数构造器和固定 Runtime 模式。
2. 删除已废弃的 `thirdClientOptions()`。
3. 将无参数 `resolveOptions()` 替换为 `resolveOptions(C config)`。
4. 接受请求恰好跨越 Runtime 刷新边界时的最终一致性语义。
5. 将 `clearConfigCache()` 改名为 `clearRuntimeCache()`。
6. `configCacheTtl() <= 0` 表示固定 Runtime，不自动过期。
