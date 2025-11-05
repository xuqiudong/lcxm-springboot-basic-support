package cn.xuqiudong.generator.model;

import cn.xuqiudong.generator.autoconfigure.GeneratorProperties;
import cn.xuqiudong.generator.service.GeneratorService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 模板上下文 数据模型
 *
 * @author Vic.xu
 * @see GeneratorService #getTemplateData(TableEntity, TableConfigVO)
 * @since 2025-07-30 17:41
 */

public class TemplateContext {

    /**
     * 表数据
     */
    private TableEntity table;

    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表备注
     */
    private String comments;

    /**
     * 主键
     */
    private ColumnEntity pk;

    /**
     * 类名(第一个字母大写)，如：sys_user => SysUser
     */
    private String className;
    /**
     * 类名(第一个字母小写)，如：sys_user => sysUser 注入的时候使用
     */
    private String classname;

    private String pathName;

    /**
     * 表的列名(不包含主键)
     */
    private List<ColumnEntity> columns;

    /**
     * 列表页展示的列，即columns中extend.show = true
     */
    private List<ColumnEntity> listColumns;
    /**
     * 是否包含BigDecimal
     */
    private boolean hasBigDecimal;
    /**
     * 包
     */
    private String packageName;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 作者
     */
    private String author;

    /**
     * 数据库类型
     */
    private DataBaseDialectInfo dialect;


    /**
     * 是否支持lombok
     */
    private boolean lombok;

    /**
     * 是否 使用springdoc 2 注解
     */
    private boolean springdoc2;

    /**
     * 时间
     */
    private String datetime;

    /**
     * 模板上下文数据模型 转为map 类型
     */
    public Map<String, Object> getMapContext() {
        Map<String, Object> mapContext = new HashMap<>();
        // 允许访问私有字段
        ReflectionUtils.doWithFields(this.getClass(), field -> {
            field.setAccessible(true);
            // 值为原始对象
            mapContext.put(field.getName(), field.get(this));
        });
        // 包 同时支持 package 和 packageName
        mapContext.put("package", packageName);
        return mapContext;
    }


    public TemplateContext() {
        this.datetime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm");
    }

    public TemplateContext(TableEntity table, GeneratorProperties generatorProperties) {
        this();
        this.table = table;
        this.tableName = table.getTableName();
        this.comments = table.getComments();
        this.pk = table.getPk();
        this.className = table.getClassName();
        this.classname = table.getClassname();
        this.pathName = table.getClassname().toLowerCase();
        this.columns = table.getColumns();
        this.listColumns = table.getListColumns();
        this.hasBigDecimal = table.getHasBigDecimal();
        this.author = generatorProperties.getAuthor();
        this.dialect = generatorProperties.getTaDatabaseType().getDialect();
        this.lombok = generatorProperties.isLombok();
        this.springdoc2 = generatorProperties.isSpringdoc2();
    }

    public TableEntity getTable() {
        return table;
    }

    public void setTable(TableEntity table) {
        this.table = table;
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

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public List<ColumnEntity> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnEntity> columns) {
        this.columns = columns;
    }

    public List<ColumnEntity> getListColumns() {
        return listColumns;
    }

    public void setListColumns(List<ColumnEntity> listColumns) {
        this.listColumns = listColumns;
    }

    public boolean isHasBigDecimal() {
        return hasBigDecimal;
    }

    public void setHasBigDecimal(boolean hasBigDecimal) {
        this.hasBigDecimal = hasBigDecimal;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public DataBaseDialectInfo getDialect() {
        return dialect;
    }

    public void setDialect(DataBaseDialectInfo dialect) {
        this.dialect = dialect;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public boolean isLombok() {
        return lombok;
    }

    public void setLombok(boolean lombok) {
        this.lombok = lombok;
    }

    public boolean isSpringdoc2() {
        return springdoc2;
    }

    public void setSpringdoc2(boolean springdoc2) {
        this.springdoc2 = springdoc2;
    }
}
