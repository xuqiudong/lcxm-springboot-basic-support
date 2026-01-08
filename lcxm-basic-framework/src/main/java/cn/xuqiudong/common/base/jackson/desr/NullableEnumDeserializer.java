package cn.xuqiudong.common.base.jackson.desr;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * 描述:
 *    反序列化枚举的时候支持空字符串
 * @author Vic.xu
 * @since 2026-01-08 15:48
 */
public class NullableEnumDeserializer extends StdDeserializer<Enum<?>> {

    public NullableEnumDeserializer() {
        super(Enum.class);
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        String text = p.getText();
        if (text != null && !text.isEmpty()) {
            return null;
        }

        // 目标枚举类型
        Class<Enum<?>> enumClass = (Class<Enum<?>>) ctx.getContextualType().getRawClass();

        for (Enum<?> value : enumClass.getEnumConstants()) {
            if (value.name().equalsIgnoreCase(text)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + text + " for enum class: " + enumClass.getName());
    }
}
