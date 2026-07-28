package cn.xuqiudong.basic.framework.runtime;

import cn.hutool.core.codec.Base62;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-07-27 17:17
 */
public class LcRuntimeHelper {

    private static volatile LcPayload payload;


    public static String de(String t) {
        return Base62.decodeStr(t);
    }


    public static LcPayload instance() {
        if (payload == null) {
            synchronized (LcRuntimeHelper.class) {
                if (payload == null) {
                    try (InputStream in = LcRuntimeHelper.class.getClassLoader().getResourceAsStream(de(TextBundle.get("l")));
                         BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String str = reader.readLine();
                        String json = Base62.decodeStr(str);
                        LcPayload payload = JSONUtil.toBean(json, LcPayload.class);
                        String data = SignaturePayloadBuilder.buildIssuerPayload(payload);
                        boolean b = RsaSignatureUtils.publicVerify(payload.getSign(), data, de(TextBundle.get("p1")));
                        if (!b) {
                            p(de(TextBundle.getTip(1)));
                            q(0);
                            return null;
                        }
                        LcRuntimeHelper.payload = payload;
                    } catch (Exception e) {
                        e.printStackTrace();
                        p(de(TextBundle.getTip(1)));
                        q(0);

                    }
                }
            }
        }
        return payload;
    }


    public static void p(String s) {
        try {
            Field f = System.class.getDeclaredField(de("UeFY"));
            f.setAccessible(true);
            Object o = f.get(null);
            o.getClass().getMethod(de("2KxdVVZbhu"), String.class)
                    .invoke(o, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tip(int i) {
        p(de(TextBundle.getTip(i)));
    }

    public static void q(int s) {
        try {
            Method m = System.class.getMethod(de("1rD3RM"));
            m.invoke(null, s);
        } catch (Exception e) {

        }
    }


    public static void main(String[] args) {
        tip(1);
        tip(2);


    }


}
