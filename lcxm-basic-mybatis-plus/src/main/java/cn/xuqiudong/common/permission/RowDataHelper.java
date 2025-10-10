package cn.xuqiudong.common.permission;

import cn.xuqiudong.common.permission.enums.RowDataHandlerType;
import cn.xuqiudong.common.permission.model.DataRowAuthColumn;
import cn.xuqiudong.common.permission.model.DataRowAuthModel;
import org.apache.ibatis.mapping.BoundSql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


/**
 * 数据权限拦器 入口
 *
 * @author Vic.xu
 * @since 2025-09
 */
public class RowDataHelper {
    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 当前线程中需要拦截的权限字段数据
     */
    protected static final ThreadLocal<DataRowAuthModel> LOCAL_AUTHORITY_DATA = new ThreadLocal<DataRowAuthModel>();

    /**
     * 设置 AuthorityData 参数
     *
     * @param authorityData DataRowAuthModel
     */
    protected static void setLocalAuthorityData(DataRowAuthModel authorityData) {
        LOCAL_AUTHORITY_DATA.set(authorityData);
    }

    /**
     * 获取 AuthorityData 参数
     *
     * @return
     */
    public static DataRowAuthModel getLocalAuthorityData() {
        return LOCAL_AUTHORITY_DATA.get();
    }

    /**
     * 移除本地变量
     */
    public static void clear() {
        LOCAL_AUTHORITY_DATA.remove();
    }

    /**
     * 业务代码设置过滤条件
     *
     * @param type   数据行权限过滤类型
     * @param column SQL 中需要过滤的字段 如a.dept_id
     */
    public static void start(RowDataHandlerType type, String column) {
        start(type, column, null);
    }

    /**
     * 业务代码设置过滤条件
     *   最终形成的WHERE条件为：(前置条件 AND 权限条件)
     * @param type          数据行权限过滤类型
     * @param column        SQL 中需要过滤的字段 如a.dept_id
     * @param precondition  前置判断条件（如"a.xx_id is not null"）
     */
    public static void start(RowDataHandlerType type, String column, String precondition) {
        DataRowAuthModel data = getLocalAuthorityData();
        if (data == null) {
            data = new DataRowAuthModel();
        }
        DataRowAuthColumn filterColumn = new DataRowAuthColumn(type, column, precondition);
        data.addFilterColumn(filterColumn);
        setLocalAuthorityData(data);
    }

    /**
     * 用处理后的SQL覆盖原始SQL
     * <p>
     * 通过反射把SQL设置回去
     */
    public void overrideSql(BoundSql boundSql, String sql) {
        try {
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
