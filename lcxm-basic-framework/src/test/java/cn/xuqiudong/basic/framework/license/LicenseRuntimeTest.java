package cn.xuqiudong.basic.framework.license;

import cn.hutool.core.codec.Base62;
import cn.hutool.json.JSONUtil;
import cn.xuqiudong.basic.framework.license.issuer.LicenseIssuer;
import cn.xuqiudong.basic.framework.license.issuer.LicenseSecretPairs;
import cn.xuqiudong.basic.framework.runtime.LcPayload;
import cn.xuqiudong.basic.framework.runtime.LcRuntimeHelper;
import cn.xuqiudong.basic.framework.runtime.RsaSignatureUtils;
import cn.xuqiudong.basic.framework.runtime.SignaturePayloadBuilder;
import cn.xuqiudong.basic.framework.runtime.TextBundle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

class LicenseRuntimeTest {

    @Test
    @DisplayName("instance loads classpath license")
    void instanceLoadsClasspathLicense() {
        LcPayload payload = LcRuntimeHelper.instance();

        Assertions.assertNotNull(payload);
        Assertions.assertEquals("hhny", payload.getSubject());
        Assertions.assertEquals("1.0.0", payload.getVersion());
        Assertions.assertFalse(payload.expired());
        String jsonStr = JSONUtil.toJsonStr(payload);
        System.out.println(jsonStr);
        Instant instant = Instant.ofEpochMilli(payload.getIssueAt());
        System.out.println(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
        Instant expireAt = Instant.ofEpochMilli(payload.getExpireAt());
        System.out.println(expireAt.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Test
    @DisplayName("发行端生成的 license 可以被运行端公钥验签")
    void issuedLicenseCanBeVerifiedByRuntimePublicKey() {
        LicenseIssuer issuer = LicenseIssuer.create("Vic.xu", 7, LicenseSecretPairs.P1).issue();

        LcPayload payload = decodeLicense(issuer.getLicenseString());
        String signData = SignaturePayloadBuilder.buildIssuerPayload(payload);

        Assertions.assertTrue(
                RsaSignatureUtils.publicVerify(payload.getSign(), signData, LcRuntimeHelper.de(TextBundle.get("p1")))
        );
        Assertions.assertFalse(payload.expired());
    }

    @Test
    @DisplayName("license 内容被篡改后验签失败")
    void tamperedLicenseCanNotBeVerified() {
        LicenseIssuer issuer = LicenseIssuer.create("Vic.xu", 7, LicenseSecretPairs.P1).issue();
        LcPayload payload = decodeLicense(issuer.getLicenseString());

        payload.setSubject("attacker");
        String tamperedSignData = SignaturePayloadBuilder.buildIssuerPayload(payload);

        Assertions.assertFalse(
                RsaSignatureUtils.publicVerify(payload.getSign(), tamperedSignData, LcRuntimeHelper.de(TextBundle.get("p1")))
        );
    }

    @Test
    @DisplayName("过期 license 可以被识别")
    void expiredLicenseCanBeDetected() {
        LicenseIssuer issuer = LicenseIssuer.create("Vic.xu", -1, LicenseSecretPairs.P1).issue();

        LcPayload payload = decodeLicense(issuer.getLicenseString());
        String signData = SignaturePayloadBuilder.buildIssuerPayload(payload);

        Assertions.assertTrue(
                RsaSignatureUtils.publicVerify(payload.getSign(), signData, LcRuntimeHelper.de(TextBundle.get("p1")))
        );
        Assertions.assertTrue(payload.expired());
    }

    private static LcPayload decodeLicense(String licenseString) {
        return JSONUtil.toBean(Base62.decodeStr(licenseString), LcPayload.class);
    }
}
