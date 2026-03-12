/**
 * srpc即simple rpc
 * 需求：
 * 1. 简单的内部系统之间的接口相互调用，没有特别复杂的逻辑
 * 2. 简单的封装，方便使用，有rpc的意思，但是不需要太多功能
 * 3. 参照我原本的写过的rpc: https://gitee.com/xuqiudong/xqd-rpc 进行更简单的实现
 * 功能：
 * 1. 不需要注册中心，不需要监听provider
 * 2. provider 和consumer 都要注册到spring：自定义注解
 * 3. provider 和consumer 使用相同的接口： provider实现逻辑，consumer直接注入到需要调用的地方即可
 *
 */
package cn.xuqiudong.basic.core.srpc;
/**
 *  流程：
 *  1. provider 端，被自定义注解@SrpcService 标注的bean，注册到spring, 并被XqdServiceHolder持有
 *     1.1 入口类 XqdSpringProviderBeanProcessor
 *     1.2 处理客户端请求入口：SimpleRpcController
 *     1.3 HttpServerHandler 处理请求并响应
 *  2. consumer 端，注入的field被 @SrpcReference标注的bean，生成代理类注入
 *     2.1 入口类：SimpleRpcSpringReferenceBeanProcessor
 *     2.2 XqdBeanFactory bean工厂类
 *     2.3 JdkProxyInvocation 代理类执行远程调用过程
 *  3. 配置类入口 SrpcrAutoConfiguration
 *     3.1 bean： SimpleRpcSpringReferenceBeanProcessor  织入消费端逻辑
 *     3.2 bean： XqdSpringProviderBeanProcessor         织入提供端逻辑
 *     3.3 bean: SimpleRpcController                     织入控制层
 *     3.4 抽象方法：SrpcRequestUrl                        服务地址和设置session等信息
 *
 *
 */