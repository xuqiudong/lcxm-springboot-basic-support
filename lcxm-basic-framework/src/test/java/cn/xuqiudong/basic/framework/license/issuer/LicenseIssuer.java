package cn.xuqiudong.basic.framework.license.issuer;

import cn.hutool.core.codec.Base62;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSONUtil;
import cn.xuqiudong.basic.framework.runtime.LcPayload;
import cn.xuqiudong.basic.framework.runtime.RsaSignatureUtils;
import cn.xuqiudong.basic.framework.runtime.SignaturePayloadBuilder;
import lombok.Data;

/**
 * Description:
 *  生成  license （license 字符串和对应的公钥）
 * @author Vic.xu
 * @since 2026-07-28 9:03
 */
public class LicenseIssuer {


    public static void main(String[] args) {
        LicenseIssuer issue = LicenseIssuer.create("Vic.xu", 7, LicenseSecretPairs.P1)
                .issue();
        issue.print();
        issue.validate();

    }


    private License license;

    /**
     * base62 license
     */
    String licenseString;
    /**
     * base64->base62 公钥
     */
    String publicKeyString;

    RSA rsa;


    public static LicenseIssuer create(String subject, long expireDays) {
        LicenseIssuer licenseIssuer = new LicenseIssuer();
        licenseIssuer.rsa = new RSA();
        return licenseIssuer.build(subject, expireDays);
    }

    public static LicenseIssuer create(String subject, long expireDays, LicenseSecretPairs.RsaKeyPair pair) {
        LicenseIssuer licenseIssuer = new LicenseIssuer();
        licenseIssuer.rsa = new RSA(pair.getPrivateKey(), pair.getPublicKey());
        return licenseIssuer.build(subject, expireDays);
    }


    /**
     * 签发
     * <p>
     * 1. 构建payload 的 字符串
     * 2. 私钥签名
     * 3. payload 转json
     * 4. json 转base62 = license
     * </p>
     *
     * @return
     */
    public LicenseIssuer issue() {
        LicensePayload payload = license.getPayload();
        // 待签名字符串
        String s = SignaturePayloadBuilder.buildIssuerPayload(license.getPayload());
        // 私钥签名
        String sign = RsaSignatureUtils.privateSign(s, rsa.getPrivateKeyBase64());
        payload.setSign(sign);
        String json = JSONUtil.toJsonStr(payload);
        licenseString = Base62.encode(json);
        publicKeyString = Base62.encode(rsa.getPublicKeyBase64());
        return this;
    }

    public void validate(){
        // 原始待签名字符串
        String origin = SignaturePayloadBuilder.buildIssuerPayload(license.getPayload());
        LcPayload payload = JSONUtil.toBean(Base62.decodeStr(licenseString), LcPayload.class);

        boolean b = RsaSignatureUtils.publicVerify(payload.getSign(), origin, rsa.getPublicKeyBase64());
        if (!b) {
            throw new RuntimeException("license 验证失败");
        }
        System.out.println("license 验证成功");
    }

    public String getLicenseString() {
        return licenseString;
    }

    public String getPublicKeyString() {
        return publicKeyString;
    }

    public void print() {
        System.out.println("license: " + licenseString);
        System.out.println("publicKey: " + publicKeyString);
    }


    public LicenseIssuer build(String subject, long expireDays) {
        License license = new License();
        LicensePayload licensePayload = new LicensePayload();
        licensePayload.setExpireAt(System.currentTimeMillis() + expireDays * 24 * 60 * 60 * 1000);
        licensePayload.setIssueAt(System.currentTimeMillis());
        licensePayload.setSubject(subject);
        licensePayload.setVersion("1.0.0");
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        license.setPayload(licensePayload);
        license.setPrivateKey(privateKeyBase64);
        license.setPublicKey(publicKeyBase64);
        this.license = license;
        return this;
    }


    @Data
    public static class License {

        LicensePayload payload;

        String publicKey;

        String privateKey;


    }
}
