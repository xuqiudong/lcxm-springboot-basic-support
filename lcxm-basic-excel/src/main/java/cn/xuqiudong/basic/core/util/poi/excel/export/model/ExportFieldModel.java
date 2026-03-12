package cn.xuqiudong.basic.core.util.poi.excel.export.model;

import cn.xuqiudong.basic.core.util.poi.excel.export.annotation.ExportField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 描述: 需要导出的字段的描述
 * @author Vic.xu
 * @since 2021-12-20 10:04
 */
public class ExportFieldModel implements Comparable<ExportFieldModel> {

    private String title;

    private String ptitle;

    private int sort;

    private ValueType valueType;

    private ExportField exportField;

    private Field filed;

    private Method method;

    public ExportFieldModel(ExportField exportField, Field filed) {
        this.valueType = ValueType.field;
        this.exportField = exportField;
        this.filed = filed;
    }

    public ExportFieldModel(ExportField exportField, Method method) {
        this.valueType = ValueType.method;
        this.exportField = exportField;
        this.method = method;
    }

    private void setOtherFiled(ExportField exportField) {
        //TODO
    }


    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public ExportField getExportField() {
        return exportField;
    }

    public void setExportField(ExportField exportField) {
        this.exportField = exportField;
    }

    public Field getFiled() {
        return filed;
    }

    public void setFiled(Field filed) {
        this.filed = filed;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPtitle() {
        return ptitle;
    }

    public void setPtitle(String ptitle) {
        this.ptitle = ptitle;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Object getValue(Object data) throws Exception {
        if (valueType == ValueType.field) {
            filed.get(data);
        }
        return method.invoke(data);
    }

    @Override
    @SuppressFBWarnings(value = "EQ_COMPARETO_USE_OBJECT_EQUALS")
    public int compareTo(ExportFieldModel o) {
        return Integer.compare(this.sort, o.getSort());
    }

    /**
     * 通过method还是field获导出字段的取值
     */
    static enum ValueType {
        /**method*/
        method,
        /**field*/
        field;
    }

}
