package cn.xuqiudong.basic.core.handler.thymeleaf;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * 描述:thymeleaf 输入到页面的时候进行xss转义  ,${{variable}}  双括号
 * @author Vic.xu
 * @since 2022-03-21 10:46
 */
public class XssStringFormatter implements Formatter<String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String parse(String text, Locale locale) throws ParseException {
        logger.debug("parse {}", text);
        return text;
    }

    /**
     * 输入到页面的时候进行xss转义
     * @param object
     * @param locale
     * @return
     */
    @Override
    public String print(String object, Locale locale) {
        logger.debug("print {}", object);
        return StringEscapeUtils.escapeHtml4(object);
    }
}
