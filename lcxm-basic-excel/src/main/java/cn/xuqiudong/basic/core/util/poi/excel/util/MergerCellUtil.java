package cn.xuqiudong.basic.core.util.poi.excel.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述: 合并单元格
 * @author Vic.xu
 * @since 2021-12-20 11:11
 */
public class MergerCellUtil {

    private static Logger logger = LoggerFactory.getLogger(MergerCellUtil.class);

    public static void addMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        try {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        } catch (Exception e) {
            logger.debug("发生了一次合并单元格错误,{},{},{},{}", new Integer[]{
                    firstRow, lastRow, firstCol, lastCol
            });
            logger.debug(e.getMessage(), e);
        }
    }
}
