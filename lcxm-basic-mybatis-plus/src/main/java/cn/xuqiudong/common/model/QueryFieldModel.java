package cn.xuqiudong.common.model;

import cn.hutool.core.bean.PropDesc;
import cn.xuqiudong.common.annotation.QueryCondition;
import cn.xuqiudong.common.enums.QueryOperation;
import lombok.Data;

/**
 * 描述: 查询字段模型
 *
 * @author Vic.xu
 * @see cn.xuqiudong.common.annotation.QueryCondition
 * @since 2025-10-29 15:52
 */
@Data
public class QueryFieldModel {

    /**
     * 查询操作类型
     */
    private QueryOperation operation;

    /**
     * 当前字段对应的sql字段
     * 默认驼峰转下划线
     * 如果指定多个字段， 则 表示 or 关系
     */
    private String[] columns;

    /**
     * 如果值为null 是否生成is null
     * 为 null 默认不查询
     */
    private boolean allowNullQuery;

    /**
     * 是否对字段进行trim操作
     */
    private boolean trim;

    /**
     * in 语句 时候，  分割字符串 的分隔符
     */
    private String delimiter;

    /**
     * 字段类型
     */
    private Class<?> fieldType;

    /**
     * 字段描述
     */
    private PropDesc propDesc;

    public QueryFieldModel(QueryCondition queryCondition, Class<?> fieldType, PropDesc propDesc) {
        this.operation = queryCondition.operation();
        this.columns = queryCondition.columns();
        this.allowNullQuery = queryCondition.allowNullQuery();
        this.trim = queryCondition.trim();
        this.delimiter = queryCondition.delimiter();
        this.fieldType = fieldType;
        this.propDesc = propDesc;

        if (columns == null || columns.length == 0) {
            columns = new String[]{propDesc.getFieldName()};
        }
    }

    public Object getValue(Object query) {
        return propDesc.getValue(query);
    }


}
