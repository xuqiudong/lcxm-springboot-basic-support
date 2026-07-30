package cn.xuqiudong.basic.third.demo.outbound.partner.demo.model;

import lombok.Data;

/**
 * 出站 demo 请求模型。
 */
@Data
public class DemoRequest {

    private String orderNo;

    private String amount;
}
