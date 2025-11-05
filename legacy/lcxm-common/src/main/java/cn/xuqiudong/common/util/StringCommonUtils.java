package cn.xuqiudong.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 */

/**
 *  说明 :  字符串的一些通用操作
 *  @author Vic.xu
 * @since  2020年7月7日上午8:28:28
 */
public class StringCommonUtils extends StringUtils {

    private StringCommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 查询长度 供oracle使用的判断
     * 其实使用 str.getBytes("UTF-8").length 更方便一些，如果确认了字符编码的话
     * @param str
     * @return
     */
    public static int length4Oracle(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        int length = 0;
        char[] cs = str.toCharArray();
        for (char c : cs) {
            //中文
            if ((c >= 0x0391 && c <= 0xFFE5)) {
                length += 3;
                //英文
            } else if (c >= 0x0000 && c <= 0x00FF) {
                length += 1;
                // 应该很少有else吧	 +3 以预防长度不够
            } else {
                length += 3;
            }
        }
        return length;
    }

    /**
     * 字符串填充:把字符source用placeholder填充到length长度
     * @param source 源字符
     * @param placeholder 用以填充的字符
     * @param length 总长度
     * @return
     */
    public static String fill(String source, String placeholder, int length) {
        if (isBlank(source)) {
            source = "";
        }
        int len = source.length();
        if (len <= length) {
            return source;
        }

        return null;
    }

    /**
     * 拼接对象的属性
     * @param <T>
     * @param list 对象集合
     * @param fieldSeparator 属性之间的分隔符
     * @param separator 对象之间的分隔符
     * @param getMethods  获取对象的属性方法 数组
     * @return
     */
    @SafeVarargs
    public static <T> String joinObjectsFields(Collection<T> list, String fieldSeparator, String separator, Function<T, ?>... getMethods) {
        if (CollectionUtils.isEmpty(list) || getMethods == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        list.forEach(t -> {
            Stream.of(getMethods).forEach(m -> {
                result.append(m.apply(t)).append(fieldSeparator);
            });
            //删除多余属性分隔符
            result.setLength(result.length() - fieldSeparator.length());
            result.append(separator);
        });

        if (isEmpty(separator)) {
            return result.toString();
        }

        // 删除多余的分隔符
        if (result.length() > separator.length()) {
            result.setLength(result.length() - separator.length());
        }
        return result.toString();
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = ";我？22*簳";
        System.out.println(str.getBytes("UTF-8").length);
        int len = length4Oracle(str);
        System.out.println(len);
        String s = "aaa";
        s = StringUtils.leftPad(s, 2, "0");
        System.out.println(s);

        StringBuilder sb = new StringBuilder();
        sb.append("aa").append(":").append("bbb").append(",");
        sb.setLength(sb.length() - 1);
        System.out.println(sb);

        String join = joinObjectsFields(Test.random(), ":", ",", Test::getId, Test::getName);
        System.out.println(join);


    }
}

class Test {
    private int id;

    private String name;

    public static List<Test> random() {
        List<Test> list = new ArrayList<Test>();
        list.add(new Test(1, "zhangsan"));
        list.add(new Test(2, "lisi"));
        list.add(new Test(3, "王五"));
        list.add(new Test(4, "陆路"));
        return list;
    }

    /**
     *
     */
    public Test() {
        super();
    }

    /**
     * @param id
     * @param name
     */
    public Test(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
