package cn.xuqiudong.basic.framework.captcha;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 说明:  base64图片验证码
 *
 * @author Vic.xu
 * @since 2023/5/17/0017 09:42
 */

public class Base64CaptchaModel {

    /**
     * 对应验证码的凭证
     */
    private String token;

    /**
     * 验证码
     */
    @JsonIgnore
    private String code;
    /**
     * base64验证码图片(地址)
     */
    private String src;



    public Base64CaptchaModel() {
        super();
    }
    public Base64CaptchaModel(String token, String code, String src) {
        super();
        this.token = token;
        this.code = code;
        this.src = src;
    }
    public String getToken() {
        return token;
    }
    public String getCode() {
        return code;
    }
    public String getSrc() {
        return src;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setSrc(String src) {
        this.src = src;
    }


}