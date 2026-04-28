package cn.xuqiudong.basic.framework.env.processor;

import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-04-27 19:44
 */
public interface ProcessorEnabled extends Ordered {

    Logger LOGGER = new Logger();

    /**
     * 是否启用
     */
    default boolean isEnabled(Environment environment, String propertyName, boolean defaultValue) {
        return environment.getProperty(propertyName, Boolean.class, defaultValue);
    }

    /**
     * 内部 Logger 类，模拟 SLF4J 的常用方法
     * EnvironmentPostProcessor runs before logging is initialized, so you cannot rely on logging in this phase
     */
     class Logger {
        void info(String msg, Object... args) {
            System.out.println(format("[INFO] " + msg, args));
        }

        void debug(String msg, Object... args) {
            System.out.println(format("[DEBUG] " + msg, args));
        }

        void warn(String msg, Object... args) {
            System.err.println(format("[WARN] " + msg, args));
        }

        void error(String msg, Object... args) {
            System.err.println(format("[ERROR] " + msg, args));
        }

        private String format(String pattern, Object... args) {
            try {
                return java.text.MessageFormat.format(pattern.replace("'", "''"), args);
            } catch (Exception e) {
                // fallback to simple replace if MessageFormat fails
                String result = pattern;
                for (Object arg : args) {
                    result = result.replaceFirst("\\{\\}", String.valueOf(arg));
                }
                return result;
            }
        }
    }
}
