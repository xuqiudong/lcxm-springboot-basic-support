package cn.xuqiudong.basic.core.handler.json;

import cn.xuqiudong.basic.core.handler.json.annotation.AppendJsonField;
import cn.xuqiudong.basic.core.tool.ApplicationContextHolder;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 在追加json字段的时候的处理类
 *
 * @author VIC.xu
 *
 */
public abstract class BaseAppendJsonHandler {

    /**
     * spring中所有的处理类
     */
    private static final Map<String, BaseAppendJsonHandler> SUB_APPEND_JSON_HANDLER_MAP = new HashMap<>();


    private static final Logger logger = LoggerFactory.getLogger(BaseAppendJsonHandler.class);

    /**
     * 序列化
     *
     * @param appendJsonField
     *            追加的field 信息
     * @param value
     *            原字段值
     * @param gen
     *            JsonGenerator
     */
    protected abstract void serialize(AppendJsonField appendJsonField, Object value, JsonGenerator gen);

    /**
     * 标注自己的type
     * @return type
     */
    protected abstract String type();

    /**
     * 根据类型找到抽象类的实例，并将结果拼装出来
     *
     * @param appendJsonField
     *            字段信息
     * @param value origin value
     * @param gen JsonGenerator
     */

    public static void write(AppendJsonField appendJsonField, Object value, JsonGenerator gen) {
        BaseAppendJsonHandler handler = getSubAppendJsonHandlerMap().get(appendJsonField.appendType());
        if (handler == null) {
            logger.info("请注册{}类型的append json 处理类到spring", appendJsonField.appendType());
            return;
        }
        Assert.notNull(appendJsonField.key(), "追加json时,key需要指定");
        handler.serialize(appendJsonField, value, gen);
    }

    /**
     * 获得所有的处理类
     *
     * @return all handler
     */
    public static Map<String, BaseAppendJsonHandler> getSubAppendJsonHandlerMap() {
        if (SUB_APPEND_JSON_HANDLER_MAP.size() > 0) {
            return SUB_APPEND_JSON_HANDLER_MAP;


        }
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (applicationContext == null) {
            logger.info("无法从ApplicationContextHolder中获取ApplicationContext");
            return SUB_APPEND_JSON_HANDLER_MAP;
        }
        Map<String, BaseAppendJsonHandler> beansOfType = applicationContext.getBeansOfType(BaseAppendJsonHandler.class);
        if (beansOfType != null) {
            beansOfType.forEach((k, v) -> {
                SUB_APPEND_JSON_HANDLER_MAP.put(v.type(), v);
            });
        }
        return SUB_APPEND_JSON_HANDLER_MAP;

    }

}
