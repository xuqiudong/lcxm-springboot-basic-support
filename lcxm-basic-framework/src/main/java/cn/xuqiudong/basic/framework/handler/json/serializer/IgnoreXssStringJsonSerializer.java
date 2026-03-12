package cn.xuqiudong.basic.framework.handler.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 描述: 写入前端的字段忽略xss处理 {@link XssStringJsonSerializer}
 * 可以通过如下代码定义全局解析器
 <code>
 @Bean
 @Primary public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
 ObjectMapper objectMapper = builder.createXmlMapper(false).build();
 //注册xss解析器
 SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
 xssModule.addSerializer(String.class, new XssStringJsonSerializer());
 objectMapper.registerModule(xssModule);
 return objectMapper;
 }
 </code>
  * @author Vic.xu
 * @since 2022-03-21 11:23
 */
@SuppressWarnings("PMD")
public class IgnoreXssStringJsonSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(value);
        }
    }
}

