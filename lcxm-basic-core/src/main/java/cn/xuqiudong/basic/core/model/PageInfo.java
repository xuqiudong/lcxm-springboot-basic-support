package cn.xuqiudong.basic.core.model;

import cn.xuqiudong.basic.core.lookup.Lookup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 列表数据用于分页
 *
 * @author VIC
 */
@Data
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 数据
     */
    private List<T> datas;

    /**
     * 总数据量
     */
    /**
     * 总数据量
     */
    private long total;

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

    public PageInfo(long total, List<T> datas) {
        this.total = total;
        this.datas = datas;
    }

    public PageInfo(long total, List<T> datas, Lookup lookup) {
        this.total = total;
        this.datas = datas;
        this.lookup = lookup;
        if (lookup != null) {
            this.page = lookup.getPage();
            this.size = lookup.getSize();
            this.curSize = datas.size();
            this.pages =(int) (total % (long) size == 0 ? total / (long) size : total / (long) size + 1);
        }
    }

    public static <T> PageInfo<T> instance(int total, List<T> datas, Lookup lookup) {
        return new PageInfo<T>(total, datas, lookup);
    }




    /**
     * hasMore是否有更多的数据 page * size < total
     */
    public boolean getHasMore() {
        return page * size < total;
    }


}
