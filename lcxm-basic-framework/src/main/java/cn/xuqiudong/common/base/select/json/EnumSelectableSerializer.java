package cn.xuqiudong.common.base.select.json;

import cn.xuqiudong.common.base.select.EnumSelectable;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.SneakyThrows;

/**
 * 描述:
 * 通用枚举序列化器：自动为EnumSelectable枚举追加xxxText字段
 *
 * @author Vic.xu
 * @since 2025-11-24 15:35
 */
public class EnumSelectableSerializer extends JsonSerializer<EnumSelectable> implements ContextualSerializer {

    // 枚举字段名（如status → statusText）
    private String fieldName;


    public EnumSelectableSerializer() {
    }

    public EnumSelectableSerializer(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 上下文初始化：获取枚举字段的名称
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {

        if (property != null) {
            // 获取实体中枚举字段的名称
            return new EnumSelectableSerializer(property.getName());
        }
        return new EnumSelectableSerializer();
    }


    /**
     * 核心序列化逻辑
     */
    @Override
    @SneakyThrows // 简化IOException捕获
    public void serialize(EnumSelectable value, JsonGenerator gen, SerializerProvider serializers) {
        // 处理null值
        if (value == null) {
            gen.writeNull();
            return;
        }
        // 1. 序列化枚举的value（核心值）
        gen.writeString(value.getValue());

        // 2. 追加xxxText字段（text描述）
        if (fieldName != null) {
            gen.writeStringField(fieldName + "Text", value.getText());
        }
    }
}
