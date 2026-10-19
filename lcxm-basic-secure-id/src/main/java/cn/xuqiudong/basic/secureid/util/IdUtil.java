package cn.xuqiudong.basic.secureid.util;

import cn.hutool.core.codec.Base62;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * 对于id的加解密操作, 具体应用可按需设置盐值    #setSaltSupplier
 *
 * @author Vic.xu
 * @since 2026-08-20 15:00
 */
public class IdUtil {


    //id 前缀标示 RandomStringUtils.randomAlphabetic(5)
    private static final String ID_PREFIX = "WdLPex";
    //id 后缀标示
    private static final String ID_SUFFIX = "xgIyHU";

    /**
     * 匹配字符串中被固定前后缀包裹的 Base62 密文，并通过唯一捕获组提取密文载荷。
     * 载荷使用非贪婪匹配以停在最近的后缀，同时限定字符范围，避免跨越逗号、JSON 标点等普通内容。
     */
    private static final String ENCRYPTED_REGEX = Pattern.quote(ID_PREFIX)
            + "([0-9A-Za-z]+?)"
            + Pattern.quote(ID_SUFFIX);

    /** 支持在单个密文、多个密文以及包含普通文本的字符串中逐段查找密文。 */
    private static final Pattern ENCRYPTED_PATTERN =
            Pattern.compile(ENCRYPTED_REGEX, Pattern.CASE_INSENSITIVE);


    //默认加密盐值
    private static final String DEFAULT_SALT = "secure-id";


    /**
     * Salt Supplier : 项目自行处理
     */
    private static Supplier<String> saltSupplier = () -> DEFAULT_SALT;

    /**
     * 设置加密盐值
     */
    public static void setSaltSupplier(Supplier<String> saltSupplier) {
        IdUtil.saltSupplier = saltSupplier;
    }

    /**
     * 判断两个id是否相等:  空 用不相等,空字符串也是
     *
     * @return
     */
    public static boolean equals(String id1, String id2) {
        if (StringUtils.isEmpty(id1) || StringUtils.isEmpty(id2)) {
            return false;
        }
        return decrypt(id1).equals(decrypt(id2));
    }

    /**
     * 加密id,只针对未加密的 先加密再包裹
     */
    public static String encrypt(String id) {
        if (StringUtils.isNotEmpty(id) && !isEncrypted(id)) {
            return wrapId(encodeDataInfo(id, getSalt()));
        }
        return id;
    }

    /**
     * 解密字符串中所有被固定前后缀包裹的 ID 密文。
     * <p>
     * 统一采用逐段查找和替换，因此支持单个密文、分隔符连接的多个密文，以及密文与普通文本混合的场景；
     * 未包含密文的字符串保持不变。
     *
     * @param id 待处理的字符串
     * @return 解密并替换全部密文后的字符串；未包含密文时返回原值
     */
    public static String decrypt(String id) {
        if (isEncrypted(id)) {
            return replacementAllHanderString(id, ENCRYPTED_REGEX, str -> decodeDataInfo(str, getSalt()));
        }
        return id;
    }

    /**
     * 加密之前包裹id
     */
    private static String wrapId(String id) {
        return new StringBuffer(ID_PREFIX).append(id).append(ID_SUFFIX).toString();
    }

    /**
     * 判断字符串中是否包含符合固定前后缀及 Base62 载荷格式的密文。
     *
     * @param src 待检查的字符串
     * @return 包含至少一个密文时返回 {@code true}
     */
    private static boolean isEncrypted(String src) {
        return StringUtils.isNotEmpty(src) && ENCRYPTED_PATTERN.matcher(src).find();
    }

    private static String getSalt() {
        String salt;
        try {
            salt = saltSupplier.get();
        } catch (Exception e) {
            salt = DEFAULT_SALT;
        }
        return salt;
    }


    /**
     * 带盐值的base62加密
     */
    private static String encodeDataInfo(String srcData, String salt) {
        byte[] xorArray = salt.getBytes(StandardCharsets.UTF_8);
        byte[] srcInfo = srcData.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < srcInfo.length; i++) {
            for (int j = 0; j < xorArray.length; j++) {
                srcInfo[i] = (byte) (srcInfo[i] ^ xorArray[j]);
            }
        }
        return Base62.encode(srcInfo);

    }

    /**
     * 带盐值的base62解密
     */
    private static String decodeDataInfo(String dicData, String salt) {
        byte[] xorArray = salt.getBytes(StandardCharsets.UTF_8);
        byte[] dicInfo = Base62.decode(dicData);
        for (int i = 0; i < dicInfo.length; i++) {
            for (int j = 0; j < xorArray.length; j++) {
                dicInfo[i] = (byte) (dicInfo[i] ^ xorArray[j]);
            }
        }
        return new String(dicInfo, StandardCharsets.UTF_8);
    }

    /**
     * 把map中指定的key对应的value加密  Vic.xu
     */
    public static <T extends Map<String, ? super String>> void encryptKey(T map, String key) {
        if (map == null || map.isEmpty() || StringUtils.isEmpty(key)) {
            return;
        }
        String value = map.get(key) + "";
        if (StringUtils.isNotBlank(value)) {
            map.put(key, encrypt(value));
        }
    }

    /**
     * 把map中指定的"id"对应的value加密
     */
    public static <T extends Map<String, ? super String>> void encryptKey(T map) {
        encryptKey(map, "id");
    }

    /**
     * 把map中指定的key对应的value解密
     *
     */
    public static <T extends Map<String, ? super String>> void decryptKey(T map, String key) {
        if (map == null || map.isEmpty() || StringUtils.isEmpty(key)) {
            return;
        }
        String value = map.get(key) + "";
        if (StringUtils.isNotBlank(value)) {
            map.put(key, decrypt(value));
        }
    }

    /**
     * 把map中指定的"id"对应的value解密 Vic.xu
     *
     * @param map
     * @return 返回自己 方便链式调用
     */
    public static <T extends Map<String, ? super String>> void decryptKey(T map) {
        decryptKey(map, "id");
    }

    /**
     * 把字符串中符合正则表达式的字串 处理后  替换回去
     *
     * @param srcStr   原字符串
     * @param regexStr 正则 必须包含一个组(即小括号)
     * @param handler  提取的字符串的处理方式
     * @return String
     */
    public static String replacementAllHanderString(String srcStr, String regexStr, Function<String, String> handler) {
        if (StringUtils.isAnyBlank(srcStr, regexStr)) {
            return srcStr;
        }
        Pattern pattern = ENCRYPTED_REGEX.equals(regexStr)
                ? ENCRYPTED_PATTERN
                : Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(handler.apply(matcher.group(1))));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) {
        test();
    }

    private static void test() {
        String id = "123";
        String encrypt = encrypt(id);
        System.out.println(encrypt);
        System.out.println(decrypt(encrypt));
    }
}
