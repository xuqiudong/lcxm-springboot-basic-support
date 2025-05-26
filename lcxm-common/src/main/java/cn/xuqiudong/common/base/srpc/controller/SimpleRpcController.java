package cn.xuqiudong.common.base.srpc.controller;

import cn.xuqiudong.common.base.srpc.constant.SimpleRpcConstant;
import cn.xuqiudong.common.base.srpc.protocol.HttpServerHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述:
 *      simple rpc  消费端和服务端的交互controller
 * @author Vic.xu
 * @since 2024-06-25 9:56
 */
@RestController
public class SimpleRpcController {

    @PostMapping(value = SimpleRpcConstant.SIMPLE_RPC_URL)
    public void request(HttpServletRequest request, HttpServletResponse response) {
        HttpServerHandler.handle(request, response);
    }
}
