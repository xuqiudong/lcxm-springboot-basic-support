package cn.xuqiudong.common.query;

import cn.xuqiudong.common.util.ColumnUtils;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * MP 分页查询参数得基类
 *
 * @author Vic.xu
 * @since 2025-10-29 11:13
 */
@Data
@Schema(description = "分页查询参数基类")
public class PageQuery implements Serializable {


    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页", defaultValue = "1")
    protected int page = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    protected int size = 10;

    @Schema(description = "排序字段, 格式:  column1,desc;column2,asc;....", defaultValue = "id,desc")
    private String orders;

    @Schema(description = "默认排序字段, 默认:  id,desc", hidden = true)
    private String defaultOrders = "id,desc";

    private static final String ASC = "asc";

    private static final String DESC = "desc";

    /**
     * 设置排序字段
     */
    public void setOrderBy(OrderBy orderBy) {
        this.orders = orderBy.toOrderSql();
    }

    /**
     * 转换成MP分页参数, 并处理排序字段
     */
    public <T> Page<T> toPage() {
        Page<T> page;
        if (size <= 0) {
            // 查询所有
            page = new Page<>(1, -1, false);
        } else {
            page = new Page<>(this.page, this.size);
        }
        // 添加排序字段
        List<OrderItem> orderItems = parseParamOrders();
        page.addOrder(orderItems);
        return page;
    }

    /**
     * 解析排序字段
     */
    private List<OrderItem> parseParamOrders() {
        String ordersStr = this.orders;
        if (StringUtils.isBlank(ordersStr)) {
            ordersStr = defaultOrders;
        }
        List<OrderItem> orderItems = new ArrayList<>();

        if (StringUtils.isBlank(ordersStr)) {
            return orderItems;
        }

        String[] orderArr = ordersStr.split(";");
        for (String orders : orderArr) {
            String[] split = orders.split(",");
            Assert.isTrue(split.length == 2, "排序字段格式错误, 请使用: column1,desc;column2,asc;....");
            String column = split[0];
            column = ColumnUtils.safeColumn(column);
            String order = split[1];
            if (ASC.equalsIgnoreCase(order)) {
                orderItems.add(OrderItem.asc(column));
            } else if (DESC.equalsIgnoreCase(order)) {
                orderItems.add(OrderItem.desc(column));
            } else {
                throw new IllegalArgumentException(String.format("排序字段[%s]中的排序方式错误", ordersStr));
            }
        }
        return orderItems;
    }
}
