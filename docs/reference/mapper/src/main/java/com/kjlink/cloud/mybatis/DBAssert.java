package com.kjlink.cloud.mybatis;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.apache.ibatis.exceptions.PersistenceException;

/**
 * 数据库操作断言
 *
 * @author Fulai
 * @since 2025-08-28
 */
public class DBAssert {

    public static void checkUpdate(int ret) {
        if (ret == 0) {
            throw new MybatisPlusException("更新数据记录失败，找不到该记录");
        }
        if (ret != 1) {
            throw new MybatisPlusException(StrUtil.format("更新数据记录失败，受影响的行数为:{}", ret));
        }
    }

    public static void checkInsert(int ret) {
        if (ret != 1) {
            throw new PersistenceException("保存数据记录失败");
        }
    }

    public static void checkDelete(int ret) {
        if (ret == 0) {
            throw new PersistenceException("删除数据记录失败，找不到该记录");
        }
        if (ret != 1) {
            throw new PersistenceException(StrUtil.format("删除数据记录失败，实际删除的记录数为:{}", ret));
        }
    }

    /**
     * 并发修改
     *
     * @param ret
     * @param model
     */
    public static void checkSafe(int ret, String model) {
        if (ret != 1) {
            throw new PersistenceException(StrUtil.format("更新{}异常，请刷新后重试", model));
        }
    }

    public static void check(int ret, String message, Object... arguments) {
        if (ret != 1) {
            throw new PersistenceException(StrUtil.format(message, arguments));
        }
    }

}
