# lcxm-basic-srpc

Simple RPC 基础模块。

`lcxm-basic-srpc` 提供一套基于 HTTP + 序列化 + JDK 动态代理的轻量 RPC 能力，包含服务暴露、引用注入、协议请求、序列化和 Spring 接入配置。

该模块适合内部系统间轻量调用；如果需要服务治理、负载均衡、熔断、注册中心等完整能力，应优先选择成熟 RPC 框架。

## 主包 Tree

```text
cn.xuqiudong.basic.srpc
|-- annotation             # @SrpcService、@SrpcReference、@SrpcMethod
|-- constant               # RPC URL、协议常量
|-- controller             # SimpleRpcController，接收 RPC HTTP 请求
|-- model                  # 请求、响应、调用元数据、URL 模型
|-- protocol               # HTTP 客户端、服务端处理器、协议接口
|-- provider               # 服务提供方 Bean 扫描和服务持有器
|-- proxy                  # 代理工厂
|   `-- jdk                # JDK 动态代理调用实现
|-- reference              # 服务引用扫描、代理注入、FactoryBean
`-- serializer             # 序列化接口和实现
    |-- hessian            # Hessian2 序列化，含 Java Time 扩展
    `-- json               # JSON 序列化实现
```

## 主要能力

| 能力 | 说明 |
| --- | --- |
| 服务暴露 | `@SrpcService` 标记服务，由 `XqdSpringProviderBeanProcessor` 注册到本地服务持有器。 |
| 服务引用 | `@SrpcReference` 标记字段，由 `SimpleRpcSpringReferenceBeanProcessor` 注入代理。 |
| HTTP 协议 | `HttpProtocol` 发起请求，`SimpleRpcController` 接收请求，`HttpServerHandler` 执行本地调用。 |
| JDK 动态代理 | `JdkProxyFactory`、`JdkProxyInvocation` 将接口方法调用转换为 RPC 请求。 |
| Hessian 序列化 | 默认使用 `Hessian2Serializer`，补充 Java Time 类型支持。 |
| 字段级引用配置 | `@SrpcReference` 字段可配置不同服务编码、超时时间等参数。 |

## Maven

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-srpc</artifactId>
    <version>3.5.0-jdk21-3.0.0</version>
</dependency>
```

## Spring 接入

业务项目提供一个配置类，继承 `SrpcrAutoConfiguration`，并按需重写远端地址、会话信息等配置。

```text
业务配置类
  -> extends SrpcrAutoConfiguration
  -> 注册 SimpleRpcSpringReferenceBeanProcessor
  -> 注册 XqdSpringProviderBeanProcessor
  -> 注册 SimpleRpcController
```

## 调用流程

消费端：

```text
业务 Bean 字段标注 @SrpcReference
  -> SimpleRpcSpringReferenceBeanProcessor 扫描字段
  -> XqdBeanFactory 创建 JDK 代理
  -> 调用接口方法
  -> JdkProxyInvocation
  -> HttpProtocol
  -> 发送 XqdRequest
```

提供端：

```text
业务服务标注 @SrpcService
  -> XqdSpringProviderBeanProcessor
  -> XqdServiceHolder 注册服务
  -> SimpleRpcController 接收请求
  -> HttpServerHandler 反序列化并调用目标方法
  -> 返回 XqdResponse
```

## 依赖边界

适合放入本模块：

- RPC 注解、代理、协议、序列化。
- 简单 HTTP RPC 请求和响应模型。
- Spring 环境下的服务暴露和引用注入处理器。

不适合放入本模块：

- 具体业务接口和实现。
- 业务鉴权、租户、审计落库。
- 注册中心、服务发现、熔断、限流等完整治理能力。

## 注意点

- 当前实现是轻量内部 RPC，不等价于 Dubbo、gRPC 等完整框架。
- 默认协议基于 HTTP，默认序列化偏向 Hessian2。
- `@SrpcReference` 支持字段级差异化配置，但代理对象不应承载业务状态。
- 对外暴露 RPC 前，应由业务项目补齐鉴权、网络边界和日志审计策略。
