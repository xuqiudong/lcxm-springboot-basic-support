package cn.xuqiudong.basic.generator.model;

import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.basic.generator.model.meta.ColumnMeta;
import cn.xuqiudong.basic.generator.registry.DataTypeMappingRegistry;
import cn.xuqiudong.basic.generator.registry.KeyWordsHandlerRegistry;
import cn.xuqiudong.basic.generator.util.JavaTypeInferUtil;
import cn.xuqiudong.basic.generator.util.NameConvertUtils;
import cn.xuqiudong.basic.generator.util.TypeConvertUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述:
 * 表的字段信息
 *
 * @author Vic.xu
 * @since 2025-09-11 20:01
 */
@Data
public class FieldInfo implements Cloneable {

    /**
     * 列原始名
     */
    private String columnName;

    /**
     * 安全的列名称:  关键字会带转移符
     */
    private String columnNameSafe;

    /**
     * 属性名称(第一个字母大写)，如：user_name => UserName 主要get和set时候使用
     */
    private String methodFieldName;
    /**
     * 属性名称(第一个字母小写)，如：user_name => userName
     */
    private String fieldName;

    /**
     * 属性描述
     */
    private String comments;

    /**
     * 属性类型: resultMap 中可以使用
     */
    private DataType dataType;
    /**
     * 属性类型: java类型,  一般情况下为 dataType 中的 javaType， 可能额外的加一些判断
     *
     */
    private Class<?> javaType;

    /**
     * 表实体中是否忽略此字段(因在基类中已定义)
     */
    private boolean entityIgnore = false;

    /**
     * 字段上的注解
     */
    private Set<String> annotations = new HashSet<>();

    /**
     * 是否主键
     */
    private boolean pk;

    /**
     * 字段元信息
     */
    @Setter(AccessLevel.NONE)
    private ColumnMeta meta;

    /**
     * 是否大文本
     */
    public boolean isLob() {
        return dataType.isLob();
    }

    /**
     * 数据库字段对应的java 数据类型, 如  LocalDate
     */
    public String getDataTypeName() {
        return dataType.getJavaType().getSimpleName();
    }

    /**
     * 获取ts类型
     */
    public String getTsType() {
        return TypeConvertUtil.toTsType(dataType);
    }

    /**
     * 获取ts 默认值
     */
    public String getTsDefault() {
        return TypeConvertUtil.getTsDefault(dataType);
    }

    /**
     * 设置字段元信息: 属性名称  字段类型
     */
    public void setMeta(DatabaseType databaseType, ColumnMeta meta) {
        this.meta = meta;
        this.columnName = meta.getColumnName();
        this.columnNameSafe = KeyWordsHandlerRegistry.formatColumn(databaseType, this.columnName);
        this.comments = meta.getComments();
        if (StringUtils.isBlank(comments)) {
            this.comments = meta.getColumnName();
        }
        // 列名转换成Java
        String className = NameConvertUtils.columnToJava(meta.getColumnName());
        // 属性名称(第一个字母大写)  Nickname, 用于生成get/set   getNickname
        this.methodFieldName = className;
        // 属性名称(第一个字母小写)，如：user_name => userName
        this.fieldName = StringUtils.uncapitalize(className);
        //列的数据类型
        DataType originDataType = DataTypeMappingRegistry.get(meta.getDataType());
        this.dataType = JavaTypeInferUtil.inferJavaType(originDataType,  comments);
        // 是否是主键
        this.pk = meta.isPk();
    }

    /**
     * 添加注解
     */
    public void addAnnotation(String annotation) {
        if (annotation == null) {
            annotations = new HashSet<>();
        }
        annotations.add(annotation);
    }

    /**
     * 克隆 原始数据， 此时 注解尚未处理 所以 只需要new个空集合即可
     */
    @Override
    public FieldInfo clone() {
        try {
            FieldInfo clone = (FieldInfo) super.clone();
            this.annotations = new HashSet<>();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
