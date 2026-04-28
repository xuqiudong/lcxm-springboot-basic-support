package cn.xuqiudong.basic.framework.env.processor;

import cn.xuqiudong.basic.core.util.encrypt.Base62Enhance;
import cn.xuqiudong.basic.framework.env.LcxmEnvConstant;
import org.apache.commons.lang3.StringUtils;
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
 * <p>
 * 1. 配置方式：
 * META-INF/spring.factories
 * org.springframework.boot.env.EnvironmentPostProcessor=cn.xuqiudong.basic.framework.env.processor.DecryptEnvironmentPostProcessor
 * 2. 配置项：
 * lcxm.env.decrypt.enable=true    #是否启用配置文件解密
 * lcxm.env.decrypt.prefix=dec()   #加密的属性的前缀
 * lcxm.env.decrypt.salt=vic.xu    # 配置文件解密中的salt
 *
 * </p>
 *
 * @author Vic.xu
 * @since 2022-03-22 9:31
 */
public class DecryptEnvironmentPostProcessor implements EnvironmentPostProcessor, ProcessorEnabled {


    /**
     * 获取配置文件的salt，没有则默认为vic.xu
     */
    private String salt;

    /**
     * 加密的属性的前缀：dec() + 密文    eg: dec()passWord
     */
    private String decryptPrefix;

    private int decryptPrefixLength;

    /**
     * 创建一个简单的加密实例
     */
    private Base62Enhance enhance;

    /**
     * 替换过的resource的name
     */
    private static final String DECRYPT_RESOURCE_NAME = "decryptResource";


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!isEnabled(environment, LcxmEnvConstant.ENABLE_DECRYPT_CONFIG_KEY)) {
            LOGGER.info("未启用配置文件解密，跳过配置文件解密");
            return;
        }
        initConfig(environment);
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (propertySource instanceof MapPropertySource) {
                MapPropertySource ps = (MapPropertySource) propertySource;
                replace(propertySources, ps);
            }
        }
    }

    /**
     * 初始化配置
     */
    private void initConfig(ConfigurableEnvironment environment) {
        salt = environment.getProperty(LcxmEnvConstant.DECRYPT_SALT_KEY);
        if (StringUtils.isBlank(salt)) {
            salt = LcxmEnvConstant.DEFAULT_DECRYPT_SALT;
        }
        decryptPrefix = environment.getProperty(LcxmEnvConstant.DECRYPT_PREFIX_KEY);
        if (StringUtils.isBlank(decryptPrefix)) {
            decryptPrefix = LcxmEnvConstant.DEFAULT_DECRYPT_PREFIX;
        }
        decryptPrefixLength = decryptPrefix.length();
        enhance = Base62Enhance.createInstance(salt);
    }


    /**
     * 找出需要解密的value并解密 然后replace source
     *
     */
    private void replace(MutablePropertySources propertySources, MapPropertySource ps) {
        Map<String, Object> newSource = new HashMap<>(ps.getSource());
        boolean isReplaced = false;
        for (String name : ps.getPropertyNames()) {
            Object value = ps.getProperty(name);
            if (value != null) {
                String v = String.valueOf(value);
                //解密的value以dec()开头
                if (v.length() <= decryptPrefixLength || !v.startsWith(decryptPrefix)) {
                    continue;
                }
                isReplaced = true;
                v = v.substring(decryptPrefixLength);
                v = enhance.decode(v);
                newSource.put(name, v);
                LOGGER.debug("{} decrypt success !", name);

            }
        }
        if (isReplaced) {
            // 替换原来的 PropertySource，顺序保持不变 ★
            propertySources.replace(ps.getName(), new MapPropertySource(ps.getName(), newSource));
        }
    }

    /**
     * 对外提供的加密方法， 和此中解码方法对应
     */
    public static String enc(String value, String salt) {
        return Base62Enhance.createInstance(salt).encode(value);
    }

    @Override
    public int getOrder() {
        return -9;
    }
}