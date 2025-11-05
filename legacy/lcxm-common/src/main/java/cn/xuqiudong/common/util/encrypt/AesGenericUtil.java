package cn.xuqiudong.common.util.encrypt;

import cn.xuqiudong.common.util.JsonUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 描述:通用的aes加密(可用于第三方)，不再对密文做额外的处理，
 * 且对加密key和
 * @author Vic.xu
 * @since 2023-02-09 17:19
 */
@SuppressWarnings("PMD")
public class AesGenericUtil {

    private static final Logger logger = LoggerFactory.getLogger(AesGenericUtil.class);

    /**
     * 安全随机算法
     */
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * AES加密算法
     */
    private static final String AES_CIPHER_ALGORITHM = "AES";
    /**
     * 加密key的长度
     */
    private static final int KEY_SIZE = 16;

    /**
     * 对应KEY_CIPHER_CACHE(key和  Cipher) 的最大缓存数量
     */
    private static final int MAX_KEY_CIPHER_CACHE_SIZE = 32;

    /**
     * 缓存key和 Cipher， 因怀疑此处造成执行缓慢 TODO 需确认
     * @see  <a href="https://stackoverflow.com/questions/39798728/cipher-encryption-is-noticeably-slow">Cipher Encryption is noticeably slow</a>
     */
    private static final LinkedHashMap<String, CachedCipherPair> KEY_CIPHER_CACHE = new LinkedHashMap<>();

    /**
     * AES 加密， 含压缩
     * @param param 需加密的参数
     * @param key 加密的key
     * @return ciphertext
     */
    public static String encrypt(String param, String key) {
        try {
            return new String(encodeBase64(zip(StringUtils.getBytesUtf8(toEncrypt(encode(param), key)))), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * java.net.URLEncoder.encode
     */
    private static String encode(String str) {
        String str2 = "";
        try {
            str2 = java.net.URLEncoder.encode(str, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return str2;
    }

    private static byte[] zip(byte[] bContent) {

        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(bContent.length);
            zip.putNextEntry(entry);
            zip.write(bContent);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    private static byte[] encodeBase64(byte[] source) {
        return Base64.encodeBase64(source);
    }

    private static String toEncrypt(String content, String password) {
        try {

            // 创建密码器
            Cipher cipher = initCipherAndCache(password, Cipher.ENCRYPT_MODE);
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            byte[] result = cipher.doFinal(byteContent);
            // 加密
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private static byte[] unZip(byte[] bContent) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bContent);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    private static byte[] decodeBase64(String source) {
        return Base64.decodeBase64(source);
    }

    /**
     * AES解密，含解压缩
     * @param param 解密内容
     * @param key 秘钥
     * @return 解密结果
     */
    public static String decrypt(String param, String key) {
        try {
            return decode(toDecrypt(new String(unZip(decodeBase64(param)), StandardCharsets.UTF_8), key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decode(String str) {
        String str2 = "";
        try {
            str2 = URLDecoder.decode(str, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return str2;
    }

    private static String toDecrypt(String content, String key) {
        try {
            // 创建密码器
            Cipher cipher = initCipherAndCache(key, Cipher.DECRYPT_MODE);
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * init cipher and cache it if necessary
     * @param key key
     * @return Cipher
     * @throws Exception ex
     */
    private static Cipher initCipherAndCache(String key, Integer cryptoModel) throws Exception {
        key = correctKey(key);
        Cipher cipher = KEY_CIPHER_CACHE.getOrDefault(key, new CachedCipherPair()).getCipher(cryptoModel);
        if (cipher != null) {
            return cipher;
        }

        KeyGenerator kgen = KeyGenerator.getInstance(AES_CIPHER_ALGORITHM);
        SecureRandom srandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        srandom.setSeed(key.getBytes(StandardCharsets.UTF_8));
        kgen.init(128, srandom);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, AES_CIPHER_ALGORITHM);
        // 创建密码器
        Cipher deCipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        Cipher enCipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        // 初始化
        deCipher.init(Cipher.DECRYPT_MODE, keySpec);
        enCipher.init(Cipher.ENCRYPT_MODE, keySpec);
        CachedCipherPair cachedCipherPair = new CachedCipherPair(deCipher, enCipher);
        //放入缓存
        KEY_CIPHER_CACHE.put(key, cachedCipherPair);
        //超出最大缓存则删除最先放入的Cipher
        if (KEY_CIPHER_CACHE.size() > MAX_KEY_CIPHER_CACHE_SIZE) {
            int more = KEY_CIPHER_CACHE.size() - MAX_KEY_CIPHER_CACHE_SIZE;
            Iterator<String> iterator = KEY_CIPHER_CACHE.keySet().iterator();
            while (iterator.hasNext() && more-- > 0) {
                String next = iterator.next();
                logger.info("remove cipher cache for key = {}", next);
                iterator.remove();
            }
        }
        return cachedCipherPair.getCipher(cryptoModel);

    }


    /**
     * 修改key的长度，保证key为16位置=
     * @param key
     * @return key
     */
    private static String correctKey(String key) {
        key = org.apache.commons.lang3.StringUtils.substring(key, 0, KEY_SIZE);
        key = org.apache.commons.lang3.StringUtils.rightPad(key, KEY_SIZE, '-');
        return key;
    }

    /**
     *  缓存的Cipher对
     */
    static class CachedCipherPair {
        Map<Integer, Cipher> pairs = new HashMap<Integer, Cipher>();

        public CachedCipherPair() {
        }

        /**
         * @param decrypt 解密 Cipher
         * @param encrypt 加密 encrypt
         */
        public CachedCipherPair(Cipher decrypt, Cipher encrypt) {
            pairs.put(Cipher.ENCRYPT_MODE, encrypt);
            pairs.put(Cipher.DECRYPT_MODE, decrypt);
        }

        public Cipher getCipher(Integer cryptoModel) {
            return pairs.get(cryptoModel);
        }
    }


    public static void main(String[] args) throws Exception {
        String key = "!@sHgfPt5#&dfKH910";
        System.out.println("origin key length = " + key.length());
        key = correctKey(key);
        System.out.println(key);
        System.out.println("correctKey length = " + key.length());
        Map<String, Object> map = new HashMap<>();
        map.put("id", 123456);
        map.put("name", "zhangsan");
        map.put("time", new Date());

        String param = JsonUtil.toJson(map);
        System.out.println("the original parameters is :    " + param);
        String ciphertext = encrypt(param, key);
        System.out.println("the ciphertext is : " + ciphertext);
        String plaintext = decrypt(ciphertext, key);
        System.out.println("the plaintext is : " + plaintext);
        System.out.println("Original  param is equals  plaintext :" + param.equals(plaintext));

    }


}
