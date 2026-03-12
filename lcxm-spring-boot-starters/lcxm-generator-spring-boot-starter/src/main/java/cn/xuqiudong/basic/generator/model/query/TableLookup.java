package cn.xuqiudong.basic.generator.model.query;

import cn.xuqiudong.basic.core.lookup.Lookup;
import lombok.Getter;
import lombok.Setter;

/**
 * 描述:
 * table  查询参数
 *
 * @author Vic.xu
 * @since 2025-09-12 10:56
 */
@Getter
@Setter
public class TableLookup extends Lookup {

    private String tableName;

    private String comments;

    private String engine;
}
