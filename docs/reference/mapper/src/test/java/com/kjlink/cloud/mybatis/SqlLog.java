package com.kjlink.cloud.mybatis;

import java.util.LinkedList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-24
 */
public class SqlLog extends Slf4jImpl {
    public static final String PREPARING = "==>  Preparing: ";
    private static List<String> sqlBuffer = new LinkedList<>();

    public SqlLog(String clazz) {
        super(clazz);
    }

    public static void assertContains(String sql) {
        for (String s : sqlBuffer) {
            if (StrUtil.containsIgnoreCase(s, sql)) {
                sqlBuffer.clear();
                return;
            }
        }
        throw new AssertionError("找不到期望的sql:" + sql + "\n" + sqlBuffer);
    }

    public static void assertNotContains(String sql) {
        for (String s : sqlBuffer) {
            if (StrUtil.containsIgnoreCase(s, sql)) {
                throw new AssertionError("不期望的sql:" + sql + "\n" + sqlBuffer);
            }
        }
    }

    public static void clear() {
        sqlBuffer.clear();
    }

    @Override
    public void debug(String s) {
        if (s.startsWith(PREPARING)) {
            sqlBuffer.add(s.substring(PREPARING.length()));
        }
        super.debug(s);
    }
}
