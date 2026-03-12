package cn.xuqiudong.basic.core.util.poi.excel.exception;

import cn.xuqiudong.basic.core.util.poi.excel.enmus.ExcelExceptionType;

/**
 * 描述: 导出异常
 * @author Vic.xu
 * @since 2021-12-20 9:46
 */
public class ExcelExportException extends RuntimeException {

    private ExcelExceptionType type;

    public ExcelExportException(ExcelExceptionType type) {
        super(type.getMsg());
        this.type = type;
    }

    public ExcelExportException(ExcelExceptionType type, Throwable cause) {
        super(type.getMsg(), cause);
    }

    public ExcelExportException(String message) {
        super(message);
    }

    public ExcelExportException(String message, ExcelExceptionType type) {
        super(message);
        this.type = type;
    }

    public ExcelExceptionType getType() {
        return type;
    }

    public void setType(ExcelExceptionType type) {
        this.type = type;
    }
}
