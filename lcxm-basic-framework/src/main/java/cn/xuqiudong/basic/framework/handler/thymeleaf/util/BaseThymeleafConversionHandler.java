package cn.xuqiudong.basic.framework.handler.thymeleaf.util;

import cn.xuqiudong.basic.framework.tool.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述: Thymeleaf 转换工具对象抽象类
 * @author Vic.xu
 * @since 2022-04-08 9:24
 */
public abstract class BaseThymeleafConversionHandler {

    /**
     * 哪些类型可以转换: 请定义枚举子类
     */
    public interface ConversionType {
        /**
         * 类型名
         * @return 类型
         */
        String type();
    }

    private static final Logger logger = LoggerFactory.getLogger(BaseThymeleafConversionHandler.class);

    /**
     * spring中所有的处理类
     */
    private static final Map<String, BaseThymeleafConversionHandler> CONVERSION_HANDLER_HASH_MAP = new HashMap<>();

    /**
     * 处理的类型
     * @return 类型名
     */
    protected abstract ConversionType type();

    /**
     * 转换的数据，如把useId转为userName
     * @param content 待转换的数据
     * @return 转换
     */
    protected abstract String convert(String content);


    /**
     * 转换某个类型的数据
     * @param content 原数据
     * @param type 类型
     * @return 转换后
     */
    public static String convert(String content, String type) {
        BaseThymeleafConversionHandler handler = getSubAppendJsonHandlerMap().get(type);
        if (handler == null) {
            logger.info("请注册{}类型的BaseThymeleafConversionHandler处理类到spring", type);
            return content;
        }
        return handler.convert(content);
    }


    /**
     * 获得所有的处理类
     *
     * @return all handler
     */
    public static Map<String, BaseThymeleafConversionHandler> getSubAppendJsonHandlerMap() {
        if (CONVERSION_HANDLER_HASH_MAP.size() > 0) {
            return CONVERSION_HANDLER_HASH_MAP;
        }
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (applicationContext == null) {
            logger.info("无法从ApplicationContextHolder中获取ApplicationContext");
            return CONVERSION_HANDLER_HASH_MAP;
        }
        Map<String, BaseThymeleafConversionHandler> beansOfType = applicationContext.getBeansOfType(BaseThymeleafConversionHandler.class);
        if (beansOfType != null) {
            beansOfType.forEach((k, v) -> {
                CONVERSION_HANDLER_HASH_MAP.put(v.type().type(), v);
            });
        }
        return CONVERSION_HANDLER_HASH_MAP;

    }

}
