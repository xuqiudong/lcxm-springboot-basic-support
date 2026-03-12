package cn.xuqiudong.basic.mybatisplus.util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.util.List;

/**
 * 关于SQL的一些工具类
 * 
 * @author Vic.xu
 * @since 2021/10/12
 */
public class SqlUtil {

    private SqlUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 把oracle对应的SQL语句包装为分页语句
     * 
     * @param prefixSql 原来的完整的sql
     * @param page 页码
     * @param size  每页数据量
     */
    public static String oracleLimit(String prefixSql, int page, int size) {
        int start = (page - 1) * size;
        int end = page * size;
        String pattern =
            "select * from( select rownum as rowno, t.*  from ( %s ) t where rownum < %d) tt where tt.rowno > %d";

        return String.format(pattern, prefixSql, end, start);
    }

    /**
     *
     * 说明： 追加in的where条件
     * @author Vic.xu
     * @since 2020年3月3日 下午10:31:20
     * @param sql
     * @param column
     * @param conditions
     * @param needParenthesis
     * @return
     * @throws JSQLParserException e
     */
    public static String appendWhereIn(String sql, String column, List<String> conditions, boolean needParenthesis)
            throws JSQLParserException {
        String append = findIn(column, conditions);
        return appendWhere(sql, append, needParenthesis);

    }

    /**
     *
     * 说明： 追加Equals的where条件
     * @author Vic.xu
     * @since 2020年3月3日 下午10:31:34
     * @param sql
     * @param column
     * @param value
     * @param needParenthesis
     * @return
     * @throws JSQLParserException
     */
    public static String appendWhereEquals(String sql, String column, Object value, boolean needParenthesis)
            throws JSQLParserException {
        String append = findEquals(column, value);
        return appendWhere(sql, append, needParenthesis);

    }

    /**
     *
     * 说明： 追加where 条件
     * @author Vic.xu
     * @since 2020年3月3日 下午10:08:11
     * @param sql             原SQL
     * @param append          需要追加的条件
     * @param needParenthesis 追加的条件是否需要括弧
     * @return
     * @throws JSQLParserException e
     */
    public static String appendWhere(String sql, String append, boolean needParenthesis) throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression where = CCJSqlParserUtil.parseCondExpression(append);
        if (needParenthesis) {
            where = new ParenthesedExpressionList(where);
        }
        if (plainSelect.getWhere() == null) {
            plainSelect.setWhere(where);
        } else {
            plainSelect.setWhere(new AndExpression(plainSelect.getWhere(), where));

        }
        return plainSelect.toString();
    }

    /**
     * 说明： 拼接in 条件： column in ('','')
     * @author Vic.xu
     * @since 2020年3月3日 下午10:11:43
     * @param column
     * @param conditions
     * @return
     */
    public static String findIn(String column, List<String> conditions) {
        return column + " in (" + CommonUtils.list2DatabaseIn(conditions) + ")";
    }

    /**
     * 说明： 拼接等于条件： column ='value'
     * @author Vic.xu
     * @since 2020年3月3日 下午10:11:43
     * @param column
     * @param value
     * @return
     */
    public static String findEquals(String column, Object value) {
        if (value instanceof Number) {
            return column + " = " + value + " ";
        }
        return column + " = '" + value + "' ";
    }




    public static void main(String[] args) {
        String sql = "select * from user_tab_columns a where 1=1 order by a.CHAR_LENGTH desc";
        System.out.println(oracleLimit(sql, 2, 10));
    }

}
