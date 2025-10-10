package cn.xuqiudong.common.util.poi.excel.export.export.model;

import cn.xuqiudong.common.util.poi.excel.enmus.ExcelType;

import java.util.List;

/**
 * 描述: excel导出的相关参数
 * @author Vic.xu
 * @since 2021-12-17 11:22
 */
public class ExportParam<T> {

    /**
     * Excel 导出版本  默认2003因为更快一点
     */
    private ExcelType type = ExcelType.HSSF;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * data数据 class类型
     */
    private Class<T> dataClass;

    /**
     * 需要导出的数据
     */
    private List<T> datas;


    public ExcelType getType() {
        return type;
    }

    public void setType(ExcelType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }
}
