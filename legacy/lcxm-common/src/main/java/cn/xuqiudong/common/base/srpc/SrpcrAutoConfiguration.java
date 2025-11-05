package cn.xuqiudong.common.base.srpc;

import cn.xuqiudong.common.base.srpc.annotation.SrpcReference;
import cn.xuqiudong.common.base.srpc.controller.SimpleRpcController;
import cn.xuqiudong.common.base.srpc.model.SrpcRequestUrl;
import cn.xuqiudong.common.base.srpc.provider.XqdSpringProviderBeanProcessor;
import cn.xuqiudong.common.base.srpc.reference.SimpleRpcSpringReferenceBeanProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 描述:
 *    grpc 的入口类，
 *    在需要使用的项目继承此类，标注为@Configuration,并重写相关方法
 * @author Vic.xu
 * @since 2024-06-25 15:00
 */

public abstract class SrpcrAutoConfiguration {


    public static SrpcRequestUrl srpcRequestUrl;

    /**
     * 为 通过{@link SrpcReference} 引用的类的动态注入 远程调用 代理类
     */
    @Bean
    @ConditionalOnMissingBean
    public SimpleRpcSpringReferenceBeanProcessor simpleRpcSpringReferenceBeanProcessor() {
        srpcRequestUrl = requestUrl();
        return new SimpleRpcSpringReferenceBeanProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public XqdSpringProviderBeanProcessor xqdSpringProviderBean() {
        return new XqdSpringProviderBeanProcessor();
    }


    /**
     * simple rpc controller 控制器
     */
    @Bean
    @ConditionalOnMissingBean
    public SimpleRpcController simpleRpcController(){
        return new SimpleRpcController();
    }

    public abstract SrpcRequestUrl requestUrl();
}
