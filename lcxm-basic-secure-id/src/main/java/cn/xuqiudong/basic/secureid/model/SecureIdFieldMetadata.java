package cn.xuqiudong.basic.secureid.model;

import java.lang.reflect.Field;

/**
 * 响应对象字段处理元数据。
 * <p>
 * 构建一次后缓存，后续同 Class 对象直接使用，减少反射字段扫描和注解判断成本。
 * @author Vic.xu
 */
public class SecureIdFieldMetadata {

    /**
     * 已设置可访问的字段。
     */
    private final Field field;

    /**
     * true 表示该字段按 ID 字段加密；false 表示该字段继续递归展开。
     */
    private final boolean encryptField;

    public SecureIdFieldMetadata(Field field, boolean encryptField) {
        this.field = field;
        this.encryptField = encryptField;
    }

    public Field field() {
        return field;
    }

    public boolean encryptField() {
        return encryptField;
    }
}
