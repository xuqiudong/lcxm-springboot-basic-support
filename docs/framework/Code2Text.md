## Code2Text 框架摘要

### 目的

在返回给前端的接口中，把code值转化为对应的文本，减少业务层的连表查询或者业务处理。
比如：

1. 通用枚举值处理
2. 通用参数项转化
3. 常见外键处理，比如用户、部门

实现方案：基于jackson的序列化时候，插入额外字段，表示其文本值。

考虑到性能， 框架层面提供至少2层缓存 比如redis(可选) + 本地（caffeine）以提升效率
另外还需要缓存的更新（事件通知、redis订阅等），缓存预热。

后续扩展： 比如在excel导入的时候(或者其他必要的地方)，会根据对象字段上的注解，进行反向处理

### 序列化部分：

### 注解 Code2Text

> 定义所有编码转文本场景的通用契约，不直接使用，由子注解继承

### 序列化逻辑：Code2TextSerializer extends JsonSerializer<Object> implements ContextualSerializer

只负责序列化
> 定义 序列化逻辑，找到字段上的注解，然后找到注解对应的解析器，解析文本，追加json字段

### Code2TextResolver

> 处理code 和text 的转换 的核心接口， 每个子注解对应一个解析器实现

### Code2TextResolverRegistry 核心模块 负责装配 Resolver

> 注册解析器 ，在序列化阶段，会根据注解的value值，找到对应的解析器
> 注解解析器的时候会根据配置生成 把解析器包装为 CachedResolverProxy
> 在包装为 CachedResolverProxy的时候 通过CacheRegionFactory 创建 CacheRegion 注册到 Code2TextCacheManager

### CachedResolverProxy： 缓存代理

- 代理Code2TextResolver： 只负责转发 + 缓存入口

### DefaultCacheRegionFactory

创建 CacheRegion：

- 本地缓存 ： CaffeineCacheRegion 必定存在
- 远程缓存： RedisCacheRegion
- 组合缓存： CompositeCacheRegion （当配置redis的时候 创建CompositeCacheRegion： CaffeineCacheRegion + RedisCacheRegion）

### Code2TextCacheManager

> 缓存管理器 : 持有缓存只负责 Region 管理，提供缓存的各种处理（ 失效调度、预热）

- getOrLoad 获取并创建缓存
- invalidate 单个是缓存失效
- invalidateAll 缓存全部失效
- registerRegion 注册缓存Region
- preload 预加载缓存

### Code2TextPreloadRunner 缓存预加载执行器

- 利用spring的ApplicationRunner 启动时
- 通过Code2TextResolverRegistry 获取所有解析器，如果实现了Code2TextPreloadable，则调用 preload，然后通过Code2TextCacheManager
  加载到缓存中

### CacheRegion: 缓存细节：

- 本地缓存、 Redis、TTL、双删、预热、key 级 / region 级失效

### 静态工具类：

- Code2TextCacheHelper:清除缓存，通过发布spring事件触发
    1. spring监听事件： 删除本地和redis
    2. 发布redis监听事件， 其他节点监听 到后删除本地缓存
- Code2TextHelper: 静态方法获取 code 对应的 text (后续加反向)
    - 通过Code2TextResolverRegistry 获取解析器，调用解析器解析




## 模块划分（预计）
code2text-core

- 注解
- Resolver API
- Registry
- Cache SPI

code2text-cache

- Caffeine 实现
- Redis 实现

code2text-spring-boot-starter

- 自动配置
- Redis listener
- Bean 装配

----
