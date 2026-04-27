package cn.xuqiudong.basic.framework.tool.evn;

import org.springframework.core.env.Environment;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-04-27 19:44
 */
public interface ProcessorEnabled {

    /**
     * 是否启用
     */
    default boolean isEnabled(Environment environment, String propertyName) {
        return environment.getProperty(propertyName, Boolean.class, false);
    }
}
