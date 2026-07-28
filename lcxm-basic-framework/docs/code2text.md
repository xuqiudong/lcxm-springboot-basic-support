# Code2Text 框架说明

## 一、项目背景

在业务系统中，接口返回数据时经常需要将：

- 枚举 code 转换为名称
- 参数项 code 转换为描述
- 外键 id 转换为对象名称（如用户、部门）

如果在业务层完成这些转换，通常会导致：

- 大量连表或重复查询
- Service 层包含展示层逻辑
- 列表接口容易出现 N+1 查询问题

Code2Text 框架的目标是：

> 在 JSON 序列化阶段自动完成 code → text 的转换，  
> 业务层只返回原始 code，文本转换由框架统一处理。

---

## 二、核心能力

1. 基于 Jackson 序列化扩展，零侵入业务接口
2. 注解驱动的字段级文本增强
3. Resolver 插件化扩展，支持多种转换来源
4. 本地 Caffeine + Redis 两级缓存（可选）
5. 启动预加载与运行期主动失效机制
6. 支持非序列化场景下的手动解析

---

## 三、使用方式概览

使用流程分为三步：

1. 在字段上添加 Code2Text 子注解
2. 提供对应的 Resolver 实现
   实现 `Code2TextResolver` 接口
   如果预加载缓存数据，则同时实现 `Code2TextPreloadable`
3. 引入 starter 即可生效
  参见入口类：cn.xuqiudong.common.base.code2text.Code2TextAutoConfiguration

序列化时框架会自动追加对应的 `xxxText` 字段。

---

## 四、整体工作流程

当接口返回对象进行 JSON 序列化时：

1. Jackson 调用字段的 JsonSerializer
2. Code2TextSerializer 发现字段存在 Code2Text 注解
3. 根据注解类型从 ResolverRegistry 获取 Resolver
4. Resolver 经过 CachedResolverProxy 进入缓存层
5. 缓存未命中时调用真实 Resolver 加载数据
6. 返回文本结果并写入 JSON 扩展字段

业务代码不需要参与任何转换过程。

---

## 五、核心设计与组件说明

### 5.1 注解体系

父注解：

- `@Code2Text`  
  作为统一标识，不直接使用

子注解：

- 表达具体业务语义，如枚举、字典、外键等
- 每种子注解对应一个 Resolver 实现

---

### 5.2 Code2TextSerializer

职责：

- 识别字段上的 Code2Text 注解
- 根据注解定位 Resolver
- 调用 Resolver 获取文本
- 将文本字段追加到 JSON 输出中

不包含业务查询逻辑，也不涉及缓存实现。

---

### 5.3 Code2TextResolver

核心扩展接口，用于定义 code → text 的转换规则。

特点：

- 每种业务转换方式实现一个 Resolver
- Resolver 只关心如何获取文本，不关心缓存

可选扩展：

- 实现 `Code2TextPreloadable` 接口以支持缓存预加载

---

### 5.4 Code2TextResolverRegistry

Resolver 注册与查找中心，负责：

- 收集并注册所有 Resolver
- 建立注解类型与 Resolver 的映射关系
- 为 Resolver 创建 CachedResolverProxy
- 提供统一 Resolver 查询入口

业务代码和 Serializer 均不直接依赖具体 Resolver 实现。

---

### 5.5 CachedResolverProxy

Resolver 的统一代理层，负责：

- 接管所有解析请求
- 执行缓存查询与回写
- 屏蔽缓存实现细节

真实 Resolver 不感知缓存存在。

---

### 5.6 CacheRegion 抽象 和 CacheRegionBundle

缓存实现 SPI 接口，用于封装具体缓存策略：

- 本地缓存：Caffeine
- 分布式缓存：Redis
- 两级缓存：CompositeCacheRegion

支持能力：

- key 级缓存
- region 级整体失效
- TTL 控制
- 预加载写入

CacheRegionBundle ： 同时绑定 code -> text 和 text -> code的缓存（CacheRegion）。
---

### 5.7 Code2TextCacheManager

缓存统一管理入口，负责：

- 注册 CacheRegion
- 提供缓存获取与加载能力
- 触发缓存失效
- 协调预加载流程

框架所有缓存操作均通过该组件完成。

---

### 5.8 缓存预加载机制

Resolver 可实现 `Code2TextPreloadable` 接口：

- 在应用启动阶段由 PreloadRunner 调用
- 批量加载数据并写入缓存
- 避免首次请求触发大量查询

适用于枚举、参数字典等稳定数据场景。

---

## 六、工具类说明

### 6.1 Code2TextCacheHelper

缓存失效统一入口工具类，用于运行期刷新缓存。

能力：

- 清理指定 Resolver 对应的缓存 region
- 发布 Spring 事件
- 触发 Redis 广播
- 通知其他节点清理本地缓存

典型使用场景：

- 字典配置变更
- 用户或部门信息更新
- 组织结构调整

---

### 6.2 Code2TextHelper

非序列化场景下的文本解析工具类。

适用场景：

- Excel 导出
- 日志记录
- 手工构造 VO 对象

内部流程：

- 从 ResolverRegistry 获取 Resolver
- 通过 CachedResolverProxy 进入缓存体系
- 返回最终文本结果

后续可扩展支持反向解析（text → code）。

---

## 七、整体架构

┌─────────────────────────────────────────────┐
│                Business API                 │
│            Controller / Service             │
└───────────────────────▲─────────────────────┘
                        │
┌───────────────────────┴─────────────────────┐
│              Jackson Serialization           │
│             Code2TextSerializer              │
└───────────────────────▲─────────────────────┘
                        │
┌───────────────────────┴─────────────────────┐
│        Resolver Dispatch & Proxy Layer       │
│   Code2TextResolverRegistry                  │
│   CachedResolverProxy                        │
└───────────────▲───────────────▲─────────────┘
                │               │
┌───────────────┴───────┐ ┌─────┴────────────────┐
│     Cache Manager     │ │     Resolver Impl     │
│ Code2TextCacheManager │ │ Enum / Dict / DB etc  │
└───────────────▲───────┘ └──────────▲───────────┘
                │                    │
┌───────────────┴────────────────────┴─────────────┐
│     CacheRegionBundle(CacheRegion) SPI           │
│   CaffeineCache | RedisCache | CompositeCache    │
└──────────────────────────────────────────────────┘

缓存预热流程：
Spring Boot Startup
|
v
Code2TextPreloadRunner
|
v
ResolverRegistry.getAllResolvers()
|
v
for each Preloadable Resolver
|
v
Resolver.preload()
|
v
Code2TextCacheManager.putAll()
|
v
CacheRegion (Caffeine / Redis)


缓存刷新：
Business Update
|
v
Code2TextCacheHelper.invalidate(resolverType)
|
v
Publish Spring Event
|
+--> Local Listener -> clear local cache
|
+--> Publish Redis Message
|
v
Other Node Listener
|
v
clear local cache



## 九、设计原则

1. 序列化层不包含业务逻辑
2. Resolver 不感知缓存细节
3. 缓存层不依赖业务语义
4. 所有扩展能力通过接口完成

Code2Text 目标是成为可复用、可扩展、可维护的通用框架能力。
