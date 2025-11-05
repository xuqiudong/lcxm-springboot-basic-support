package cn.xuqiudong.generator.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;

/**
 * 列的属性
 */
@SuppressFBWarnings(value = "SE_BAD_FIELD")
public class ColumnEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列名类型
     */
    private String dataType;
    /**
     * 列名备注 完整的备注
     */
    private String comments;
    /**
     * 约束类型:<br />
     * MYSQL: PRI主键约束 UNI唯一约束 MUL可以重复 <br />
     * ORACLE: P-Primary Key U R.....
     * gaussDb: p主键约束  u 唯一约束  f 外键约束  c 检查约束
     */
    private String columnKey;
    /**
     * auto_increment
     */
    private String extra;

    /* ************************************************************ */

    /**
     * 当前列是否是主键
     */
    public boolean isPrimaryKey() {
        return "PRI".equalsIgnoreCase(this.getColumnKey()) || "P".equalsIgnoreCase(this.getColumnKey());
    }

    // 备注 根据comments截取描述部分
    private String comment;

    // 属性名称(第一个字母大写)，如：user_name => UserName 主要get和set时候使用
    private String attrName;
    // 属性名称(第一个字母小写)，如：user_name => userName
    private String attrname;
    // 属性类型
    private String attrType;

    /**
     * 表实体中是否忽略此字段(因在基类中已定义)
     */
    private boolean entityIgnore = false;

    // 列表的扩展
    private ColumnExtend extend = new ColumnExtend();

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAttrname() {
        return attrname;
    }

    public void setAttrname(String attrname) {
        this.attrname = attrname;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public ColumnExtend getExtend() {
        return extend;
    }

    public void setExtend(ColumnExtend extend) {
        this.extend = extend;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isEntityIgnore() {
        return entityIgnore;
    }

    public void setEntityIgnore(boolean entityIgnore) {
        this.entityIgnore = entityIgnore;
    }

}
