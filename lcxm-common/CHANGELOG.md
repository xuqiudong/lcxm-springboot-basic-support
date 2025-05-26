## 版本说明
### maven坐标
[Maven Central: 搜索本项目](https://central.sonatype.com/search?q=cn.xuqiudong.basic)
```
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-common</artifactId>
    <version>${version}</version>
</dependency>
```
### changelog 格式优化
```
1. 版本号规范化（SemVer + 日期）
2. 变更条目分类标签: [新增]/[修复]/[优化]/[安全]等）
3. 日期统一前置
4. 重要变更高亮
5. 待办事项与已完成分离
6. 模块分类（可选）： - [RPC][优化] SRPC 连接池配置  
7 . 关联 Issue/Commit（如果适用）：
demo:
### 1.0.6 - 2025-03-28  
- 2025-03-12 [新增] 加入 `BaseGenericEntity` 泛型基类  
- 2025-02-08 [安全] 重定向地址白名单过滤 `SafelyRedirectFilter`  
> **注意**: 需检查所有 `redirect:` 调用是否在白名单内。  
### 1.0.5 - 2024-12-10  
- 2024-12-09 [优化] Lookup 类 `sortOrder` 改为枚举类型  
- 2024-10-29 [修复] AsyncOperation 添加同步锁  
```
### next version

###  1.1.0-2025  2025-03-28
- Lookup  sortOrder 字段不限制大小写
- (Select2Vo): 移除pid的JsonIgnore
- 2025-01-14 新增RequestLoggerFilter，记录请求时长
- 2025-02-05 Select2VO 添加additional 属性
- 2025-02-08 重定向之前对重定向地址进行白名单过滤 `SafelyRedirectFilter`
- 2025-02-27 SRPC 中 JdkProxyInvocation的Invoker变量修改为每次新建，防止变量共享导致数据错乱
- 2025-03-12 加入 BaseGenericEntity BaseGenericMapper BaseGenericService BaseGenericController 用以支持实体id的泛型

### 1.0.5-2024
> 2024-12-10
- 2024-10-25 JsonUtil 提供对 Java 8 java.time 日期/时间 API 的支持;新增toJsonWithAllField方法
- 2024-10-29 AsyncOperation#notifyNewPut加上同步锁
- 2024-12-04 srpc的一些优化：http连接池，注册SrpcService的时候获取原始class，接口中忽略Object方法的远程调用
- 2024-12-09 Lookup类 优化 @JsonIgnore 修改为@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)；sortOrder的set方法修改为枚举，防止注入
- 
### 1.0.4-2024
- 2024-10-12
- 修复spotbugs 警告
- CalcDiff4EntityRelationsUtil: 实现实体对象（主要
### 1.0.2-2024
- 2024-06-05是级联对象）之间的差异计算

- 版本管理继承`lcxm-springboot-parent`

### 1.0.1-2024
- 2024-05-22
- 移除对`redisson-spring-boot-starter`依赖的传递，设置为`optional`
  - 因为这个依赖会自动装配RedisTemplate，要求项目必须引入redis的配置，且引入了`spring-boot-starter-actuator`
  - 导致不需要引入redis等组件的项目必须要额外的进行一些配置，以消除启动报错，比如：
    - `@SpringBootApplication(exclude = {RedissonAutoConfiguration.class}`
    - `Management.endpoints.enabled-by-default=false`
- 把其他非必要组件也设置为 为`optional`
- 这些组件到时候在使用到的项目内按需引入即可


### 1.0.0
- 2024-05-21
- 初始提交，来源于以下两个项目的合并：
  -  [lcxm-common-util](https://gitee.com/xuqiudong/boot-support/tree/master/lcxm-common-util)
  -  [lcxm-common-base](https://gitee.com/xuqiudong/boot-support/tree/master/lcxm-common-base)



