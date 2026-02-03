package cn.xuqiudong.basic.generator.model.meta;

import cn.xuqiudong.basic.generator.model.DataType;
import cn.xuqiudong.basic.generator.registry.DataTypeMappingRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 描述:
 * 表的列元信息
 *
 * @author Vic.xu
 * @since 2025-09-11 10:01
 */
@Getter
@Setter
@ToString
public class ColumnMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 列名
     */
    private String columnName;
    /**
     * 列数据类型
     */
    private String dataType;

    /**
     * 列名备注 完整的备注
     */
    private String comments;
    /**
     * 约束类型:<br />
     * MYSQL: PRI主键约束 UNI唯一约束 MUL可以重复 <br />
     * ORACLE: P-Primary Key U R.....
     * gaussDb: p主键约束  u 唯一约束  f 外键约束  c 检查约束
     */
    private String columnKey;
    /**
     * auto_increment
     */
    private String extra;

    /**
     * 当前列是否是主键
     */
    public boolean isPk() {
        return "PRI".equalsIgnoreCase(this.getColumnKey()) || "P".equalsIgnoreCase(this.getColumnKey());
    }

    /**
     * 获取列数据类型映射
     */
    public DataType getDataTypeMapping() {
        return DataTypeMappingRegistry.get(this.getDataType());
    }

    /**
     * 是否是自增主键: mysql auto_increment
     */
    public boolean isAutoIncrement() {
        return "auto_increment".equalsIgnoreCase(this.getExtra());
    }

    // 列表的扩展
//    private ColumnExtend extend = new ColumnExtend();


}
