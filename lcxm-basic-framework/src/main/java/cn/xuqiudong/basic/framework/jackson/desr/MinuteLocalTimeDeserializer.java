package cn.xuqiudong.basic.framework.jackson.desr;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Description:
 * LocalTime  反序列化 接收 HH:mm 格式的字符串
 * <p>
 * 使用方式 在LocalTime字段上加
 * {@code @JsonDeserialize(using = MinuteLocalTimeDeserializer.class)}
 *
 * @author Vic.xu
 * @since 2026-09-15 11:34
 */
public class MinuteLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        return LocalTime.parse(p.getText(), FORMATTER);
    }
}
