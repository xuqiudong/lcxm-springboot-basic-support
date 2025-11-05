package cn.xuqiudong.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 * @author VIC
 */
@SuppressWarnings("PMD")
public class RegexUtil {
    private static final String ROOT_URL_REGEX = "((http|https)://.*?/)";
    private static final String CURRENT_URL_REGEX = "((http|https)://.*/)";
    private static final String CH_REGEX = "([\u4E00-\u9FA5])";

    /**
     *
     * 1 返回字符串中匹配正则的字符串并根据分隔符拼接
     *
     * @param srcStr    源字符串
     * @param regexStr 正则表达式
     * @param splitStr  分隔符
     * @param n 所在正则的位置
     * @return string
     *
     */
    public static String getString(String srcStr, String regexStr, String splitStr, int n) {
        if (StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(regexStr) || n < 1) {
            return "";
        }
        splitStr = splitStr == null ? "" : splitStr;
        // CASE_INSENSITIVE：当我们在匹配的时候要忽略字符大小写时 DOTALL:使用这个选项之后metacharacter .就可以包括一行的终止字符了，如果没有这个选项，
        // 一行的终止字符，并不会考虑在字符串之内的。 使用这个选项会降低效率

        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            sb.append(matcher.group(n).trim()).append(splitStr);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     *
     * 2 返回字符串中匹配正则的字符串拼接
     * @param srcStr
     * @param regexStr
     * @param n 所在正则的位置
     * @return
     *
     */
    public static String getString(String srcStr, String regexStr, int n) {
        return getString(srcStr, regexStr, null, n);
    }

    /**
     *
     * 3 返回第一条匹配的结果
     * @param srcStr
     * @param regexStr
     * @param n 所在正则的位置
     * @return string
     *
     */
    public static String getFirstString(String srcStr, String regexStr, int n) {
        if (StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(regexStr) || n < 1) {
            return "";
        }
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        while (matcher.find()) {
            return matcher.group(n).trim();
        }
        return "";
    }

    /**
     *
     * 4 回字符串中匹配正则的字符串 放入list集合
     * @param srcStr
     * @param regexStr
     * @param n  所在正则的位置
     * @return List<String>
     */
    public static List<String> getList(String srcStr, String regexStr, int n) {
        if (StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(regexStr) || n < 1) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        while (matcher.find()) {
            list.add(matcher.group(n));
        }
        return list;
    }

    /**
     *
     * 5 获取字符中匹配正则的全部数据String集合 放入list集合
     * @param srcStr
     * @param regexStr
     * @param arr  正则位置数组
     * @return List<String [ ]>
     */
    public static List<String[]> getList(String srcStr, String regexStr, int[] arr) {
        boolean check = StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(regexStr) || (arr == null || arr.length < 1);
        if (check) {
            return Collections.emptyList();
        }
        int len = arr.length;
        for (int j : arr) {
            if (j < 1) {
                return Collections.emptyList();
            }
        }
        List<String[]> list = new ArrayList<String[]>();

        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        while (matcher.find()) {
            String[] ss = new String[len];
            for (int i = 0; i < len; i++) {
                ss[i] = matcher.group(arr[i]).trim();
            }
            list.add(ss);
        }
        return list;
    }

    /**
     *
     * 6 获取字符中匹配正则的全部数据String 放入list集合
     *
     * @param srcStr
     * @param regexStr
     * @param arr 正则位置数组
     * @return List<String>
     */
    public static List<String> getStringList(String srcStr, String regexStr, int[] arr) {
        boolean check = isCheck(srcStr, regexStr, arr);
        if (check) {
            return Collections.emptyList();
        }
        int len = arr.length;
        for (int j : arr) {
            if (j < 1) {
                return Collections.emptyList();
            }
        }
        List<String> list = new ArrayList<String>();

        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        while (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                sb.append(matcher.group(arr[i]).trim());
            }
            list.add(sb.toString());
        }
        return list;
    }

    private static boolean isCheck(String srcStr, String regexStr, int[] arr) {
        return StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(regexStr) || (arr == null || arr.length < 1);
    }

    /**
     *
     * 7 获取字符中匹配正则的第一条数据 String[]
     *
     * @param srcStr
     * @param regexStr
     * @param arr
     *            正则位置数组
     * @return
     *
     */
    public static String[] getFirstyArray(String srcStr, String regexStr, int[] arr) {
        if (StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(regexStr) || (arr == null || arr.length < 1)) {
            return null;
        }
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            if (arr[i] < 1) {
                return null;
            }
        }
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        while (matcher.find()) {
            String[] ss = new String[len];
            for (int i = 0; i < len; i++) {
                ss[i] = matcher.group(arr[i]).trim();
            }
            return ss;
        }
        return null;
    }

    /**
     *
     * 8 .组装网址，网页的URL
     *
     * @param url
     * @param currentUrl
     * @return
     *
     */
    private static String getHttpUrl(String url, String currentUrl) {
        try {
            // 新增的replaceAll 转化有些地址接口中的转化地址，如： \/test\/1.html
            url = encodeUrlCh(url).replaceAll("\\\\/", "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (url.indexOf("http") == 0) {
            return url;
        }
        if (url.indexOf("/") == 0) {
            return getFirstString(currentUrl, ROOT_URL_REGEX, 1) + url.substring(1);
        }
        if (url.indexOf("\\/") == 0) {
            return getFirstString(currentUrl, ROOT_URL_REGEX, 1) + url.substring(2);
        }
        return getFirstString(currentUrl, CURRENT_URL_REGEX, 1) + url;
    }

    /**
     *
     * 9 获取和正则匹配的绝对链接地址
     *
     * @param srcStr
     * @param regexStr
     * @param currentUrl
     * @param n
     * @return
     */
    public static List<String> getArrayList(String srcStr, String regexStr, String currentUrl, int n) {
        List<String> reArrayList = new ArrayList<String>();
        if (srcStr == null || regexStr == null || n < 1 || srcStr.isEmpty()) {
            return reArrayList;
        }
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        while (matcher.find()) {
            reArrayList.add(getHttpUrl(matcher.group(n).trim(), currentUrl));
        }
        return reArrayList;
    }

    /**
     * 10 把字符串中符合正则表达式的字串 处理后 替换回去
     *
     * @param srcStr
     *            原字符串
     * @param regexStr
     *            正则 必须包含一个组(即小括号)
     * @param hander
     *            提取的字符串的处理方式
     * @return
     */
    public static String replacementAllhanderString(String srcStr, String regexStr, Function<String, String> hander) {
        if (StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(regexStr)) {
            return srcStr;
        }
        Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(srcStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, hander.apply(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 字符串是否以指定字符串结尾
     *
     * @param src
     * @param suffixs
     * @return
     *
     */
    public static boolean endWith(String src, String... suffixs) {
        if (src == null || suffixs == null || suffixs.length < 1) {
            return false;
        }
        for (String suffix : suffixs) {
            if (src.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转化URL中的中文字符
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeUrlCh(String url) throws UnsupportedEncodingException {
        while (true) {
            String s = getFirstString(url, CH_REGEX, 1);
            if (StringUtils.isEmpty(s)) {
                return url;
            }
            url = url.replace(s, URLEncoder.encode(s, "utf-8"));
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        String seq = " ===~ name ~---: 我是谁啊";
        // 正则表达式，两个捕获组
        String regex = " ===~ (.*?) ~---: (.*)";
        String firstString = getFirstString(seq, regex, 2);
        System.out.println("firstString:" +  firstString);

        // 创建 Pattern 和 Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(seq);

        // 如果匹配成功
        if (matcher.find()) {
            // 提取翻译后的文本（第二个捕获组）
            String translatedContent = matcher.group(2);
            // 提取字段名（第一个捕获组）
            String fieldName = matcher.group(1);

            // 输出结果
            System.out.println("Field Name: " + fieldName);
            System.out.println("Translated Content: " + translatedContent);
        } else {
            System.out.println("No match found.");
        }

        // 调试信息
        System.out.println("Input string: " + seq);
        System.out.println("Pattern used: " + regex);
        System.out.println("Matcher find result: " + matcher.find());



        String xml = "aaaa img src=&quot;../img/a.png&quot;&gt;bbbaaaa img src=&quot;../img/b.png&quot;&gt;bbb";
        List<String> paths = getList(xml, "src=&quot;(.*?)&quot;&gt;", 1);
        System.out.println(String.join("   ", paths));
        System.out.println("end...");
        String ch = "1我是 3中4华5小6当7家8";
        List<String> list2 = getList(ch, "([\u4e00-\u9fa5]+)", 1);

        System.out.println(list2);

        String tieba = "<div class=\"threadlist_title pull_left j_th_tit \">\r\n" + "    \r\n" + "    \r\n"
                + "    <a rel=\"noreferrer\" href=\"/p/7441036648\" title=\"【活动现场】强吧防暑小贴士活动\" target=\"_blank\" class=\"j_th_tit \">【活动现场】强吧防暑小贴士活动</a>\r\n"
                + "</div><div class=\"threadlist_author pull_right\">";

        String keyworlds2 = "【活动现场】强吧防暑小贴士活动";
        /*
         <a rel="noreferrer" href="/p/7435368062" title="【活动现场】强吧欢庆活动" target="_blank" class="j_th_tit ">【活动现场】强吧欢庆活动</a>
         */
        String reg2 =
                "<a rel=\"noreferrer\" href=\"(.*?)\" title=\"" + keyworlds2 + "\" target=\"_blank\" class=\"j_th_tit \">";
        System.out.println(RegexUtil.getFirstString(tieba, reg2, 1));
        boolean flag = 1 + 1 > 1;
        if (flag) {
            return;
        }


        String aa = "abnc1m2n3mn345";
        String reg = "(\\d)";
        System.out.println(getString(aa, reg, "|", 1));

        System.out.println(encodeUrlCh("www.合肥"));
        System.out.println("-------------------------");
        System.out.println(getFirstString("https://www.baidu.com/search/", ROOT_URL_REGEX, 1));
        System.out.println(getFirstString("http://www.baidu.com/search/", CURRENT_URL_REGEX, 1));

        String ss = "提出的加班申请[546546546]";
        System.out.println("***********  " + getFirstString(ss, "提出的加班申请[(.*?)]", 1));

        String s = "asd[546879213]";

        System.out.println("***********  " + getFirstString(s, "asd\\[(.*?)\\]", 1));

        String content =
                "<p>爱的阿萨德阿萨德<br/></p><p><img src=\"http://localhost:80//wroot-blog/console/ajax/visit/7\" title=\"mao.jpg\" alt=\"mao.jpg\"/><br/></p><p><br/></p><p></p><p>爱上大声地<br/></p><img src=\"http://localhost:80//wroot-blog/console/ajax/visit/8\"";
        List<String> list = getList(content, "/console/ajax/visit/(\\d+)\"", 1);
        System.out.println(list.size());
        for (String d : list) {
            System.out.println(d);
        }

    }
}
