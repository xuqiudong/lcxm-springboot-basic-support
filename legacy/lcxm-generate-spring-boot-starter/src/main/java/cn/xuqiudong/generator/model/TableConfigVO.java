package cn.xuqiudong.generator.model;

import java.util.List;

/**
 * 说明 :  前端手选的表的一些基本配置
 * @author  Vic.xu
 * @since  2019年12月8日 下午9:02:42
 */
public class TableConfigVO {
    /**
     * 当前选中的表名
     */
    private String tableName;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 模块名
     */
    private String moduleName;


    /**
     * 列表展示的字段配置
     */
    private List<ColumnConfigVO> columns;


    public TableConfigVO(String tableName, String packageName, String moduleName) {
        super();
        this.tableName = tableName;
        this.packageName = packageName;
        this.moduleName = moduleName;
    }

    public TableConfigVO() {
        super();
    }

    public String getTableName() {
        return tableName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public List<ColumnConfigVO> getColumns() {
        return columns;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setColumns(List<ColumnConfigVO> columns) {
        this.columns = columns;
    }


}
