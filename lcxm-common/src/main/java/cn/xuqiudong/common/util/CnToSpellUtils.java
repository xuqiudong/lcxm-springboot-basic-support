package cn.xuqiudong.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;


/**
 * 汉语转拼音工具类
 *
 * @author VIC
 */
public class CnToSpellUtils {

    /**
     * 返回字符串的全拼,是汉字转化为全拼,其它字符不进行转换
     *
     * @param cnStr String 字符串
     * @return String 转换成全拼后的字符串
     */
    public static String getFullSpell(String cnStr) {
        if (StringUtils.isBlank(cnStr)) {
            return cnStr;
        }
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] input = cnStr.trim().toCharArray();
        StringBuilder output = new StringBuilder();
        try {
            for (char c : input) {
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    output.append(temp[0]);
                } else if (Character.toString(c).matches("[^\\x00-\\x80]") || c == ' ') {
//                    output.append("");

                } else {
                    output.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * 获得汉语字符串的每个汉字的首字母小写  其它字符不进行转换
     *
     * @param cnStr 中文
     */
    public static String getFirstSpell(String cnStr) {
        if (StringUtils.isBlank(cnStr)) {
            return cnStr;
        }
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] input = cnStr.trim().toCharArray();
        StringBuilder output = new StringBuilder();
        try {
            for (char c : input) {
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    String full = temp[0];
                    if (StringUtils.isNotBlank(full)) {
                        output.append(full.charAt(0));
                    }
                } else if (Character.toString(c).matches("^\\x00-\\x80") || c == ' ') {

                } else {
                    output.append(c);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output.toString().toLowerCase();

    }
}

