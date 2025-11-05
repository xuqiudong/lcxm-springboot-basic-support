package cn.xuqiudong.common.util.poi.excel.export.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 二级表头
 * @author Vic.xu
 * @since 2021-12-20 11:57
 */
public class MultiHeader implements Comparable<MultiHeader> {

    /**所属父表头*/
    private String ptitle;

    /**父表头的长度*/
    private int plength;

    /**
     * 当前是否是单表头: 即不存在父表头
     */
    private boolean singleHeader;

    /**不存在父表头时候的 表头名*/
    private String singleTitle;

    private int sort;

    /**子表头列表*/
    private List<String> titleList = new ArrayList<>();

    public MultiHeader() {

    }

    public MultiHeader(String singleTitle, int sort) {
        this.singleHeader = true;
        this.sort = sort;
        this.plength = 1;
        this.singleTitle = singleTitle;
        this.titleList.add(singleTitle);
        this.ptitle = singleTitle;
    }

    public MultiHeader(String ptitle, List<ExportFieldModel> fields) {
        this.ptitle = ptitle;
        this.sort = fields.get(0).getSort();
        this.plength = fields.size();
        this.singleHeader = false;
        fields.forEach(f -> {
            titleList.add(f.getTitle());
        });
    }

    public String getPtitle() {
        return ptitle;
    }

    public void setPtitle(String ptitle) {
        this.ptitle = ptitle;
    }

    public int getPlength() {
        return plength;
    }

    public void setPlength(int plength) {
        this.plength = plength;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }

    public boolean isSingleHeader() {
        return singleHeader;
    }

    public void setSingleHeader(boolean singleHeader) {
        this.singleHeader = singleHeader;
    }

    public String getSingleTitle() {
        return singleTitle;
    }

    public void setSingleTitle(String singleTitle) {
        this.singleTitle = singleTitle;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    @SuppressFBWarnings(value = "EQ_COMPARETO_USE_OBJECT_EQUALS")
    public int compareTo(MultiHeader o) {
        return Integer.compare(this.sort, o.getSort());
    }

}
