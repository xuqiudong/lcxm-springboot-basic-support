package cn.xuqiudong.common.base.jackson.desr;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 描述:
 *    反序列化枚举的时候支持空字符串
 * @author Vic.xu
 * @since 2026-01-08 15:48
 */
public class NullableEnumDeserializer extends StdDeserializer<Enum<?>> implements ContextualDeserializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NullableEnumDeserializer.class);

    private Class<Enum<?>> enumClass;

    public NullableEnumDeserializer() {
        super(Enum.class);
    }

    public NullableEnumDeserializer(Class<Enum<?>> enumClass) {
        super(enumClass);
        this.enumClass = enumClass;
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        String text = p.getText();
        if (text != null && text.isEmpty()) {
            return null;
        }
        for (Enum<?> value : enumClass.getEnumConstants()) {
            if (value.name().equalsIgnoreCase(text)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + text + " for enum class: " + enumClass.getName());
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType type = property.getType();
        return new NullableEnumDeserializer((Class<Enum<?>>) type.getRawClass());
    }
}
