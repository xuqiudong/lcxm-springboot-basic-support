package cn.xuqiudong.basic.framework.constant;

/**
 * Description:
 * lcxm 框架部分一些常量：environment 相关
 * @author Vic.xu
 * @since 2026-04-27 19:58
 */
public interface LcxmFrameworkEnvConstant {

    /**
     * 是否启用外部配置 的配置项 lcxm.env.external.enable=true
     * @see cn.xuqiudong.basic.framework.tool.evn.ExternalConfigProcessor;
     */
    String ENABLE_EXTERNAL_CONFIG_KEY = "lcxm.env.external.enable";

    /**
     * 外部配置文件路径的配置项   lcxm.env.external.location=/data/config/config.properties
     * @see cn.xuqiudong.basic.framework.tool.evn.ExternalConfigProcessor;
     */
    String EXTERNAL_CONFIG_LOCATION_KEY = "lcxm.env.external.location";


    /**
     * 是否启用配置文件解密的配置项 lcxm.env.decrypt.enable=true
     * @see cn.xuqiudong.basic.framework.tool.evn.DecryptEnvironmentPostProcessor;
     */
    String ENABLE_DECRYPT_CONFIG_KEY = "lcxm.env.decrypt.enable";
    /**
     * 配置文件解密 中配置salt的key lcxm.env.decrypt.salt=vic.xu
     * @see cn.xuqiudong.basic.framework.tool.evn.DecryptEnvironmentPostProcessor;
     */
    String DECRYPT_SALT_KEY = "lcxm.env.decrypt.salt";

    /**
     *配置文件解密 默认的salt
     */
    String DEFAULT_DECRYPT_SALT = "vic.xu";

    /**
     *加密的属性的前缀 配置项：dec() + 密文    eg: dec()passWord
     */
    String DECRYPT_PREFIX_KEY = "lcxm.env.decrypt.prefix";

    /**
     * 加密的参数的默认前缀：dec() + 密文    eg: dec()passWord
     */
    String DEFAULT_DECRYPT_PREFIX = "dec()";

}
