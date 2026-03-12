package cn.xuqiudong.basic.core.util.poi.excel.export.model;

import cn.xuqiudong.basic.core.util.poi.excel.enmus.ExcelType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 是否只读
     */
    private boolean readOnly;

    /**
     * sheet名称
     */
    private String sheetName;


    /**
     * 导出的列的描述列表
     */
    private List<ExportFieldModel> exportFieldModelList;

    /**
     * 是否包含二级表头
     */
    private boolean hasMultiHead;
    /**
     * 二级表头 列表
     */
    private List<MultiHeader> multiHeaderList;

    /**
     * TODO 参数是否不合法
     */
    public boolean invalid() {
        return false;
    }


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


    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public boolean isHasMultiHead() {
        return hasMultiHead;
    }

    public void setHasMultiHead(boolean hasMultiHead) {
        this.hasMultiHead = hasMultiHead;
    }

    public List<MultiHeader> getMultiHeaderList() {
        return multiHeaderList;
    }

    public void setMultiHeaderList(List<MultiHeader> multiHeaderList) {
        this.multiHeaderList = multiHeaderList;
    }

    public List<ExportFieldModel> getExportFieldModelList() {
        return exportFieldModelList;
    }

    public void setExportFieldModelList(List<ExportFieldModel> exportFieldModelList) {
        this.exportFieldModelList = exportFieldModelList;
        this.nultipleHeadProcess();
    }

    /**
     * 是否包含二级表头
     */
    private void nultipleHeadProcess() {
        //根据ptitle 分组
        Map<String, List<ExportFieldModel>> map = exportFieldModelList.stream().collect(Collectors.groupingBy(e -> e.getPtitle() == null ? "" : e.getPtitle()));
        //只有一组  说明没有二级表头
        if (map.size() == 1) {
            return;
        }
        this.hasMultiHead = true;
        map.forEach((ptitle, models) -> {
            if (StringUtils.isBlank(ptitle)) {
                for (ExportFieldModel model : models) {
                    MultiHeader header = new MultiHeader(model.getTitle(), model.getSort());
                    multiHeaderList.add(header);
                }
            } else {
                MultiHeader header = new MultiHeader(ptitle, models);
            }
        });
        Collections.sort(multiHeaderList);

        //同时需要修改 二级表头的顺序：从一级表头中提取出来
        Map<String, ExportFieldModel> exportFieldModelMap = exportFieldModelList.stream().collect(Collectors.toMap(ExportFieldModel::getTitle, e -> e));
        List<ExportFieldModel> newExportFieldModelList = new ArrayList<>();
        multiHeaderList.forEach(heads -> {
            heads.getTitleList().forEach(title -> {
                newExportFieldModelList.add(exportFieldModelMap.get(title));
            });
        });
        this.exportFieldModelList = newExportFieldModelList;

    }
}


