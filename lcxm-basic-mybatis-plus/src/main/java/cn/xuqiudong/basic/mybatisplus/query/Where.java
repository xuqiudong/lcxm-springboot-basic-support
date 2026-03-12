package cn.xuqiudong.basic.mybatisplus.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 描述:
 * 同 QueryWrapper  主要为了方便使用 ${where} 而不是${ew.customSqlSegment}
 * 也不需要在接口方法内写 @Param(Constants.WRAPPER) Wrapper<T> queryWrapper,
 *
 * @author Vic.xu
 * @since 2025-11-19 17:38
 */
public class Where<T> extends QueryWrapper {

    /**
     * 方便在 xml 中使用 ${where} 而不是${ew.customSqlSegment}, <br /
     * 也不需要在mapper方法内写 @Param(Constants.WRAPPER) Wrapper<T> queryWrapper,
     * <code>
     * List<T> selectByWhere(Where where)
     * xml:
     * select * from table where ${where}
     * </code>
     *
     * @see #getCustomSqlSegment()
     */
    @Override
    public String toString() {
        String sqlSegment = getCustomSqlSegment();
        return sqlSegment.replaceAll("ew\\.", "where.");
    }
}
