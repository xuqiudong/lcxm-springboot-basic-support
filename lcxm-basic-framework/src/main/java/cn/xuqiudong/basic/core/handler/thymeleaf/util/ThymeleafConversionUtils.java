package cn.xuqiudong.basic.core.handler.thymeleaf.util;

/**
 * 描述:描述:转换工具类，比如把用户id转为用户name
 * @author Vic.xu
 * @since 2022-04-07 17:48
 */
public class ThymeleafConversionUtils {

    /**
     *转换数据：使用方式${#conversion.convert('someValue','type')}
     * @param content 原数据
     * @param type 类型
     * @return 转换
     */
    public String convert(String content, String type) {
        return BaseThymeleafConversionHandler.convert(content, type);
    }
}
