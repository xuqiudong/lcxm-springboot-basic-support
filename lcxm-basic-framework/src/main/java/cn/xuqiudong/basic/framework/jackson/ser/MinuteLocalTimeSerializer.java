package cn.xuqiudong.basic.framework.jackson.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Description:
 * LocalTime  序列化为  HH:mm
 * <p>
 * 使用方式 在LocalTime字段上加
 * {@code @JsonSerialize(using = MinuteLocalTimeSerializer.class)}
 *
 * @author Vic.xu
 * @since 2026-09-15 11:30
 */
public class MinuteLocalTimeSerializer extends JsonSerializer<LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void serialize(LocalTime value,
                          JsonGenerator gen,
                          SerializerProvider serializers)
            throws IOException {
        gen.writeString(value.format(FORMATTER));
    }
}
