package cn.xuqiudong.generator.model;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 说明:
 * 不同数据库一些sql的差别，
 * 后续可考虑直接修改为生成多个mapper.xml,并在xml中加入databaseId选项
 *
 * @author Vic.xu
 * @since 2023/6/14/0014 17:13
 */
public class DataBaseDialectInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 获取当前时间的语句， 比如mysql: CURRENT_TIMESTAMP() oracle：sysdate
     */
    private String currentDate;

    /**
     * 列转义使用的引号， oracle:", mysql:`
     */
    private String quotation;

    /**
     * 根据查询字段属性的，返回 拼接后的 like后面的字符串 mysql： "CONCAT('%', #{" + attr + "}, '%')"
     * oracle:"'%'||#{" + attr + "}||'%'"
     */
    private Function<String, String> likeJoint;

    /**
     * 初始化一些数据库 dialect
     */
    public static final DataBaseDialectInfo mysql = new DataBaseDialectInfo();

    public static final DataBaseDialectInfo oracle = new DataBaseDialectInfo();

    public static final DataBaseDialectInfo gauss = new DataBaseDialectInfo();

    static {
        mysql.setCurrentDate("CURRENT_TIMESTAMP()");
        mysql.setQuotation("`");
        mysql.setLikeJoint(attr -> "CONCAT('%', #{" + attr + "}, '%')");

        oracle.setCurrentDate("sysdate");
        oracle.setQuotation("\"");
        // oracle.setLikeJoint(attr -> "'%'||#{" + attr + "}||'%'");
        oracle.setLikeJoint(attr -> "concat(concat('%', #{" + attr + "}), '%')");

        gauss.setCurrentDate("current_timestamp");
        gauss.setQuotation("\"");
        gauss.setLikeJoint(attr -> "CONCAT('%', #{" + attr + "}, '%')");

    }

    public DataBaseDialectInfo() {
        super();
    }

    public DataBaseDialectInfo(String currentDate, String quotation, Function<String, String> likeJoint) {
        super();
        this.currentDate = currentDate;
        this.quotation = quotation;
        this.likeJoint = likeJoint;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public Function<String, String> getLikeJoint() {
        return likeJoint;
    }

    public void setLikeJoint(Function<String, String> likeJoint) {
        this.likeJoint = likeJoint;
    }
}
