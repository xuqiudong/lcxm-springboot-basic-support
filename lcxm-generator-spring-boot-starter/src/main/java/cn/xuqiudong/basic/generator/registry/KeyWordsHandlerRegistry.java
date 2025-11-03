package cn.xuqiudong.basic.generator.registry;

import cn.xuqiudong.basic.generator.dialect.keywords.BaseKeyWordsHandler;
import cn.xuqiudong.basic.generator.dialect.keywords.impl.MySqlKeyWordsHandler;
import cn.xuqiudong.basic.generator.dialect.keywords.impl.OracleKeyWordsHandler;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 * 注册不同数据库的关键字处理器  <br />
 * 表字段如果是关键字则对其格式化: 加引号 或者反引号
 *
 * @author Vic.xu
 * @since 2025-09-11 17:49
 */
public class KeyWordsHandlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyWordsHandlerRegistry.class);

    /**
     * 持有 数据库关键字处理器
     */
    private static Map<DatabaseType, BaseKeyWordsHandler> REGISTRY = new HashMap<>();


    static {
        registerDefault();
    }

    /**
     * 注册默认关键字处理器
     */
    public static void registerDefault() {
        register(DatabaseType.mysql, new MySqlKeyWordsHandler());
        register(DatabaseType.oracle, new OracleKeyWordsHandler());
        register(DatabaseType.gauss, new OracleKeyWordsHandler());
    }

    /**
     * 注册 数据库关键字处理器
     */
    public static void register(DatabaseType databaseType, BaseKeyWordsHandler keyWordsHandler) {
        REGISTRY.put(databaseType, keyWordsHandler);
    }


    /**
     * 格式化表字段名称
     */
    public static String formatColumn(DatabaseType databaseType, String columnName) {
        BaseKeyWordsHandler keyWordsHandler = REGISTRY.get(databaseType);
        if (keyWordsHandler == null) {
            LOGGER.warn("未找到数据库类型：{} 的关键字处理类", databaseType);
            return columnName;
        }
        if (keyWordsHandler.isKeyWords(columnName)) {
            return keyWordsHandler.formatColumn(columnName);
        }
        return columnName;
    }

}
