package com.kjlink.cloud.mybatis.interceptor;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.PluginException;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.kjlink.cloud.mybatis.DBUtil;
import com.kjlink.cloud.mybatis.meta.CurrentUserInfoUtil;

/**
 * 拦截delete sql，备份数据到t_sys_trash表
 *
 * @author Fulai
 * @since 2024-09-11
 */
public class TrashInnerInterceptor implements InnerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(TrashInnerInterceptor.class);
    private static final String INSERT_SQL_1 = "INSERT INTO ";
    private static final String INSERT_SQL_2 = " (id, http_method, request_url, thread_id, delete_by, delete_time, " +
                                               "table_name, location, data) VALUES (?,?,?,?,?,?,?,?,?)";
    //日志表名称
    private String logTableName = "sys_trash";
    //最大支持的LOB大小：16MB，防止内存溢出, 也是Mysql的MEDIUMBLOB, MEDIUMTEXT类型最大长度
    private long maxLobLength = 16777216;
    //指定要包含的表名称前缀(不配置或为ALL包含所有表）
    private String[] includeTables;
    //指定要排除的表名称前缀（先包含再排除）
    private String[] excludeTables;

    @Override
    public void setProperties(Properties properties) {
        String tableName = properties.getProperty("tableName");
        if (StrUtil.isNotBlank(tableName)) {
            this.logTableName = tableName;
        }
        String length = properties.getProperty("maxLobLength");
        if (NumberUtil.isLong(length)) {
            this.maxLobLength = NumberUtil.parseLong(length);
        }
        String excludeTablesStr = properties.getProperty("excludeTables");
        if (StrUtil.isNotBlank(excludeTablesStr)) {
            this.excludeTables = StrUtil.splitToArray(excludeTablesStr, ",");
        }
        String includeTablesStr = properties.getProperty("includeTables");
        if (StrUtil.isNotBlank(includeTablesStr)) {
            this.includeTables = StrUtil.splitToArray(includeTablesStr, ",");
        }
    }

    public void setProperties(TrashPluginProperties properties) {
        this.logTableName = properties.getTable();
        this.maxLobLength = properties.getMaxLength();
        this.excludeTables = properties.getExcludes();
        this.includeTables = properties.getIncludes();
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreOthersByKey(ms.getId(), "trash")) {
                return;
            }
            beforeDelete(executor, ms, parameter);
        }
    }

    //@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    private void beforeDelete(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameter);
        //原始删除SQL
        String deleteSql = boundSql.getSql();
        //仅支持简单的delete语言
        if (!checkIsDelete(deleteSql)) {
            return;
        }
        //获取原始表
        Table table = getTable(deleteSql);
        String tableName = table.getName().toLowerCase(Locale.ROOT);

        //判断是否要忽略
        if (!isIncludeTable(tableName)) {
            return;
        }

        //删除人
        String subject = CurrentUserInfoUtil.getUsername();
        //打印日志
        LOG.info("用户{}将从表{}中删除数据，备份到回收站", subject, tableName);
        StopWatch stopWatch = StopWatch.create("");
        stopWatch.start();

        //获取数据库连接
        Transaction transaction = executor.getTransaction();
        //复用连接，不需要关闭
        Connection connection = transaction.getConnection();
        //构建快照
        List<SnapshotRecord> snapshotList = buildSnapshot(ms, parameter, boundSql, connection);
        //为空时不需要备份
        if (!snapshotList.isEmpty()) {
            saveSnapshot(ms, connection, snapshotList, tableName, subject);
        }
        stopWatch.stop();
        LOG.info("备份了{}条数据，用时{}ms", snapshotList.size(), stopWatch.getTotalTimeMillis());
    }

    private void saveSnapshot(MappedStatement ms, Connection connection, List<SnapshotRecord> snapshotList,
            String tableName, String subject) throws SQLException {
        PreparedStatement insertStatement = null;
        try {
            String json = SnapshotRecord.serialize(snapshotList);
            //触发删除的mapper方法
            String location = ms.getId();

            //插入回收站SQL
            String insertSql = createInsertSql();

            //多条记录使用相同的删除时间
            Timestamp deleteTime = Timestamp.from(Instant.now());
            //当前请求
            HttpServletRequest currentRequest = getCurrentRequest();
            String httpMethod = currentRequest == null ? "" : currentRequest.getMethod();
            String requestUrl = currentRequest == null ? "" : currentRequest.getRequestURI();
            //线程id
            String threadId = Thread.currentThread().getName();

            insertStatement = prepareStatement(connection, insertSql);
            insertStatement.setString(1, DBUtil.nextId());
            insertStatement.setString(2, httpMethod);
            insertStatement.setString(3, DBUtil.truncateVarchar(requestUrl, 256));
            insertStatement.setString(4, DBUtil.truncateVarchar(threadId, 36));
            insertStatement.setString(5, subject);
            insertStatement.setTimestamp(6, deleteTime);
            insertStatement.setString(7, tableName);
            insertStatement.setString(8, DBUtil.truncateVarchar(location, 256));
            insertStatement.setString(9, json);
            insertStatement.execute();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeStatement(insertStatement);
        }
    }

    private List<SnapshotRecord> buildSnapshot(MappedStatement ms, Object parameter, BoundSql boundSql,
            Connection connection) throws SQLException {
        List<SnapshotRecord> resultList = new LinkedList<>();
        //需要关闭的对象
        PreparedStatement selectStatement = null;
        ResultSet selectResult = null;
        try {
            //查询SQL
            String selectSql = createSelectSql(boundSql);
            selectStatement = prepareStatement(connection, selectSql);

            Configuration configuration = ms.getConfiguration();
            //查询参数设置工具
            ParameterHandler parameterHandler = configuration.newParameterHandler(ms, parameter, boundSql);
            parameterHandler.setParameters(selectStatement);

            selectResult = selectStatement.executeQuery();
            ResultSetMetaData metaData = selectResult.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            JdbcType[] columnTypes = new JdbcType[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
                columnTypes[i] = getMyBatisJdbcType(metaData.getColumnType(i + 1));
            }
            while (selectResult.next()) {
                SnapshotRecord data = new SnapshotRecord();
                for (int i = 0; i < columnCount; i++) {
                    Object value = readObject(selectResult, i + 1, columnTypes[i]);
                    data.put(columnNames[i], value);
                }
                resultList.add(data);
            }
        } finally {
            JdbcUtils.closeResultSet(selectResult);
            JdbcUtils.closeStatement(selectStatement);
        }
        return resultList;
    }

    private JdbcType getMyBatisJdbcType(int columnType) {
        return JdbcType.forCode(columnType);
    }

    private Object readObject(ResultSet selectResult, int columnIndex, JdbcType jdbcType) throws SQLException {
        if (jdbcType == JdbcType.BLOB) {
            Blob blob = selectResult.getBlob(columnIndex);
            if (blob == null) {
                return null;
            }
            long length = blob.length();
            if (length == 0) {
                return null;
            }

            if (length > maxLobLength) {
                throw new PluginException(StrUtil.format("记录删除日志时发现太大的clob字段,len={}", length));
            }

            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return bytes;
        } else if (jdbcType == JdbcType.CLOB) {
            Clob clob = selectResult.getClob(columnIndex);
            if (clob == null) {
                return null;
            }
            if (clob.length() == 0) {
                return "";
            }

            if (clob.length() > maxLobLength) {
                throw new PluginException(StrUtil.format("记录删除日志时发现太大的clob字段,len={}", clob.length()));
            }

            String content = clob.getSubString(1, (int) clob.length());
            return content;
        } else if (jdbcType == JdbcType.TIMESTAMP) {
            return selectResult.getTimestamp(columnIndex);
        }
        return selectResult.getObject(columnIndex);
    }

    private PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    private boolean isIncludeTable(String tableName) {
        //删除自己时，不记录日志
        if (tableName.equalsIgnoreCase(this.logTableName)) {
            return false;
        }

        //判断是否包含，默认包含所有表
        if (ArrayUtil.isEmpty(includeTables) || (includeTables.length == 1 && "ALL".equals(includeTables[0]))
            || StrUtil.startWithAnyIgnoreCase(tableName, includeTables)) {
            //再判断排除规则
            if (ArrayUtil.isEmpty(excludeTables)) {
                //需要包含
                return true;
            }
            //需要包含
            return !StrUtil.startWithAnyIgnoreCase(tableName, excludeTables);
        }

        //不匹配
        return false;
    }

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            Object request = requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            if (request instanceof HttpServletRequest) {
                return (HttpServletRequest) request;
            }
        }
        return null;
    }

    private String createInsertSql() {
        return INSERT_SQL_1 + logTableName + INSERT_SQL_2;
    }

    private String createSelectSql(BoundSql boundSql) {
        return "SELECT *" + boundSql.getSql().substring(6);
    }

    private boolean checkIsDelete(String deleteSql) {
        String keywords = deleteSql.substring(0, 6);
        if (!"delete".equalsIgnoreCase(keywords)) {
            LOG.error("删除语句需要以delete开头, Error SQL: {}", deleteSql);
            return false;
        }
        return true;
    }

    private Table getTable(String deleteSql) throws SQLException {
        try {
            Statement statement = CCJSqlParserUtil.parse(deleteSql);
            if (statement instanceof Delete) {
                Delete deleteStat = (Delete) statement;
                List<Table> tables = deleteStat.getTables();

                if (CollUtil.isNotEmpty(tables)) {
                    throw new PluginException(
                            StrUtil.format("记录删除日志目前仅支持单表删除语句, Error" + " SQL: {}", deleteSql));
                }
                return deleteStat.getTable();
            }
        } catch (JSQLParserException e) {
            throw new SQLException(e);
        }
        throw new PluginException(StrUtil.format("删除语句解析错误, Error SQL: {}", deleteSql));
    }

}
