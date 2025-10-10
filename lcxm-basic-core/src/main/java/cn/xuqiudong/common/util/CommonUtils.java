package cn.xuqiudong.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用util
 *
 * @author Vic.xu
 */
@SuppressFBWarnings("UC_USELESS_OBJECT")
public class CommonUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String UNKNOWN = "unknown";

    private static final String WORDS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    /**
     * 通过response输出JSON
     *
     * @return
     */
    public static void writeJson(Object obj, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(toJson(obj));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 获得当前请求的URL
     * 不是用request.getServletPath() 是因为这个API和URL-MAPPING有变化关系
     */
    public static String getRequestUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        int i = requestUri.indexOf("?");
        if (i < 0) {
            i = requestUri.length();
        }
        return requestUri.substring(contextPath.length(), i);
    }


    /**
     * 对象转JSON
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * 根据文件名获得mimeType
     */
    public static String guessContentTypeFromName(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }

    /**
     * 把数字转为字母 1->A， 2->B
     */
    public static String numToWord(int num) {
        if (num > 26 || num < 1) {
            return "";
        }
        return "" + WORDS.charAt(num - 1);
    }

    /**
     * 字符串数组转Integer类型的list
     *
     * @param arr arr
     * @return List<Integer>
     */
    public static List<Integer> toIntList(String[] arr) {
        List<Integer> list = new ArrayList<Integer>();
        if (arr != null && arr.length > 0) {
            for (String obj : arr) {
                try {
                    Integer cur = Integer.parseInt(obj);
                    list.add(cur);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 删除int数组中的null
     */
    public static Integer[] deleteNullInArr(Integer[] arr) {
        if (arr == null || arr.length == 0) {
            return new Integer[]{};
        }
        List<Integer> list = new ArrayList<Integer>();
        for (Integer i : arr) {
            if (i != null && i != 0) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[]{list.size()});
    }

    /**
     * 把字符串首字母大写
     *
     * @return
     */
    public static String first(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return Character.toString(str.charAt(0)).toUpperCase() + str.substring(1, str.length());
    }

    /**
     * 删除字符串前后的分隔符或者子字符串
     * 2016年12月12日 by VIC
     *
     * @return
     */
    public static String deleteAroundSep(String str, String separator) {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(separator)) {
            return null;
        }
        int sepLen = separator.length();
        if (str.startsWith(separator)) {
            str = str.substring(sepLen, str.length());
        }
        if (str.endsWith(separator)) {
            str = str.substring(0, str.length() - sepLen);
        }
        return str;
    }

    /**
     * 把树形结构list数据转化为树
     *
     * @param list
     * @param idField       id 属性的名称
     * @param pidField      pid 属性的名称
     * @param childrenField children属性的名称    必须是List集合
     * @return
     */

    public static <T> List<T> buildTree(List<T> list, String idField, String pidField, String childrenField) {
        Map<Object, T> map = new LinkedHashMap<>();
        List<T> result = new ArrayList<>();
        try {
            for (T t : list) {
                Class<? extends Object> clazz = t.getClass();
                PropertyDescriptor idPd = new PropertyDescriptor(idField, clazz);
                Method getIdMethod = idPd.getReadMethod();//获得get方法
                Object id = getIdMethod.invoke(t);
                map.put(id, t);
            }


            for (Map.Entry<Object, T> entry : map.entrySet()) {
                T cur = entry.getValue();
                Class<? extends Object> clazz = cur.getClass();
                PropertyDescriptor pidPd = new PropertyDescriptor(pidField, clazz);
                Method getPidMethod = pidPd.getReadMethod();
                Object pid = getPidMethod.invoke(cur);
                T parent = map.get(pid);
                if (parent != null) {
                    Class<? extends Object> pclazz = cur.getClass();
                    PropertyDescriptor childrenPd = new PropertyDescriptor(childrenField, pclazz);
                    Method getChildrenMethod = childrenPd.getReadMethod();
                    @SuppressWarnings("unchecked")
                    List<T> children = (List<T>) getChildrenMethod.invoke(parent);
                    if (children == null) {
                        children = new ArrayList<>();
                    }
                    children.add(cur);
                } else {
                    result.add(cur);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * 把List中的数组用分隔符join成字符串
     *
     * @param list
     * @param segmentation
     * @return
     */
    public static String joinList(List<Integer> list, String segmentation) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(String::valueOf).collect(Collectors.joining(segmentation));
    }


    /**
     * bead --> map
     */
    public static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!"class".equals(key)) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            logger.error("transBean2Map Error " + e);
        }
        return map;
    }


    /**
     * 从request中获得IP
     */
    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    private static final String[] BOOTSTRAP_CLASSES = {"info", "warning", "primary", "success", "default", "danger"};

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 随机获取bootstrap的样式
     */
    public static String randomBootstrapClass() {
        int index = RANDOM.nextInt(5);
        return randomBootstrapClass(index);
    }

    public static String randomBootstrapClass(int index) {
        index = index % 6;
        return BOOTSTRAP_CLASSES[index];
    }

    /**
     * 把list集合中的数据转化为in条件 ["11","222"]->"'11','22'"
     * <p>
     * 描述:
     *
     * @author Vic.xu
     * @since 2020年3月3日 下午6:03:29
     */
    public static String list2DatabaseIn(List<?> ids) {

        if (CollectionUtils.isEmpty(ids)) {
            return "''";
        }
        // 去重
        Set<?> set = new HashSet<>(ids);
        return String.format("'%s'", StringUtils.join(set, "','"));
    }

    /**
     * 根据Function 过滤list后返回, 会进行空判断
     *
     * @param <T>
     * @param <R>
     * @param list
     * @param funtion
     * @return
     */
    public static <T, R> List<R> listFilter(List<T> list, Function<T, R> funtion) {
        List<T> notNoneList = Optional.ofNullable(list).orElse(new ArrayList<T>());
        return notNoneList.stream().map(funtion).collect(Collectors.toList());
    }

    /**
     * 随机生成一个uuid
     */
    public static String randomUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
