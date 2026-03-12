package cn.xuqiudong.basic.core.tool.evn;

import cn.xuqiudong.basic.core.util.ApplicationPropertiesUtil;
import cn.xuqiudong.basic.core.util.encrypt.Base62Enhance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述:配置文件解密后置处理器
 * 原理：spring application context refreshed之前定制application运行环境，插入/修改配置信息 <br />
 * 另：jasypt-spring-boot-starter的处理时机是 EnableEncryptablePropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor#postProcessBeanFactory<br />
 * @author Vic.xu
 * @since 2022-03-22 9:31
 */
public class DecryptEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 配置文件中配置salt的key
     */
    private static final String SALT_KEY = "decrypt.salt";

    /**
     * 获取配置文件的salt，没有则默认为vic.xu
     */
    private static final String SALT = ApplicationPropertiesUtil.getString(SALT_KEY, "vic.xu");

    /**
     * 加密的属性的前缀：dec() + 密文    eg: dec()passWord
     */
    private final static String DECRYPT_PREFIX = "dec()";

    private final static int DECRYPT_PREFIX_LENGTH = DECRYPT_PREFIX.length();

    /**
     *创建一个简单的加密实例
     */
    private static final Base62Enhance ENHANCE = Base62Enhance.createInstance(SALT);

    /**
     * 替换过的resource的name
     */
    private static final String DECRYPT_RESOURCE_NAME = "decryptResource";


    private final Logger logger = LoggerFactory.getLogger(DecryptEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, Object> replacedMap = new HashMap<>(16);
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof MapPropertySource) {
                MapPropertySource ps = (MapPropertySource) propertySource;
                replace(ps, replacedMap);
            }
        }
        if (!replacedMap.isEmpty()) {
            logger.info("{} decrypt properties  has replace !", replacedMap.size());
            propertySources.addFirst(new MapPropertySource(DECRYPT_RESOURCE_NAME, replacedMap));
        }

    }

    /**
     * 找出需要解密的value并解密
     * @param ps
     * @param replacedMap  replacedMap
     */
    private void replace(MapPropertySource ps, Map<String, Object> replacedMap) {
        for (String name : ps.getPropertyNames()) {
            Object value = ps.getProperty(name);
            if (value != null) {
                String v = String.valueOf(value);
                //解密的value以dec()开头
                if (v.length() <= DECRYPT_PREFIX_LENGTH || !v.startsWith(DECRYPT_PREFIX)) {
                    continue;
                }
                v = v.substring(DECRYPT_PREFIX_LENGTH);
                v = ENHANCE.decode(v);
                replacedMap.put(name, v);
            }
        }
    }

    /**
     * 对外提供的加密方法， 和此中解码方法对应
     */
    public static String enc(String value) {
        return ENHANCE.encode(value);
    }
}