package com.kjlink.cloud.mybatis.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分页查询基础类
 *
 * @author kj
 * @since 2023-02-07
 */
@Schema(description = "分页查询基础类")
public class PageQuery {
    @Schema(description = "当前页，从1开始", example = "1")
    @SqlCondition(ignore = true)
    protected int current;
    @Schema(description = "页大小，为0时不分页", example = "10")
    @SqlCondition(ignore = true)
    protected int size;
    @Schema(description = "排序，格式为列名称1,asc,列名称2,desc...", example = "id,asc")
    @SqlCondition(ignore = true)
    protected String orders;
    @Schema(hidden = true, description = "设置一个默认排序规则，避免前端未指定排序时异常；如果前端已经指定则优先用前端的")
    private String defaultOrders = "id,asc";

    /**
     * 默认第1页，页大小为10
     */
    public PageQuery() {
        this.current = 1;
        this.size = 10;
    }

    /**
     * 指定页大小和当前页码
     *
     * @param current 当前页码，从1开始
     * @param size    页大小，-1不分页
     */
    public PageQuery(int current, int size) {
        this.current = current;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    /**
     * @param orderItems
     * @deprecated 不需要使用mbp的对象
     */
    @Deprecated
    public void setOrders(List<OrderItem> orderItems) {
        if (CollUtil.isNotEmpty(orderItems)) {
            StringJoiner sj = new StringJoiner(",");
            for (OrderItem orderItem : orderItems) {
                sj.add(orderItem.getColumn());
                sj.add(orderItem.isAsc() ? "asc" : "desc");
            }
            this.orders = sj.toString();
        } else {
            this.orders = null;
        }
    }

    /**
     * 设置orderBy
     *
     * @param orderBy
     */
    public void setOrders(OrderBy orderBy) {
        this.orders = toOrdersString(orderBy);
    }

    private static String toOrdersString(OrderBy orderBy) {
        Map<String, Boolean> columnOrders = orderBy.getColumnOrders();
        StringJoiner sj = new StringJoiner(",");
        for (Map.Entry<String, Boolean> entry : columnOrders.entrySet()) {
            sj.add(entry.getKey());
            sj.add(Boolean.TRUE.equals(entry.getValue()) ? "asc" : "desc");
        }
        return sj.toString();
    }

    /**
     * 设置默认的排序规则，当前端未指定时，按默认规则排序
     */
    public void setDefaultOrders(OrderBy orderBy) {
        this.defaultOrders = toOrdersString(orderBy);
    }

    /**
     * 转成MBP自带的Page对象，主要用于手写分页sql时，MBP只认自己的分页类
     *
     * @param <T>
     * @return
     */
    public <T> Page<T> toPage() {
        Page<T> page;
        if (size <= 0) {
            //size<0时不分页
            page = new Page<>(1, -1, false);
        } else {
            page = new Page<>(current, size, true);
        }

        //解析排序字符串
        String orderBy = StrUtil.blankToDefault(orders, defaultOrders);
        if (StrUtil.isNotBlank(orderBy)) {
            List<String> terms = StrUtil.split(orderBy, ",");
            if (terms.size() % 2 != 0) {
                throw new IllegalArgumentException(StrUtil.format("排序字段格式错误：{}", orderBy));
            }

            List<OrderItem> orderItems = new ArrayList<>(terms.size() / 2);
            for (int i = 0; i < terms.size(); i += 2) {
                String safeColumn = ColumnUtil.safeColumn(terms.get(i));
                String order = terms.get(i + 1);
                if ("asc".equals(order)) {
                    orderItems.add(OrderItem.asc(safeColumn));
                } else if ("desc".equals(order)) {
                    orderItems.add(OrderItem.desc(safeColumn));
                } else {
                    throw new IllegalArgumentException(StrUtil.format("排序字段格式错误：{}", orderBy));
                }
            }
            page.setOrders(orderItems);
        }
        return page;
    }
}
