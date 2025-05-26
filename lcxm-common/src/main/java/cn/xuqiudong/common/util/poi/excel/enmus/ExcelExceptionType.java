package cn.xuqiudong.common.util.poi.excel.enmus;

/**
 * 描述: 一些异常
 * @author Vic.xu
 * @since 2021-12-20 9:47
 */
public enum ExcelExceptionType {
    /**Excel导出参数错误*/
    PARAMETER_ERROR("Excel导出参数错误");

    private final String msg;

    ExcelExceptionType(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
