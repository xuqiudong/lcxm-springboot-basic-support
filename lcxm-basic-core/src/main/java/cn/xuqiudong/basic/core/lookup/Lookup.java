package cn.xuqiudong.basic.core.lookup;

import cn.xuqiudong.basic.core.exception.CommonException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;


/**
 * 查询参数 默认带分页信息
 *
 * @author VIC.xu
 *
 */
public class Lookup implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE = 1;

    public static final int DEFAULT_SIZE = 10;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected int page = DEFAULT_PAGE;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected int size = DEFAULT_SIZE;

    /**
     * 排序字段
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String sortColumn;

    /**
     * 排序方式 asc  desc
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected String sortOrder;


    public Lookup() {

    }

    public Lookup(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page <= 0 ? DEFAULT_PAGE : page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size <= 0 ? DEFAULT_SIZE : size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 当前数据索引.
     * @return 数据索引
     */
    @JsonIgnore
    public int getIndex() {
        return (getPage() - 1) * getSize();
    }


    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortOrder() {
        isValidSortOrder(sortOrder);
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        isValidSortOrder(sortOrder);
        this.sortOrder = sortOrder;
    }

    /**
     * 是否合法的排序方式： asc  desc
     */
    protected void isValidSortOrder(String sortOrder) {
        if (StringUtils.isNotBlank(sortOrder)) {
            boolean match = Arrays.stream(LookupSortOrder.values()).anyMatch(v -> v.name().equalsIgnoreCase(sortOrder));
            if (!match) {
                throw new CommonException("Illegal sorting column");
            }
        }

    }

    public enum LookupSortOrder {
        asc, desc;
    }

}
