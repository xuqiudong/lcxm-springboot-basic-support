package cn.xuqiudong.common.util;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * application.properties 配置文件读取 <br />
 * 可根据application.properties配置的other.config.files 读取到其他文件的配置
 *
 * @author Vic.xu
 * @since 2021/08/25
 */
public class ApplicationPropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationPropertiesUtil.class);
    /**
     * 主配置文件名
     */
    private static final String FILE_NAME = "application.properties";

    /**
     * application.properties 文件中可指定需要加载的其他配置文件的key 是个list<br />
     * 形式如下：<br />
     * other.config.files=xx.properties <br />
     * other.config.files=yy.properties
     */
    private static final String OTHER_CONFIG_FILES_KEY = "other.config.files";

    private static final Configurations CONFIGS = new Configurations();

    public static final CompositeConfiguration reader = new CompositeConfiguration();

    static {
        try {
            PropertiesConfiguration properties = CONFIGS.properties(FILE_NAME);
            reader.addConfiguration(properties, true);
            // 装载其他配置文件
            List<Object> list = reader.getList(OTHER_CONFIG_FILES_KEY, new ArrayList<Object>());

            for (Object file : list) {
                logger.info("装载其他配置文件:{}", file);
                reader.addConfiguration(CONFIGS.properties((String) file), true);
            }
        } catch (ConfigurationException e) {
            logger.error("读取第三方配置文件错误 {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 读String
     */
    public static String getString(String key) {
        return reader.getString(key);
    }

    /**
     * 读String
     */
    public static String getString(String key, String defaultValue) {
        String value = reader.getString(key);
        return value == null ? defaultValue : value;
    }


    /**
     * 读int
     */
    public static int getInt(String key) {
        return reader.getInt(key);
    }

    /**
     * 读boolean
     */
    public static boolean getBoolean(String key) {
        return reader.getBoolean(key);
    }

    /**
     * 读List
     */
    public static List<?> getList(String key) {
        return reader.getList(key);
    }

    /**
     * 读数组
     */
    public static String[] getStringArray(String key) {
        return reader.getStringArray(key);
    }

    /**
     * long
     */
    public static long getLong(String key) {
        return reader.getLong(key);
    }

    public static void main(String[] args) {
        String string = getString("aaa");
        System.out.println(string);
    }
}
