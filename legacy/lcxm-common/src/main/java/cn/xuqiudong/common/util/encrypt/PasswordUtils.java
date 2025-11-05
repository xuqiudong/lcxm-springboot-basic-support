package cn.xuqiudong.common.util.encrypt;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;


/**
 * @author Vic.xu
 */
public class PasswordUtils {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public static String entryptPassword(String plainPassword) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plain.getBytes(StandardCharsets.UTF_8), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param password      密文密码
     * @return 验证成功返回true
     */
    public static boolean validatePassword(String plainPassword, String password) {
        String plain = Encodes.unescapeHtml(plainPassword);
        byte[] salt = Encodes.decodeHex(password.substring(0, 16));
        byte[] hashPassword = Digests.sha1(plain.getBytes(StandardCharsets.UTF_8), salt, HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt)
                + Encodes.encodeHex(hashPassword));
    }

    public static void main(String[] args) {
        String a = entryptPassword("12122035xx");
        System.out.println(a);
        //2552044c031b54e4d24adcc2cb7ec1dbb88329b536285a6d9fdd41b2
        System.out.println(DigestUtils.md5Hex("123456"));
        System.out.println(DigestUtils.md5Hex("admin"));
    }

}
