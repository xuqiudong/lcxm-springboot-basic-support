package cn.xuqiudong.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.xuqiudong.common.query.Column;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
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
     * 安全字段名称： 检测是否合法字段， 并驼峰转下划线
     */
    public static String safeColumn(String column) {
        Assert.notNull(column, "column can not be null");
        checkSafe(column);
        return UNDER_LINE_MAP.computeIfAbsent(column, (col) -> StringUtils.camelToUnderline(column));
    }

    /**
     * 安全字段名称： 检测是否合法字段， 并驼峰转下划线
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
        return FIELD_NAME_CACHE.computeIfAbsent(key, k -> {
            String fieldName = BeanUtil.getFieldName(methodName);
            return fieldName;
        });
    }



}
