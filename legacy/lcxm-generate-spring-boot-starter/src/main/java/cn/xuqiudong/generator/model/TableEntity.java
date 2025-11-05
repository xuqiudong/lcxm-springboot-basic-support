package cn.xuqiudong.generator.model;

import cn.xuqiudong.common.base.lookup.Lookup;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 表数据
 */
public class TableEntity extends Lookup {
    private static final long serialVersionUID = 1L;

    /**
     * 表的名称
     */
    private String tableName;
    /**
     * 表的备注
     */
    private String comments;

    /**
     * 表引擎
     */
    private String engine;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /* *************** ↑↑↑以上为需要查询出来的字段↑↑*********************************** */
    // 表的主键
    private ColumnEntity pk;
    // 表的列名(不包含主键)
    private List<ColumnEntity> columns;

    /**
     * 列表页面需要展示的列：从columns中提取的在列表页展示的数据：即ColumnEntity.extend.show = true
     */
    private List<ColumnEntity> listColumns;

    // 类名(第一个字母大写)，如：sys_user => SysUser
    private String className;
    // 类名(第一个字母小写)，如：sys_user => sysUser 注入的时候使用
    private String classname;

    // 是否包含图片
    private Boolean hasPic = false;

    // 是否包含日期
    private Boolean hasDate = false;

    // 是否包含Bigdecimal
    private Boolean hasBigDecimal = false;

    // 根据列是否是图片和日期 判断表中是否含有图片和日期
    public void findExtend(ColumnExtend extend) {
        this.hasDate = (this.hasDate || extend.getIsTime());
        this.hasPic = (this.hasPic || extend.getIsPic());
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ColumnEntity getPk() {
        return pk;
    }

    public void setPk(ColumnEntity pk) {
        this.pk = pk;
    }

    public List<ColumnEntity> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnEntity> columns) {
        this.columns = columns;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Boolean getHasPic() {
        return hasPic;
    }

    public void setHasPic(Boolean hasPic) {
        this.hasPic = hasPic;
    }

    public Boolean getHasDate() {
        return hasDate;
    }

    public void setHasDate(Boolean hasDate) {
        this.hasDate = hasDate;
    }

    public Boolean getHasBigDecimal() {
        return hasBigDecimal;
    }

    public void setHasBigDecimal(Boolean hasBigDecimal) {
        this.hasBigDecimal = hasBigDecimal;
    }

    public List<ColumnEntity> getListColumns() {
        if (listColumns == null && columns != null) {
            listColumns = new ArrayList<>();
            for (ColumnEntity column : columns) {
                if (Boolean.TRUE.equals(column.getExtend().getShow())) {
                    listColumns.add(column);
                }
            }
        } return listColumns;
    }

    public void setListColumns(List<ColumnEntity> listColumns) {
        this.listColumns = listColumns;
    }

    public void printLine() {
        String name = String.format("%-35s", tableName);
        System.err.println("表名:" + name + "\t备注:" + comments);
    }

}
