package cn.xuqiudong.basic.third.demo.outbound.partner.demo.model;

import lombok.Data;

/**
 * 出站 demo 第三方响应包装。
 */
@Data
public class DemoThirdResponse<T> {

    private String code;

    private String message;

    private T data;

    public boolean success(String successCode) {
        return successCode != null && successCode.equals(code);
    }
}
