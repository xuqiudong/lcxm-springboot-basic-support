package cn.xuqiudong.basic.framework.license.issuer;

import cn.hutool.core.codec.Base62;
import cn.xuqiudong.basic.framework.runtime.TextBundle;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @see TextBundle
 * @author Vic.xu
 * @since 2026-07-27 17:40
 */
public class LicenseTextBound {

    private static final Map<String, String > BUNDLE = new HashMap<>();

    static {
        // 公钥 p1
        BUNDLE.put("p1", "jY9T3E8jiLg0d0WCf642iMOKzi4sro5WshBGRQYuWiXTjavD0ivkWNWPsKxV3QnGjmMz7wGiQ0YzJEKuQVR0SF0D1ufluuRcoKIjSZCLO2cxedqhm3QcRo37nPbBRblSaRtU7XOyxlWSCHah7BcHzyMRvgGY8wmT00Vypl3nFOO36xwv9HaOR649jkseBrC8wo6PmOjhmoC9WvVu8SM1ZJ1uPIxsGOUSVJvEO8wUhqHjfTq8pbe2jfh7JZTD3CqbLz7Il8Ltq9o2pNjVkghUfEQx7BPLfdc0ME");
        // license
        BUNDLE.put("l", "2Fl6jZ7bMb");
        // 许可到期, 暂且停止服务
        BUNDLE.put("tip_1", "tAvGrGHEOrmAMgJwAbQ83W8J4xoX32y6p7FZvxCaSoL");
        //请求过期, 请连续管理员
        BUNDLE.put("tip_2", "tAyolEkZiWATkYhlTBD55xjQiDGs3spBRuNzNeNy4qW");
        //

    }

    public static void main(String[] args) {
        String encoded = Base62.encode("许可到期, 暂且停止服务");
        System.out.println(encoded);
        String encoded2 = Base62.encode("license");
        System.out.println(encoded2);

        System.out.println(Base62.encode("exit"));

        System.out.println(Base62.encode(LicenseSecretPairs.P1.getPublicKey()));
    }
}
