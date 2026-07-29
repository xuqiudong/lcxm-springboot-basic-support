package cn.xuqiudong.basic.third.security;

import java.nio.charset.StandardCharsets;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SignUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RSA signature helper based on Hutool.
 *
 * @author Vic.xu
 */
public final class RsaSignatureUtils {

    private RsaSignatureUtils() {
    }

    /**
     * Creates a base64 encoded RSA key pair.
     */
    public static RsaKeyPair createKeys() {
        RSA rsa = new RSA();
        return new RsaKeyPair(rsa.getPublicKeyBase64(), rsa.getPrivateKeyBase64());
    }

    /**
     * Signs plain text with private key, using SHA256withRSA.
     */
    public static String privateSign(String data, String privateKey) {
        Sign sign = SignUtil.sign(SignAlgorithm.SHA256withRSA, privateKey, null);
        return Base64.encode(sign.sign(data.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Verifies SHA256withRSA signature with public key.
     */
    public static boolean publicVerify(String signContent, String content, String publicKey) {
        Sign sign = SignUtil.sign(SignAlgorithm.SHA256withRSA, null, publicKey);
        return sign.verify(content.getBytes(StandardCharsets.UTF_8), Base64.decode(signContent));
    }

    /**
     * Base64 encoded RSA key pair.
     */
    @Getter
    @AllArgsConstructor
    public static class RsaKeyPair {

        private String publicKey;

        private String privateKey;
    }
}
