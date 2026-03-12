/**
 *
 */
package cn.xuqiudong.basic.core.util;


import org.apache.commons.lang3.math.NumberUtils;

/**
 *  说明 :  数字的工具类
 *  @author Vic.xu
 * @since  2020年7月16日下午5:56:37
 */
@SuppressWarnings("PMD")
public class CommonNumberUtils extends NumberUtils {

    /**
     * 两个Integer是否相等
     * @param i one
     * @param j two
     * @return equals
     */
    public static boolean equals(Integer i, Integer j) {
        if (i == null && j == null) {
            return true;
        }
        if (i == null || j == null) {
            return false;
        }

        return i.equals(j);
    }


    private static final int MAX_DIGIT = 100000;
    private static final String[] MAPPING = {"", "十", "百", "千", "万"};
    private static final String[] NUMBER_CHINESE = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    /**
     * 正整数转汉字数字, 暂时支持到10万以下
     *
     * @param number number
     * @return number desc
     */
    public static String digit2Chinese(int number) {
        if (number >= MAX_DIGIT) {
            throw new IllegalArgumentException("数字太大了");
        }
        StringBuilder sb = new StringBuilder();
        char[] cs = String.valueOf(number).toCharArray();
        for (int i = 0; i < cs.length; i++) {
            sb.append(NUMBER_CHINESE[cs[i] - '0']).append(MAPPING[cs.length - i - 1]);

        }
        //去掉一十二这样的数字中的一
        String first = String.valueOf(sb.charAt(0));
        if (sb.length() == 3 && NUMBER_CHINESE[1].equals(first)) {
            sb.deleteCharAt(0);
        }

        return sb.toString().replaceAll("零[千百十]", "零").replaceAll("零+", "零").replaceAll("零$", "");
    }

}
