package cn.xuqiudong.common.base.handler.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;

/**
 * 描述: 写入前端的json字段做xss处理
 * @author Vic.xu
 * @since 2022-03-21 11:23
 */
public class XssStringJsonSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            String encodedValue = StringEscapeUtils.escapeHtml4(value);
            gen.writeString(encodedValue);
        }
    }
}


