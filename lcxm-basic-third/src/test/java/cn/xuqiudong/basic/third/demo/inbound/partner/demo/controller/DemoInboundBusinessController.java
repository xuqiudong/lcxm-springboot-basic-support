package cn.xuqiudong.basic.third.demo.inbound.partner.demo.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 入站 demo 业务接口。
 *
 * <p>第三方先调用默认 token 接口获取 token，再携带 X-Third-Token 访问该接口。</p>
 */
@RestController
@RequestMapping("/demo/third/inbound")
public class DemoInboundBusinessController {

    @PostMapping("/submit")
    public Map<String, Object> submit(@RequestBody Map<String, Object> body) {
        return Map.of("success", true, "received", body);
    }
}
