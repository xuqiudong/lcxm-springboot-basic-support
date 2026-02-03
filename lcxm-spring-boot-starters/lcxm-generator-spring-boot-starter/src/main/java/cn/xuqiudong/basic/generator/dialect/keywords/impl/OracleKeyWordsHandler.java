package cn.xuqiudong.basic.generator.dialect.keywords.impl;

import cn.xuqiudong.basic.generator.dialect.keywords.BaseKeyWordsHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 描述:
 * ORACLE 关键字处理类
 * 参考：https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/Oracle-SQL-Reserved-Words.html
 *
 * @author Vic.xu
 * @since 2025-09-11 19:23
 */
public class OracleKeyWordsHandler extends BaseKeyWordsHandler {

    public OracleKeyWordsHandler() {
        super(KEY_WORDS);
    }

    @Override
    public String formatStyle() {
        return "\"%s\"";
    }

    private static final Set<String> KEY_WORDS = new HashSet<>(Arrays.asList(
            "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT",
            "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMMIT",
            "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT",
            "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS",
            "FILE", "FLOAT", "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED",
            "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL", "INSERT", "INTEGER",
            "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS",
            "MINUS", "MLSLABEL", "MODE", "MODIFY", "NATIONAL", "NOAUDIT", "NOCOMPRESS",
            "NOT", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE", "OPTION",
            "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME",
            "RESOURCE", "RETURN", "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS", "SELECT",
            "SESSION", "SET", "SHARE", "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM",
            "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE",
            "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHEN", "WHERE", "WITH"
    ));

}
