package cn.xuqiudong.basic.core.web.filter.model;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 描述:
 * http basic  token
 * 认证头格式： Authorization: Basic <credentials> (Base64解码后的格式应为username:password。)
 * https://rfc2cn.com/rfc7617.html
 *
 * @author Vic.xu
 * @since 2024-09-03 11:34
 */
public class BasicToken {

    private static final Pattern REGEX_BASIC = Pattern.compile("^Basic\\s([A-Za-z0-9+/=]+)$");

    private static final String BASIC = "Basic ";

    private static final String COLON = ":";

    /**
     * 认证主体 ,即账号
     */
    private final String subject;

    /**
     * 认证秘钥，即密码或签名等
     */
    private final String credential;

    public BasicToken(String subject, String credential) {
        Assert.hasText(subject, "subject is null");
        Assert.isTrue(!subject.contains(COLON), "subject must not contains :");
        Assert.hasText(credential, "credential is null");
        this.subject = subject;
        this.credential = credential;
    }

    /**
     * 判断字符串是否是http basic 格式
     *
     * @param authorization authorization
     * @return matches
     */
    public static boolean isBasicToken(String authorization) {
        return StringUtils.isNotBlank(authorization) && authorization.startsWith(BASIC) && REGEX_BASIC.matcher(authorization).matches();
    }

    /**
     * 解析 BasicToken
     * @param authorization authorization
     * @return BasicToken
     */
    public static BasicToken decodeBasicToken(String authorization) {
        if (!isBasicToken(authorization)) {
            return null;
        }
        String encodedCredentials = authorization.substring(BASIC.length());
        String decodedCredentials = new String( Base64.decodeBase64(encodedCredentials), StandardCharsets.UTF_8);
        StringTokenizer tokenizer = new StringTokenizer(decodedCredentials, COLON);
        String username = tokenizer.nextToken();
        String password = tokenizer.nextToken();
        return new BasicToken(username, password);
    }


    public String getSubject() {
        return subject;
    }

    public String getCredential() {
        return credential;
    }
}
