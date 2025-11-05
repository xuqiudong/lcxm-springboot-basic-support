package cn.xuqiudong.common.util.encrypt;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 对base62做增强加密处理（多加一个salt异或处理）
 * 
 * @author Vic.xu
 * @since 2021/07/21
 */
public class Base62Enhance {

    private Base62 base62;

    private byte[] xorArray;

    private Base62Enhance() {}

    /**
     * 构造Base62Enhance
     * 
     * @param salt
     *            盐
     * @return
     */
    public static Base62Enhance createInstance(String salt) {
        Base62Enhance instance = new Base62Enhance();
        instance.base62 = Base62.createInstance();
        instance.xorArray = salt.getBytes(StandardCharsets.UTF_8);
        return instance;
    }

    /**
     * 加密
     * 
     * @param src
     *            需要加密的字符串
     * @return
     */
    public String encode(String src) {
        if (StringUtils.isEmpty(src)) {
            return src;
        }
        byte[] bytes = src.getBytes(StandardCharsets.UTF_8);
        xor(bytes);
        byte[] base = base62.encode(bytes);
        String result = new String(base, StandardCharsets.UTF_8);
        return result;
    }

    /**
     * 解密
     * 
     * @param dir
     *            需要解密的字符串
     * @return
     */
    public String decode(String dir) {
        byte[] dirBytes = base62.decode(dir.getBytes(StandardCharsets.UTF_8));
        xor(dirBytes);
        String result = new String(dirBytes, StandardCharsets.UTF_8);
        return result;
    }

    /**
     * 对source的每一位和salt进行异或操作，
     * 
     * @param source
     *            源
     */
    private void xor(byte[] source) {
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < xorArray.length; j++) {
                source[i] = (byte)(source[i] ^ xorArray[j]);
            }
        }

    }

    /**
     * test
     * 
     */
    public static void main(String[] args) {
        String salt = "hahaha";
        Base62Enhance base62Enhance = Base62Enhance.createInstance(salt);
        String string = "我是Vic.xu";
        String encode = base62Enhance.encode(string);
        String decode = base62Enhance.decode(encode);
        System.out.println("原文：" + string);
        System.out.println("密文：" + encode);
        System.out.println("解密：" + decode);
    }

}
