package cn.xuqiudong.basic.core.model;

import cn.xuqiudong.basic.core.lookup.Lookup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;


/**
 * 列表数据用于分页
 * @author VIC
 *
 */
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 2112010303028938095L;
    /**
     * 数据
     */
    private List<T> datas;

    /**
     * 总数据量
     */
    private int total;

    /**
     * 每页的数据数量
     */
    private int size = 10;

    /**
     * 当前页码
     */
    private int page = 1;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 当前页数据数量
     */
    private int curSize;


    /**
     * 本页面的查询条件
     */
    @JsonIgnore
    private Lookup lookup = new Lookup();

    public PageInfo() {
    }

    public PageInfo(int total, List<T> datas) {
        this.total = total;
        this.datas = datas;
    }

    public PageInfo(int total, List<T> datas, Lookup lookup) {
        this.total = total;
        this.datas = datas;
        this.lookup = lookup;
        if (lookup != null) {
            this.page = lookup.getPage();
            this.size = lookup.getSize();
            this.curSize = datas.size();
            this.pages = total % size == 0 ? total / size : total / size + 1;
        }

    }


    public PageInfo(Page<T> page, List<T> datas, Lookup lookup) {
        if (page != null) {
            this.total = (int) page.getTotal();
            this.size = page.getPageSize();
            if (lookup != null && lookup.getPage() > 0) {
                this.page = lookup.getPage();
            }
            this.page = page.getPageNum();
            this.pages = page.getPages();
            this.curSize = page.size();
        }
        this.datas = datas;
        this.lookup = lookup;
    }


    public PageInfo(List<T> datas, Lookup lookup) {
        if (datas instanceof Page) {
            Page<T> page = (Page<T>) datas;
            this.datas = page.getResult();
            this.total = (int) page.getTotal();
            this.page = page.getPageNum();
            this.size = page.getPageSize();
            this.pages = page.getPages();
            this.curSize = page.size();
        }
        this.lookup = lookup;
    }

    public static <T> PageInfo<T> instance(Page<T> page, List<T> datas, Lookup lookup) {
        return new PageInfo<T>(page, datas, lookup);
    }

    public static <T> PageInfo<T> instance(Page<T> page, List<T> datas) {
        return new PageInfo<T>(page, datas, null);
    }

    public static <T> PageInfo<T> instance(List<T> datas, Lookup lookup) {
        return new PageInfo<T>(datas, lookup);
    }

    public static <T> PageInfo<T> instance(List<T> datas) {
        return new PageInfo<T>(datas, null);
    }

    public static <T> PageInfo<T> instance(int total, List<T> datas, Lookup lookup) {
        return new PageInfo<T>(total, datas, lookup);
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Lookup getLookup() {
        return lookup;
    }

    public void setLookup(Lookup lookup) {
        this.lookup = lookup;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurSize() {
        return curSize;
    }

    public void setCurSize(int curSize) {
        this.curSize = curSize;
    }

    /**
     * hasMore是否有更多的数据 page * size < total
     */
    public boolean getHasMore() {
        return page * size < total;
    }


}
