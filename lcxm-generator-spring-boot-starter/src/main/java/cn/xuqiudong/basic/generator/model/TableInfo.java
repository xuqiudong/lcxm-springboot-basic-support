package cn.xuqiudong.basic.generator.model;

import cn.xuqiudong.basic.generator.model.meta.TableMeta;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 表信息 转化后的表信息
 *
 * @author Vic.xu
 * @since 2025-09-11 20:00
 */
@Data
public class TableInfo {

    /**
     * 表的名称
     */
    private String tableName;

    /**
     * 表转化为java的name: 比如 sys_user -> User
     */
    private String className;

    /**
     * 表的备注
     */
    private String comments;

    /**
     * 表字段信息:   用于xml映射 ,将会去除指定要要排除的字段
     */
    List<FieldInfo> xmlFields;

    /**
     * 表的主键
     */
    private FieldInfo pk;

    /**
     * 表的list结果集映射字段信息:   用于xml映射 ,去掉主键 , 去掉lob字段, 并且把父类的字段放在前面
     */
    private List<FieldInfo> listFields;

    /**
     * lob 字段
     */
    private List<FieldInfo> lobFields;


    /**
     * add 表字段信息:   用于xml映射 ,将会去除指定要要排除的字段
     */
    public void addXmlField(FieldInfo field) {
        if (xmlFields == null) {
            xmlFields = new ArrayList<>();
        }
        xmlFields.add(field);
    }

    public void setTableMeta(TableMeta tableMeta) {
        this.tableName = tableMeta.getTableName();
        this.comments = tableMeta.getComments();
        if (StringUtils.isBlank(this.comments)) {
            this.comments = tableMeta.getTableName();
        }
    }

    /**
     * 初始化结果集映射字段:
     * 1. listFields 字段
     * 2. lobFields 字段
     */
    public void initResultMapFields() {
        List<FieldInfo> all = getXmlFields();
        if (all == null) {
            return;
        }
        // 1. 去掉主键
        List<FieldInfo> nonPk = all.stream()
                .filter(f -> !f.isPk())
                .toList();

        // 2. 拆分 lob 和非 lob
        List<FieldInfo> lobFields = nonPk.stream()
                .filter(FieldInfo::isLob)
                .collect(Collectors.toList());

        List<FieldInfo> otherFields = nonPk.stream()
                .filter(f -> !f.isLob())
                .collect(Collectors.toList());

        // 3. 其他字段排序：忽略的排前面(父类的字段)
        otherFields.sort(Comparator.comparing(FieldInfo::isEntityIgnore).reversed());

        listFields = otherFields;
        this.lobFields = lobFields;

    }

}
