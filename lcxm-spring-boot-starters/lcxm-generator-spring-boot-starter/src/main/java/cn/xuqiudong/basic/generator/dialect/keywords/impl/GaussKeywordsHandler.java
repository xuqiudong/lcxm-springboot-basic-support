package cn.xuqiudong.basic.generator.dialect.keywords.impl;

import cn.xuqiudong.basic.generator.dialect.keywords.BaseKeyWordsHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * &#x63CF;&#x8FF0;:
 * gauss db&#x5173;&#x952E;&#x5B57;&#x5904;&#x7406;
 *
 * @author Vic.xu
 * @since 2025-09-11 19:29
 */
public class GaussKeywordsHandler extends BaseKeyWordsHandler {

    public GaussKeywordsHandler() {
        super(ALL_KEYWORDS);
    }

    @Override
    public String formatStyle() {
        return "\"%s\"";
    }

    /**
     * GaussDB核心保留字（Reserved Words）
     * 这些单词不能作为标识符（表名、列名等）使用
     */
    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList(
            "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "ASSERTION",
            "AT", "AUTHORIZATION", "BEGIN", "BETWEEN", "BINARY", "BIT", "BOOLEAN", "BOTH",
            "BY", "CALL", "CASCADE", "CASE", "CAST", "CHECK", "CLOB", "CLOSE", "COALESCE",
            "COLUMN", "COMMENT", "COMMIT", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT",
            "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
            "CURRENT_USER", "CURSOR", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT",
            "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT",
            "DO", "DROP", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE",
            "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FOR", "FOREIGN", "FORWARD", "FROM",
            "FULL", "FUNCTION", "GOTO", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE",
            "IN", "INDICATOR", "INITIALLY", "INNER", "INOUT", "INSERT", "INT", "INTEGER",
            "INTERSECT", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LEADING", "LEFT", "LIKE",
            "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LOOP", "MOD", "MODE",
            "MODIFY", "NATIONAL", "NCHAR", "NCLOB", "NEW", "NO", "NOT", "NULL", "NULLIF",
            "NUMERIC", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUT", "OUTER",
            "OVERLAPS", "PLS_INTEGER", "POSITION", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE",
            "PUBLIC", "RAISE", "READ", "REAL", "REFERENCES", "RELEASE", "RENAME", "REPLACE",
            "RETURN", "REVOKE", "RIGHT", "ROLLBACK", "ROW", "ROWID", "ROWNUM", "ROWS", "SAVEPOINT",
            "SCHEMA", "SELECT", "SESSION", "SESSION_USER", "SET", "SMALLINT", "SOME", "SPACE",
            "SQL", "SQLCODE", "SQLERRM", "START", "STDDEV", "SUBSTRING", "SUM", "SYSDATE",
            "SYSTEM_USER", "TABLE", "THEN", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING",
            "TRIGGER", "TRUE", "TRUNCATE", "UNION", "UNIQUE", "UPDATE", "USER", "USING", "VALUE",
            "VALUES", "VARCHAR", "VARCHAR2", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WHILE",
            "WITH"
    ));

    /**
     * GaussDB扩展关键字（华为特有或兼容Oracle/PostgreSQL的关键字）
     */
    private static final Set<String> EXTENDED_KEYWORDS = new HashSet<>(Arrays.asList(
            "ADMIN", "ANALYZE", "ARCHIVE", "ARRAY", "ASENSITIVE", "ATTRIBUTE", "AUDIT", "AVG",
            "BACKUP", "BEFORE", "BLOB", "BLOCK", "BODY", "BREADTH", "BULK", "BYTE", "CACHE",
            "CASTABLE", "CEIL", "CHAINED", "CHARSET", "CHARSETFORM", "CHARSETID", "CHECKPOINT",
            "CLUSTER", "COLLATE", "COLLECTION", "COMMITTED", "COMPRESS", "CONNECT", "CONNECT_BY_ISCYCLE",
            "CONNECT_BY_ISLEAF", "CONNECT_BY_ROOT", "CONSTRUCTOR", "CONTEXT", "CONTENT", "CONVERT",
            "CORR", "CORRESPONDING", "COUNT", "COVAR_POP", "COVAR_SAMP", "CUME_DIST", "CYCLE",
            "DATA", "DATABASE", "DATE", "DAY", "DECIMAL", "DEFINER", "DEGREE", "DEREF", "DESCRIBE",
            "DETERMINISTIC", "DICTIONARY", "DIRECTORY", "DISABLE", "DISCONNECT", "DISTINCT", "DIV",
            "DOUBLE", "DROP_POLICY", "DUMMY", "DUMP", "EACH", "ENABLE", "ENCRYPT", "ESCAPE", "EXCEPTIONS",
            "EXCLUDE", "EXCLUSIVE", "EXECUTE", "EXPLAIN", "EXTENDED", "EXTERNAL", "EXTRACT", "FETCH",
            "FILE", "FIRST", "FIRST_VALUE", "FLOAT", "FLOOR", "FORALL", "FORCE", "FOREIGN", "FORWARD",
            "FOUND", "FREE", "FROM_TZ", "FULL", "FUNCTION", "GLOBAL", "GOTO", "GRANTED", "GROUPING",
            "HASH", "HAVING", "HIERARCHY", "HOUR", "IDENTITY", "IF", "IMMEDIATE", "INCLUDING", "INCREMENT",
            "INDEX", "INDICES", "INF", "INITIAL", "INITRANS", "INLINE", "INSTANCE", "INSTANTIABLE",
            "INTERFACE", "INTERVAL", "ISOLATION", "ITERATE", "JAVA", "LANGUAGE", "LARGE", "LAST",
            "LAST_VALUE", "LATERAL", "LEADING", "LEAST", "LEVEL", "LEVELS", "LIBRARY", "LIKE2", "LIKE4",
            "LIKEC", "LINK", "LIST", "LN", "LOAD", "LOCAL", "LOCATOR", "LOCKED", "LOG", "LOGFILE",
            "LOGGING", "LOWER", "MAP", "MAX", "MAXEXTENTS", "MAXSIZE", "MAXTRANS", "MEMBER", "MERGE",
            "METHOD", "MIN", "MINUTE", "MLSLABEL", "MOD", "MODE", "MODIFY", "MODULE", "MONTH",
            "MULTISET", "NAME", "NAN", "NATIONAL", "NCHAR", "NCLOB", "NEW", "NEXTVAL", "NOARCHIVELOG",
            "NOAUDIT", "NOCOMPRESS", "NOCOPY", "NOFORCE", "NOLOGGING", "NOMAXVALUE", "NOMINVALUE",
            "NONE", "NOORDER", "NOOVERRIDE", "NORELY", "NORMALIZE", "NOSORT", "NOTHING", "NOWAIT",
            "NTH_VALUE", "NTILE", "NUMBER", "NUMERIC", "OBJECT", "OCICOLL", "OCIDATE", "OCIDATETIME",
            "OCIINTERVAL", "OCILOBLOCATOR", "OCINUMBER", "OCIRAW", "OCIREF", "OCIREFCURSOR", "OCISTRING",
            "OLD", "ONLINE", "OPAQUE", "OPERATOR", "OPTION", "ORADATA", "ORDERED", "ORDINALITY",
            "ORGANIZATION", "OUT", "OVER", "OVERLAPS", "OVERRIDE", "PACKAGE", "PARALLEL", "PARAMETER",
            "PARAMETERS", "PARTITION", "PCTFREE", "PCTINCREASE", "PCTUSED", "PERCENT_RANK",
            "PERCENTILE_CONT", "PERCENTILE_DISC", "PLAN", "PLS_INTEGER", "POSITIVE", "POWER", "PRAGMA",
            "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURE",
            "PUBLIC", "PURGE", "QUOTA", "RAISE", "RANGE", "RAW", "READ", "REAL", "REBUILD", "RECURSIVE",
            "REF", "REFERENCES", "REFERENCING", "REFRESH", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT",
            "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RENAME",
            "RESOURCE", "RESPONSIBLE", "RESTRICT", "RESULT_CACHE", "RETURN", "RETURNING", "REVOKE",
            "REWRITE", "RIGHT", "ROLE", "ROLLBACK", "ROLLUP", "ROW", "ROWCOUNT", "ROWID", "ROWNUM",
            "ROWS", "RULE", "SAMPLE", "SAVEPOINT", "SB1", "SB2", "SB4", "SCALE", "SCHEMA", "SCOPE",
            "SCROLL", "SEARCH", "SECOND", "SEGMENT", "SELECT", "SELF", "SENSITIVE", "SEQUENCE",
            "SESSION", "SESSIONTIMEZONE", "SET", "SHARE", "SHOW", "SIGNTYPE", "SIMPLE", "SIZE",
            "SIZE_T", "SMALLINT", "SNAPSHOT", "SOME", "SOURCE", "SPACE", "SPARSE", "SPECIFIC",
            "SPECIFICTYPE", "SQL", "SQLCODE", "SQLERRM", "SQLID", "SQRT", "STANDARD", "START",
            "STATEMENT_ID", "STATIC", "STATISTICS", "STDDEV_POP", "STDDEV_SAMP", "STORAGE", "STRICT",
            "STRUCT", "STYLE", "SUBCAST", "SUBMULTISET", "SUBPARTITION", "SUBSTITUTABLE", "SUCCESSFUL",
            "SUM", "SYNONYM", "SYSDATE", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESPACE", "TEMPORARY",
            "TERMINATE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_ABBR", "TIMEZONE_HOUR", "TIMEZONE_MINUTE",
            "TIMEZONE_OFFSET", "TO", "TO_CHAR", "TO_CLOB", "TO_DATE", "TO_DSINTERVAL", "TO_LOB",
            "TO_MULTI_BYTE", "TO_NCHAR", "TO_NUMBER", "TO_SINGLE_BYTE", "TO_TIMESTAMP", "TO_TIMESTAMP_TZ",
            "TO_YMINTERVAL", "TRIGGER", "TRIM", "TRUE", "TRUNCATE", "TYPE", "UB1", "UB2", "UB4",
            "UID", "UNBOUNDED", "UNION", "UNIQUE", "UNKNOWN", "UNLIMITED", "UNLOCK", "UNNEST", "UPDATE",
            "UPDATABLE", "UPPER", "USE", "USER", "USERENV", "USING", "VALIDATE", "VALIDATION", "VALUE",
            "VARCHAR", "VARCHAR2", "VARIANCE", "VAR_POP", "VAR_SAMP", "VARYING", "VIEW", "VPD", "WAIT",
            "WHEN", "WHERE", "WHILE", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "YEAR", "ZONE"
    ));

    /**
     * 合并保留字和扩展关键字（GaussDB需全面规避）
     */
    private static final Set<String> ALL_KEYWORDS;

    static {
        ALL_KEYWORDS = new HashSet<>(RESERVED_WORDS);
        ALL_KEYWORDS.addAll(EXTENDED_KEYWORDS);
    }

}
