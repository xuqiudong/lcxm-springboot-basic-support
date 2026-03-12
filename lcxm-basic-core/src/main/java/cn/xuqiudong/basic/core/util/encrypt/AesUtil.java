package cn.xuqiudong.basic.core.util.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 *AES 加密, 每次产生随机因子，保证相同内容每次产生的密文不一致
 * <p>
 *     加密：  随机产生长度为 16 的byte数组作为随机因子， AES加密后，把随机因子和密文合并后转为Base64字符串
 *     解密： 把base64的密文转byte数组后拆分为随机因子和密文，然后进行ASE解密
 * </p>
 *
 * <a href="https://www.liaoxuefeng.com/wiki/1252599548343744/1304227762667553:>aes</a>
 * 在CBC模式下，需要一个随机生成的16字节IV参数，必须使用SecureRandom生成。因为多了一个IvParameterSpec实例，因此，初始化方法需要调用Cipher的一个重载方法并传入IvParameterSpec。
 * @author Vic.xu
 */
public final class AesUtil {

    /**
     * 随机因子长度
     */
    private static final int RANDOM_SEED_LENGTH = 16;

    /**
     * AES加密算法
     */
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    /**
     * key 加密算法
     */
    private static final String KEY_ALGORITHM = "AES";
    /**
     * 加密key的长度
     */
    private static final int KEY_SIZE = 16;

    /**
     *  加密：随机产生长度为 16 的byte数组作为随机因子， AES加密后，把随机因子和密文合并后转为Base64字符串
     * @param content 原文
     * @param key  密码
     * @return Base64格式的字符串
     * @throws Exception ex
     */
    public static String encrypt(String content, String key) throws Exception {
        if (content == null || key == null) {
            return null;
        }
        key = correctKey(key);
        // 16位长度的随机因子
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] iv = sr.generateSeed(RANDOM_SEED_LENGTH);
        IvParameterSpec ivps = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);
        byte[] input = content.getBytes(StandardCharsets.UTF_8);

        byte[] encryptedBytes = cipher.doFinal(input);
        byte[] join = join(iv, encryptedBytes);
        return Base64.encodeBase64String(join);
    }

    /**
     * 解密： 把base64的密文转byte数组后拆分为随机因子和密文，然后进行ASE解密
     * @param ciphertext base64 密文
     * @param key 密码
     * @return 原文
     * @throws Exception ex
     */
    public static String decrypt(String ciphertext, String key) throws Exception {
        if (ciphertext == null || key == null) {
            return ciphertext;
        }
        key = correctKey(key);
        byte[] input = Base64.decodeBase64(ciphertext.getBytes(StandardCharsets.UTF_8));
        // 把input分割成IV和密文:
        byte[] iv = new byte[RANDOM_SEED_LENGTH];
        byte[] data = new byte[input.length - RANDOM_SEED_LENGTH];

        System.arraycopy(input, 0, iv, 0, RANDOM_SEED_LENGTH);
        System.arraycopy(input, RANDOM_SEED_LENGTH, data, 0, data.length);

        //解密
        IvParameterSpec ivps = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
        byte[] bytes = cipher.doFinal(data);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 把密文和随机数串合在一起
     * @param bs1 随机数
     * @param bs2 密文
     * @return merger byte array
     */
    private static byte[] join(byte[] bs1, byte[] bs2) {
        byte[] r = new byte[bs1.length + bs2.length];
        System.arraycopy(bs1, 0, r, 0, bs1.length);
        System.arraycopy(bs2, 0, r, bs1.length, bs2.length);
        return r;
    }

    /**
     * 修改key的长度，保证key为16位置=
     * @param key 加密key
     * @return key 16位key
     */
    private static String correctKey(String key) {
        key = StringUtils.substring(key, 0, KEY_SIZE);
        key = StringUtils.rightPad(key, KEY_SIZE, '-');
        return key;
    }


    public static void main(String[] args) throws Exception {
        String paramStr = "pEBMr3fxZ/gY9oDyi1qMRsdasdas张三";
        String key = "(@E20-*2+!#$bc95";
        String random = "aa";
        System.out.println(random.getBytes(StandardCharsets.UTF_8).length);
        String encrypt = encrypt(paramStr, key);
        System.out.println(encrypt);
        String decrypt = decrypt(encrypt, key);
        System.out.println(decrypt);
        System.out.println(paramStr.equals(decrypt));
    }

}
