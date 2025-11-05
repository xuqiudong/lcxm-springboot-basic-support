package cn.xuqiudong.common.base.srpc.protocol;

import cn.xuqiudong.common.base.srpc.model.Invoker;

/**
 * 描述: 协议 ， 默认通过netty传输
 * @author Vic.xu
 * @since 2024-06-25
 */
public interface Protocol {




    /**
     * 客户端发送消息给服务端
     * @param invoker 包含 请求地址以及请求参数
     * @exception  Exception  throwable
     */
    Object send(Invoker invoker) throws Exception;

}
