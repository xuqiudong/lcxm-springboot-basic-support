package cn.xuqiudong.basic.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述: 通过ConfigurableEnvironment读取springboot 配置文件中的值
 *    比一般的Bean初始化时机更早，但是在EnvironmentPostProcessor之后
 * <p>
 *     通过SpringApplicationRunListener
 *    在spring上下文准备好之前，为SpringBootApplicationPropertiesUtil 工具类设置 ConfigurableEnvironment
 *    在resources/META-INFO/spring.factories中配置此监听器：
 *    org.springframework.boot.SpringApplicationRunListener=cn.xuqiudong.common.util.SpringBootApplicationPropertiesUtil
 * </p>
 * @author Vic.xu
 * @since 2024-04-18 10:33
 */
public class SpringBootApplicationPropertiesUtil implements SpringApplicationRunListener {

    private static ConfigurableEnvironment environment;

    private static final String DELIMITER = ",";

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootApplicationPropertiesUtil.class);

    /**
     * 初始化：should declare a public constructor that accepts a {@link SpringApplication}
     *  * instance and a {@code String[]} of arguments.
     * @see SpringApplicationRunListener
     */
    public SpringBootApplicationPropertiesUtil(SpringApplication application, String[] args) {
        LOGGER.info("SpringBootApplicationPropertiesUtil initialized  as  a SpringApplicationRunListener !!!");
    }

    /**
     * 在spring上下文准备好之前，为本工具类设置 ConfigurableEnvironment
     */
    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        setEnvironment(environment);
    }

    public static void setEnvironment(ConfigurableEnvironment environment) {
        SpringBootApplicationPropertiesUtil.environment = environment;
    }

    /**
     *  获取 String
     * @param key key
     */
    public static String getString(String key) {
        return environment.getProperty(key);
    }


    /**
     *  获取 String
     * @param key key
     * @param defaultValue the default value to return if no value is found
     */
    public static String getString(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    /**
     *  获取 Boolean
     * @param key key
     */
    public static Boolean getBoolean(String key) {
        return environment.getProperty(key, Boolean.class);
    }

    /**
     *  获取 Boolean
     * @param key key
     * @param defaultValue the default value to return if no value is found
     */
    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return environment.getProperty(key, Boolean.class, defaultValue);
    }

    /**
     *  获取 Integer
     * @param key key
     */
    public static Integer getInteger(String key) {
        return environment.getProperty(key, Integer.class);
    }

    /**
     *  获取 Integer
     * @param key key
     * @param defaultValue the default value to return if no value is found
     */
    public static Integer getInteger(String key, Integer defaultValue) {
        return environment.getProperty(key, Integer.class, defaultValue);
    }

    /**
     *  获取 BigDecimal
     * @param key key
     */
    public static BigDecimal getBigDecimal(String key) {
        return environment.getProperty(key, BigDecimal.class);
    }

    /**
     *  获取 BigDecimal
     * @param key key
     * @param defaultValue the default value to return if no value is found
     */
    public static BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return environment.getProperty(key, BigDecimal.class, defaultValue);
    }

    /**
     *  获取 Double
     * @param key key
     */
    public static Double getDouble(String key) {
        return environment.getProperty(key, Double.class);
    }

    /**
     *  获取 Double
     * @param key key
     * @param defaultValue the default value to return if no value is found
     */
    public static Double getDouble(String key, Double defaultValue) {
        return environment.getProperty(key, Double.class, defaultValue);
    }

    /**
     *  获取long
     * @param key key
     * @param defaultValue the default value to return if no value is found
     */
    public static Long getLong(String key, Long defaultValue) {
        return environment.getProperty(key, Long.class, defaultValue);
    }

    /**
     *  获取long
     * @param key key
     */
    public static Long getLong(String key) {
        return environment.getProperty(key, Long.class);
    }

    /**
     * 获取逗号分隔的字符串为List
     * @param key key
     */
    public static List<String> getStringAsList(String key) {
        String property = environment.getProperty(key);
        if (StringUtils.isBlank(property)) {
            return new ArrayList<>();
        }
        return Stream.of(property.split(DELIMITER)).collect(Collectors.toList());
    }

    /**
     * 获取 Map
     * (此处不缓存， 兼容以后动态刷新环境变量的情境)
     * @param prefix key prefix
     */
    public static Map<String, Object> getMapConfig(String prefix) {
        Map<String, Object> result = new HashMap<>(16);
        if (StringUtils.isBlank(prefix)) {
            return result;
        }
        String prefixedWithDot = prefix + ".";
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof MapPropertySource) {
                MapPropertySource mapPropertySource = (MapPropertySource) propertySource;
                Map<String, Object> sourceMap = mapPropertySource.getSource();
                for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
                    String propertyName = entry.getKey();
                    if (propertyName.startsWith(prefixedWithDot)) {
                        String keyWithoutPrefix = propertyName.substring(prefix.length());
                        result.put(keyWithoutPrefix, entry.getValue());
                    }

                }
            }
        }
        return result;
    }

}


