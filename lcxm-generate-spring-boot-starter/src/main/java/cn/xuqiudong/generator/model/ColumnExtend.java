package cn.xuqiudong.generator.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 列的扩展 
 * 根据标准的备注生成
 * 备注标准如：说明,类型,下拉框键值对,其他 (英文逗号分隔)
 * @author VIC
 *
 */
public class ColumnExtend {
    /* ****************************************************************/
    /*
     * 说明;类型;下拉框键值对;是否查询条件;
     *  一、列表页是否展示
     *
     * 1.普通的input 默认都必填 检验规则 ：数字、整数
     * 2.图标
     * 3.图片  列表页不展示
     * 4.富文本  列表页不展示
     * 5.下拉框   下拉框的键值
     * 6.日期
     */
    /* ****************************************************************/

    //说明
    private String comment;
    //类型 1-普通文本,2-图标,3-图片,4-富文本 5-下拉框,6-日期
    private Integer type = 1;
    //列表页是否展示
    private Boolean show = false;
    //是否是时间
    private Boolean isTime = false;

    //是否是图片
    private Boolean isPic = false;

    //是否是查询条件
    private Boolean where = false;

    /**
     * 比较方式0:=; 1:like;
     *
     */
    private Integer condition = 0;


    //下拉框数据
    private Map<String, String> select = new HashMap<>();

    public ColumnExtend() {

    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getShow() {
        return show;
    }

    public ColumnExtend setShow(Boolean show) {
        this.show = show == null ? Boolean.FALSE : show;
        return this;
    }

    public Map<String, String> getSelect() {
        return select;
    }

    public ColumnExtend setSelect(Map<String, String> select) {
        this.select = select;
        return this;
    }

    public Boolean getIsTime() {
        return isTime;
    }

    public ColumnExtend setIsTime(Boolean isTime) {
        this.isTime = isTime;
        return this;
    }

    public Boolean getIsPic() {
        return isPic;
    }

    public ColumnExtend setIsPic(Boolean isPic) {
        this.isPic = isPic;
        return this;
    }

    public Boolean getWhere() {
        return where;
    }

    public ColumnExtend setWhere(Boolean where) {
        this.where = where == null ? Boolean.FALSE : where;
        return this;
    }


    public Integer getCondition() {
        return condition;
    }


    public ColumnExtend setCondition(Integer condition) {
        this.condition = condition == null ? Integer.valueOf(0) : condition;
        return this;
    }


}
