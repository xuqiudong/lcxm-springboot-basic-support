package cn.xuqiudong.common.util.poi.excel.export;

import cn.xuqiudong.common.util.CommonUtils;
import cn.xuqiudong.common.util.poi.excel.enmus.ExcelExceptionType;
import cn.xuqiudong.common.util.poi.excel.enmus.ExcelType;
import cn.xuqiudong.common.util.poi.excel.exception.ExcelExportException;
import cn.xuqiudong.common.util.poi.excel.export.annotation.ExportField;
import cn.xuqiudong.common.util.poi.excel.export.model.ExportFieldModel;
import cn.xuqiudong.common.util.poi.excel.export.model.ExportParam;
import cn.xuqiudong.common.util.poi.excel.export.model.MultiHeader;
import cn.xuqiudong.common.util.poi.excel.util.MergerCellUtil;
import cn.xuqiudong.common.util.reflect.ReflectionUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * 描述: 导出 Excel工具类  入口
 * @author Vic.xu
 * @since 2021-12-20 9:27
 */
public class ExcelExportUtil {

    private static final int HSSF_LIMIT = 100000;

    private static Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);

    private static final String GETTER_PREFIX = "get";


    public static <T> void exprotExcel(ExportParam<T> param, Collection<T> datas) {
        if (param.invalid()) {
            throw new ExcelExportException(ExcelExceptionType.PARAMETER_ERROR);
        }
        Workbook workbook = getWorkbook(param.getType(), datas.size());
        createSheet(workbook, param, datas);
    }

    private static <T> void createSheet(Workbook workbook, ExportParam<T> param, Collection<T> datas) {
        if (logger.isDebugEnabled()) {
            logger.debug("start export excel, data model is {}, type is {}, data size {}",
                    new Object[]{param.getDataClass().getSimpleName(), param.getType().getName(), datas.size()});
        }
        List<ExportFieldModel> exportFieldModelList = buildExportFields(param);
        //设置表头列表 并计算是否包含二级表头相关信息
        param.setExportFieldModelList(exportFieldModelList);

        Sheet sheet = null;
        try {
            if (StringUtils.isBlank(param.getSheetName())) {
                sheet = workbook.createSheet();
            } else {
                sheet = workbook.createSheet(param.getSheetName());
            }

        } catch (Exception e) {
            //指定名称出错则 不指定sheetName 再次创建一次
            workbook.createSheet(param.getSheetName());
        }

        if (param.isReadOnly()) {
            sheet.protectSheet(CommonUtils.randomUuid());
        }

        insertDataToSheet(workbook, sheet, param, datas);

    }


    /**
     *   向sheet插入数据
     * @param workbook
     * @param sheet
     * @param param
     * @param datas
     * @param <T>
     */
    private static <T> void insertDataToSheet(Workbook workbook, Sheet sheet, ExportParam<T> param, Collection<T> datas) {
        int rows = createTitle(workbook, sheet, param);
        rows = createHead(workbook, sheet, param, rows);
        exportDatas(workbook, sheet, param, rows);
    }

    private static <T> void exportDatas(Workbook workbook, Sheet sheet, ExportParam<T> param, int rows) {
        List<T> datas = param.getDatas();
        List<ExportFieldModel> exportFieldModelList = param.getExportFieldModelList();


        for (T data : datas) {
            Row dataRow = sheet.createRow(rows++);
            int col = 0;
            for (ExportFieldModel em : exportFieldModelList) {
                createCell(dataRow, col++, data, em);
            }
        }
    }

    @SuppressFBWarnings
    private static <T> void createCell(Row dataRow, int col, T data, ExportFieldModel em) {
        Cell cell = dataRow.createCell(col);
        //TODO style

    }

    /**
     * 创建 抬头
     * @param workbook
     * @param sheet
     * @param param
     * @param rows
     * @param <T>
     */
    private static <T> int createHead(Workbook workbook, Sheet sheet, ExportParam<T> param, int rows) {
        if (param.isHasMultiHead()) {
            rows = createPhead(workbook, sheet, param, rows);
        }
        rows = createSubHead(workbook, sheet, param, rows);

        return rows;

    }

    /**
     * 创建行
     * @param workbook
     * @param sheet
     * @param param
     * @param rows
     * @param <T>
     */
    private static <T> int createSubHead(Workbook workbook, Sheet sheet, ExportParam<T> param, int rows) {
        Row row = sheet.createRow(rows++);
        row.setHeightInPoints(16);
        //TODO  get style
        //非多表头的时候 直接按列输出
        if (!param.isHasMultiHead()) {
            List<ExportFieldModel> exportFieldModelList = param.getExportFieldModelList();
            for (int i = 0; i < exportFieldModelList.size(); i++) {
                ExportFieldModel filed = exportFieldModelList.get(i);
                Cell cell = row.createCell(i);
                //cell.setCellStyle(..);
                cell.setCellValue(filed.getTitle());
                // 是否需要再去计算一下列的宽度呢？

                sheet.autoSizeColumn(i);
            }
        } else {
            //如果是多表头的时候，非组合表头无需再创建了
            List<MultiHeader> multiHeaderList = param.getMultiHeaderList();
            //col index
            int j = 0;
            for (int i = 0; i < multiHeaderList.size(); i++, j++) {
                MultiHeader header = multiHeaderList.get(0);
                if (!header.isSingleHeader()) {
                    for (String title : header.getTitleList()) {
                        Cell cell = row.createCell(j++);
                        //cell.setCellStyle(..);
                        cell.setCellValue(title);
                        sheet.autoSizeColumn(i);
                    }
                }
            }

        }
        return rows;
    }

    private static <T> int createPhead(Workbook workbook, Sheet sheet, ExportParam<T> param, int rows) {
        int pheadRows = rows++;
        Row phead = sheet.createRow(pheadRows);

        phead.setHeightInPoints(16);
        int j = 0;
        List<MultiHeader> multiHeaderList = param.getMultiHeaderList();
        for (int i = 0; i < multiHeaderList.size(); i++) {
            MultiHeader multiHeader = multiHeaderList.get(i);
            int firstRow = pheadRows;
            int lastRow;
            int firstCol = j;
            int lastCol;
            if (multiHeader.isSingleHeader()) {
                lastRow = firstRow + 1;
                lastCol = firstCol;
            } else {
                lastRow = firstRow;
                lastCol = firstCol + multiHeader.getPlength() - 1;
            }
            MergerCellUtil.addMergedRegion(sheet, firstRow, lastRow, firstCol, lastCol);
            Cell cell = phead.createCell(j);
            cell.setCellValue(multiHeader.getPtitle());
            //TODO set style
            sheet.autoSizeColumn(j);
            j += multiHeader.getPlength();
        }
        return rows;
    }

    /**
     * 创建标题
     * @param workbook
     * @param sheet
     * @param param
     * @param <T>
     */
    private static <T> int createTitle(Workbook workbook, Sheet sheet, ExportParam<T> param) {
        int rows = 0;
        Row titleRow = sheet.createRow(rows++);
        titleRow.setHeightInPoints(30);
        //样式TODO
        //合并标题单元格
        MergerCellUtil.addMergedRegion(sheet, 0, 0, 0, param.getExportFieldModelList().size() - 1);
        return rows;
    }


    private static Workbook getWorkbook(ExcelType type, int size) {
        if (type == ExcelType.HSSF || size < HSSF_LIMIT) {
            return new HSSFWorkbook();
        }
        return new SXSSFWorkbook();
    }


    /**
     * 查找实体对象中包含ExportField 注解的field 和get方法
     * @return
     */
    public static <T> List<ExportFieldModel> buildExportFields(ExportParam<T> param) {
        List<ExportFieldModel> fieldList = new ArrayList<>();
        Class<T> dataClass = param.getDataClass();
        for (Class cls = dataClass; cls != Object.class; cls = dataClass.getSuperclass()) {
            //遍历所有的字段
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(ExportField.class)) {
                    continue;
                }
                ExportField annotation = field.getAnnotation(ExportField.class);
                ExportFieldModel exportFieldModel = new ExportFieldModel(annotation, field);
                ReflectionUtils.makeAccessible(field);
                fieldList.add(exportFieldModel);
            }
            //遍历所有的get方法
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(ExportField.class) || !method.getName().startsWith(GETTER_PREFIX)) {
                    continue;
                }
                ExportField annotation = method.getAnnotation(ExportField.class);
                ExportFieldModel exportFieldModel = new ExportFieldModel(annotation, method);
                fieldList.add(exportFieldModel);
            }
        }

        Collections.sort(fieldList);
        return fieldList;
    }
}
