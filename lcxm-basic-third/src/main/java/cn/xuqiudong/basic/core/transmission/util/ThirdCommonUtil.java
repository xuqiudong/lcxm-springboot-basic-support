package cn.xuqiudong.basic.core.transmission.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

/**
 * 第三方通用的一些util
 * @author VIC.xu
 *
 */
public class ThirdCommonUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 转json, 保留空值
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * url 是否是http https开头
     * @return boolean
     */
    public static boolean withScheme(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        url = url.toLowerCase();

        return url.startsWith("http:") || url.startsWith("https:");
    }

}
