package cn.xuqiudong.basic.framework.runtime;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SignUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * 功能描述
 * 基于hutool 的RSA 签名和验签
 *
 * @author Vic.xu
 * @since 2026-05-09
 */
public class RsaSignatureUtils {

    /**
     * 生成RSA 密钥对
     */
    public static RsaKeyPair createKeys() {
        RSA rsa = new RSA();
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        return new RsaKeyPair(publicKeyBase64, privateKeyBase64);
    }

    /**
     * 私钥签名
     */
    public static String privateSign(String data, String privateKey) {
        Sign sign = SignUtil.sign(
                SignAlgorithm.SHA256withRSA,
                privateKey,
                null
        );
        byte[] signBytes = sign.sign(data);
        return Base64.encode(signBytes);
    }

    /**
     * 公钥验签
     *
     * @param signContent sign
     * @param content     签名前的内容
     * @param publicKey   公钥
     */
    public static boolean publicVerify(String signContent, String content, String publicKey) {
        Sign sign = SignUtil.sign(
                SignAlgorithm.SHA256withRSA,
                null,
                publicKey
        );
        return sign.verify(
                content.getBytes(StandardCharsets.UTF_8),
                Base64.decode(signContent)
        );
    }



    /**
     * RSA 密钥对
     */
    @AllArgsConstructor
    @Getter
    public static class RsaKeyPair {
        /**
         * 公钥
         */
        private String publicKey;
        /**
         * 私钥
         */
        private String privateKey;

        @Override
        public String toString() {
            return "RsaKeyPair{" + '\n' +
                    "publicKey=" + publicKey + '\n' +
                    "privateKey=" + privateKey + '\n' +
                    '}';
        }
    }
}
