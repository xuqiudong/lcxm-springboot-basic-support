package cn.xuqiudong.common.base.third.login.service;

import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.common.base.third.login.model.TokenSignModel;
import cn.xuqiudong.common.base.tool.Tools;
import cn.xuqiudong.common.base.vo.BooleanWithMsg;
import cn.xuqiudong.common.util.JsonUtil;
import cn.xuqiudong.common.util.encrypt.RsaUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * description：
 * 自动登录service 主要是check sign  ,generate token,  check token <br />
 * <p>
 * 自动登录流程:
 * 1. 第三方生成sign, 携带sign 获取token
 * 2. 通过token 登录
 *
 * @author Vic.xu
 * @see #main(String[])  生成sign
 * @see #getToken(String, String) 校验 sign
 * @see #checkToken(String, String)  校验 token
 *
 * </p>
 * @since 2025-08-28 13:42
 */
public abstract class AbstractAutoLoginService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 请求时间最大的时间跨度(ms)：5分钟 5 * 60 *1000；
     */
    private static final long MAX_TIME_SPAN = 5 * 60 * 1000L;

    /**
     * 存储oa的token的前缀 oa-token:token=userId
     */
    public static final String TOKEN_KEY_PREFIX = "autoLogin-token:";
    /**
     * 存储oa的token的过期时间 秒： 10分钟
     */
    public static final long TOKEN_EXPIRE_SECONDS = 600L;

    private RedisTemplate<String, Object> redisTemplate;


    public AbstractAutoLoginService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 校验sign,获取token
     *
     * @param username username
     * @param sign     sign (生成方式参考本类的main方法)
     * @return token in redis
     * @see #main(String[])
     */
    public BaseResponse<String> getToken(String username, String sign) {
        try {
            // 公钥解密
            String publicDecryptJson = RsaUtils.publicDecrypt(sign, getPublicKey());
            TokenSignModel model = JsonUtil.jsonToObject(publicDecryptJson, TokenSignModel.class);
            if (model == null) {
                return BaseResponse.error("invalid sign");
            }
            if (!isLegalAppId(model.getAppId())) {
                return BaseResponse.error("invalid appId");
            }
            if (!StringUtils.equals(username, model.getUsername())) {
                return BaseResponse.error("invalid username");
            }
            if (System.currentTimeMillis() - model.getTimestamp() > MAX_TIME_SPAN) {
                return BaseResponse.error("request timeout");
            }
            boolean isLegalUsername = isLegalUsername(username);
            if (!isLegalUsername) {
                return BaseResponse.error("invalid username");
            }
            String token = generateToken(username);
            return BaseResponse.success(token);
        } catch (Exception e) {
            logger.error("check sign error : {}", ExceptionUtils.getMessage(e));
            return BaseResponse.error("check sign error :" + ExceptionUtils.getMessage(e));
        }

    }


    /**
     * 检测token的合法性
     *
     * @param token    token
     * @param username 当前token 对应的username
     * @return
     */
    public BooleanWithMsg checkToken(String token, String username) {
        String cachedUsername = getUsernameFromToken(token);
        if (StringUtils.equalsIgnoreCase(cachedUsername, username)) {
            removeToken(token);
            return BooleanWithMsg.success().setMessage(username);
        }
        return BooleanWithMsg.error("invalid token");
    }

    /**
     * 检验成功 移除token
     */
    private boolean removeToken(String token) {
        String key = getTokenKey(token);
        return redisTemplate.delete(key);
    }

    /**
     * 生成并存储token
     *
     * @param username username
     * @return token in redis
     */
    private String generateToken(String username) {
        String token = Tools.randomUuid();
        String key = getTokenKey(token);
        redisTemplate.opsForValue().set(key, username, TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return token;
    }


    /**
     * 通过token 获取username
     *
     * @param token token
     * @return username
     */
    private String getUsernameFromToken(String token) {
        return (String) redisTemplate.opsForValue().get(getTokenKey(token));
    }

    /**
     * 获取存储token的key
     *
     * @param token token
     * @return TokenKey
     */
    private String getTokenKey(String token) {
        return TOKEN_KEY_PREFIX + token;
    }


    /* *********************** abstract method  below ************************************** */

    /**
     * 是否是合法的用户名: 是否存在于数据库
     *
     * @param username username
     */
    public abstract boolean isLegalUsername(String username);

    /**
     * 是否是合法的appId
     *
     * @param appId appId
     */
    public abstract boolean isLegalAppId(String appId);

    public abstract String getPublicKey();

    /* *********************** abstract method  above ************************************** */


    public static void main(String[] args) {
        RsaUtils.RsaKeyPair keys = RsaUtils.createKeys(1024);
        String PUBLIC_KEY = keys.getPublicKey();
        String PRIVATE_KEY = keys.getPrivateKey();
        TokenSignModel model = new TokenSignModel("zhangsan", "0123456");
        String json = JsonUtil.toJson(model);
        System.out.println("原始json:" + json);

        String sign = RsaUtils.privateEncrypt(json, PRIVATE_KEY);
        System.out.println("私钥加密 sign:\n" + sign);
        String publicDecryptJson = RsaUtils.publicDecrypt(sign, PUBLIC_KEY);
        System.out.println("公钥解密json:" + publicDecryptJson);
        System.out.println("原始json是否和解密后的json相等:" + json.equals(publicDecryptJson));
        TokenSignModel model2 = JsonUtil.jsonToObject(publicDecryptJson, TokenSignModel.class);
        System.out.println("解密后的json对应的原始对象:" + JsonUtil.toJson(model2));

    }

}
