package cn.xuqiudong.common.util;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.common.query.Column;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

/**
 * 描述:
 *    封装查询条件  QueryWrapper 的 工具类
 * @author Vic.xu
 * @since 2025-11-06 16:29
 */
public class WrapUtils {

    /**
     * 创建查询条件
     */
    public static <T, R> QueryWrapper<T> createWrapper(Column<T, R> column, R value) {
       QueryWrapper<T> queryWrapper = Wrappers.query();
       queryWrapper.eq(isNotEmpty(value), ColumnUtils.safeColumn(column), value);
       return queryWrapper;
    }

    private static boolean isNotEmpty(Object value) {
        if (value instanceof CharSequence) {
            return StrUtil.isNotEmpty((CharSequence) value);
        }
        return value != null;
    }

}
