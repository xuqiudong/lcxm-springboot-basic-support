package cn.xuqiudong.common.util.poi.excel.export.export;

import cn.xuqiudong.common.util.poi.excel.enmus.ExcelType;
import cn.xuqiudong.common.util.poi.excel.export.export.model.ExportParam;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * 描述: 导出 Excel工具类  入口
 * @author Vic.xu
 * @since 2021-12-20 9:27
 */
public class ExcelExportUtil {

    private static final int HSSF_LIMIT = 100000;

    private static Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);


    public static <T> void exprotExcel(ExportParam<T> param, Collection<T> datas) {
        Workbook workbook = getWorkbook(param.getType(), datas.size());
        createSheet(workbook, param, datas);

    }

    private static <T> void createSheet(Workbook workbook, ExportParam<T> param, Collection<T> datas) {
        if (logger.isDebugEnabled()) {
            logger.debug("start export excel, data model is {}, type is {}, data size {}",
                    new Object[]{param.getDataClass().getSimpleName(), param.getType().getName(), datas.size()});
        }
    }


    private static Workbook getWorkbook(ExcelType type, int size) {
        if (type == ExcelType.HSSF || size < HSSF_LIMIT) {
            return new HSSFWorkbook();
        }
        return new SXSSFWorkbook();
    }
}
