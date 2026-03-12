package cn.xuqiudong.basic.core.util.poi.excel.enmus;

/**
 * 描述: Excel 类型
 * @author Vic.xu
 * @since 2021-12-20 9:19
 */
public enum ExcelType {
    /**2003*/
    HSSF("Excel2003"),
    /**2007*/
    XSSF("Excel2003");

    private String name;

    ExcelType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
