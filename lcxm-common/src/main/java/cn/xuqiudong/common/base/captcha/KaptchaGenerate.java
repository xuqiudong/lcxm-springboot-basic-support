package cn.xuqiudong.common.base.captcha;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

/**
 * 说明:验证码生成
 *
 * @author Vic.xu
 * @since 2023/5/17/0017 09:47
 */
public class KaptchaGenerate {

    /**
     * 验证码凭证前缀
     */
    private static final String TOKEN_PREFIX = "captcha_";

    /**
     * 缓存在session中的验证码的 key
     */
    private static final String KAPTCHA_SESSION_KEY = "captcha_code_";

    private final DefaultKaptcha defaultKaptcha;

    public KaptchaGenerate(DefaultKaptcha defaultKaptcha) {
        this.defaultKaptcha = defaultKaptcha;
    }

    /**
     *  生成验证码对象
     * @return  Base64CaptchaModel
     * @throws IOException
     */
    public Base64CaptchaModel generateWithoutToken() throws IOException {
        String token = UUID.randomUUID().toString().replace("-", "");
        token = TOKEN_PREFIX + token;
        return generateWithToken(token);
    }

    /**
     *
     * 生成验证码对象
     * @param token 验证码凭证
     * @return Base64CaptchaModel
     * @throws IOException IOException
     */
    public Base64CaptchaModel generateWithToken(String token) throws IOException {
        String text = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        // 对字节数组Base64编码
        Encoder encoder = Base64.getEncoder();
        String base64Image = new String(encoder.encode(outputStream.toByteArray()), StandardCharsets.UTF_8);
        return new Base64CaptchaModel(token, text, "data:image/jpg;base64," + base64Image);
    }


    /**
     * 输出到页面并缓存
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    public void printAndStorage(HttpServletRequest request, HttpServletResponse response){
        String text = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(text);
        //存入session
        request.getSession().setAttribute(KAPTCHA_SESSION_KEY, text);
        response.setHeader("Cache-Control", "no-cache");
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setDateHeader("Expires", 0);

        try(ServletOutputStream os = response.getOutputStream()){
            //输出到页面
            ImageIO.write(image, "jpge", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  校验验证码，并删除
      * @param kaptcha 验证码
     * @param request HttpServletRequest
     * @return true or false
     */
    public boolean checkKaptcha(String kaptcha, HttpServletRequest request){
        if (StringUtils.isBlank(kaptcha) || request == null) {
            return false;
        }
        String cachedKaptcha = request.getSession().getAttribute(KAPTCHA_SESSION_KEY) + "";
        request.getSession().removeAttribute(KAPTCHA_SESSION_KEY);
        return StringUtils.equals(kaptcha, cachedKaptcha);

    }
}