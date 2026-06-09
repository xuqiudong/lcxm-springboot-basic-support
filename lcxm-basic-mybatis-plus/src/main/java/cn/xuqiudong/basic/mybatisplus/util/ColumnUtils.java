package cn.xuqiudong.basic.mybatisplus.util;

import cn.hutool.core.bean.BeanUtil;
import cn.xuqiudong.basic.mybatisplus.query.Column;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 描述:
 * mp 中一些字段处理
 *
 * @author Vic.xu
 * @since 2025-10-29 11:33
 */
public class ColumnUtils {

    /**
     * 缓存字段名称： 驼峰转下划线
     */
    private static final Map<String, String> UNDER_LINE_MAP = new ConcurrentHashMap<>();

    /**
     * 缓存字段名称： 获取字段名称
     */
    private static final Map<String, String> FIELD_NAME_CACHE = new ConcurrentHashMap<>();
    /**
     * 匹配字段名称： 检测是否合法字段  字母数字 下划线和点
     * . 字符在方括号 [] 内部时不需要转义（\）
     */
    private static final Pattern SAFE_COLUMN_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    /**
     * 安全字段名称, 如果被转义引号包裹，会除去后检测，最终返回的时候再追加回去
     * <p>
     * 1. SQL注入检查
     * 2. 驼峰转下划线
     * 3. 支持数据库字段转义符
     * </p>
     */
    public static String safeColumn(String column) {
        Assert.notNull(column, "column can not be null");
        // 缓存命中直接返回
        String cacheValue = UNDER_LINE_MAP.get(column);
        if (cacheValue != null) {
            return cacheValue;
        }
        String originColumn = column;

        column = column.trim();

        // 解析转义符: 判断是否被转义符包裹
        ColumnParseResult parseResult = unwrapColumn(column);
        // 获取去除转义符的字段名称
        String realColumn = parseResult.column();

        // 安全检查：SQL注入检查; 名称 合法字段: 字母数字 下划线和点
        checkSafe(realColumn);

        // 驼峰转下划线
        String result = StringUtils.camelToUnderline(realColumn);

        // 恢复转义符
        if (parseResult.quote() != null) {
            result = parseResult.quote().wrap(result);
        }

        UNDER_LINE_MAP.put(originColumn, result);

        return result;
    }

    /**
     * 安全字段名称： 检测是否合法字段， 并驼峰转下划线
     *
     * @param column: XxxEntity::getName
     */
    public static <T, R> String safeColumn(Column<T, R> column) {
        String name = getFieldName(column);
        return safeColumn(name);
    }

    private static void checkSafe(String column) {
        String msg = "column is illegal: [" + column + "]";
        boolean isIllegal = SqlInjectionUtils.check(column);
        Assert.isTrue(!isIllegal, msg);
        Assert.isTrue(SAFE_COLUMN_PATTERN.matcher(column).matches(), msg);
    }

    /**
     * 获取lambda字段名称
     *
     * @param column: XxxEntity::getName
     */
    public static <T> String getFieldName(Column<T, ?> column) {
        // 1. 先生成稳定的 key: className + methodName
        LambdaMeta lambda = LambdaUtils.extract(column);
        String className = lambda.getInstantiatedClass().getName();
        String methodName = lambda.getImplMethodName();
        String key = className + "#" + methodName;
        // 2. 缓存字段名称
        return FIELD_NAME_CACHE.computeIfAbsent(key, k -> BeanUtil.getFieldName(methodName));
    }

    /**
     * 解析字段: 检测是否需包含转义符
     */
    private static ColumnParseResult unwrapColumn(String column) {
        for (ColumnQuote quote : ColumnQuote.values()) {
            if (quote.match(column)) {
                return new ColumnParseResult(quote.unwrap(column), quote);
            }
        }
        return new ColumnParseResult(column, null);
    }


    /**
     * 字段名称的引号:  数据库字段转义符
     */
    @Getter
    public enum ColumnQuote {
        /**
         * MySQL
         */
        MYSQL('`', '`'),

        /**
         * Oracle / PostgreSQL / GaussDB
         */
        DOUBLE_QUOTE('"', '"'),

        /**
         * SQL Server
         */
        SQL_SERVER('[', ']');

        private final char left;

        private final char right;

        ColumnQuote(char left, char right) {
            this.left = left;
            this.right = right;
        }

        /**
         * 是否匹配
         */
        public boolean match(String str) {
            return str.length() >= 2
                    && str.charAt(0) == left
                    && str.charAt(str.length() - 1) == right;
        }

        /**
         * 去除转义符
         */
        public String unwrap(String str) {
            return str.substring(1, str.length() - 1);
        }

        /**
         * 添加转义符
         */
        public String wrap(String str) {
            return left + str + right;
        }
    }


    /**
     * 定义解析结果: 去除 和 添加转义符
     *
     * @param column 去除转义符后的字段
     * @param quote  原转义符
     */
    private record ColumnParseResult(String column, ColumnQuote quote) {
    }
}
