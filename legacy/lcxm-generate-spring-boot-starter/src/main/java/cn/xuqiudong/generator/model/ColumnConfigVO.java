package cn.xuqiudong.generator.model;

/**
 * 说明 :  表的配置
 * @author  Vic.xu
 * @since  2019年12月8日 下午9:03:20
 */
public class ColumnConfigVO {
    /**
     * 列名
     */
    private String columnName;

    /**
     * 比较方式0:=; 1:like;
     *
     */
    private Integer condition = 0;

    /**
     * 是否列表展示
     */
    private Boolean show;


    public ColumnConfigVO() {
        super();
    }

    public ColumnConfigVO(String columnName, Integer condition, Boolean show) {
        super();
        this.columnName = columnName;
        this.condition = condition;
        this.show = show;
    }

    public String getColumnName() {
        return columnName;
    }

    public Integer getCondition() {
        return condition;
    }

    public Boolean getShow() {
        return show;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }


}
