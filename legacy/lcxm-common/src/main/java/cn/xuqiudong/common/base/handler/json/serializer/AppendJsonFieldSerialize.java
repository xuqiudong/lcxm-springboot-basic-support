package cn.xuqiudong.common.base.handler.json.serializer;

import cn.xuqiudong.common.base.handler.json.BaseAppendJsonHandler;
import cn.xuqiudong.common.base.handler.json.annotation.AppendJsonField;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Objects;

/**
 * 追加JSON字段的序列化方法
 *
 * {@link AppendJsonField}
 * use for field like: @AppendJsonField(key="idDesc",keyValyeDesc= {"1:ZHANGSAN", "2:LISI"})
 * @author VIC
 *
 */
@SuppressFBWarnings(value = "NP_LOAD_OF_KNOWN_NULL_VALUE")
public class AppendJsonFieldSerialize extends JsonSerializer<Object> implements ContextualSerializer {

    /**
     * 注解信息
     */
    private AppendJsonField appendJsonField;

    /**
     * 是否是String类型的字段，不是则是Integer
     */
    private boolean isString;

    public AppendJsonFieldSerialize() {
        super();
    }

    public AppendJsonFieldSerialize(AppendJsonField appendJsonField, boolean isString) {
        super();
        this.appendJsonField = appendJsonField;
        this.isString = isString;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty)
            throws JsonMappingException {
        if (beanProperty != null) {
            Class<?> clzType = beanProperty.getType().getRawClass();
            // 非 Integer类或者String类直接跳过
            boolean validType = Objects.equals(clzType, int.class) || Objects.equals(clzType, Integer.class)
                    || Objects.equals(clzType, String.class);
            if (validType) {
                AppendJsonField appendJsonField = beanProperty.getAnnotation(AppendJsonField.class);
                if (appendJsonField == null) {
                    appendJsonField = beanProperty.getContextAnnotation(AppendJsonField.class);
                }
                // /如果能得到注解，
                if (appendJsonField != null) {
                    return new AppendJsonFieldSerialize(appendJsonField, Objects.equals(clzType, String.class));
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (isString) {
            gen.writeString(value + "");
        } else {
            gen.writeNumber(Integer.parseInt(value + ""));

        }
        // 直接根据keyValueDesc 处理
        if (appendJsonField.appendType() == AppendJsonField.AppendType.direct.name()) {
            String[] keyValueDesc = appendJsonField.keyValueDesc();
            Assert.notEmpty(keyValueDesc, "当类型为AppendType.direct时候,描述字典不能为空");
            if (StringUtils.isNoneEmpty(appendJsonField.key())) {
                for (String kv : keyValueDesc) {
                    if (kv.split(":").length == 2 && String.valueOf(value).equals(kv.split(":")[0])) {
                        gen.writeStringField(appendJsonField.key(), kv.split(":")[1]);
                        break;
                    }
                }
            }
        } else {
            // 调用各自的处理类
            BaseAppendJsonHandler.write(appendJsonField, value, gen);
        }

    }

    @Override
    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        super.serializeWithType(value, gen, serializers, typeSer);
        // NOTE: need not really be string; just indicates "scalar of some kind"
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.VALUE_STRING));
        serialize(value, gen, serializers);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    public static void main(String[] args) throws JsonProcessingException {
        Test t = new Test();
        t.setType(2);
        t.setName("999");
        ObjectMapper mapper = new ObjectMapper();
        String writeValueAsString = mapper.writeValueAsString(t);
        System.out.println(writeValueAsString);

    }

}

class Test {

    @AppendJsonField(key = "typeName", keyValueDesc = {"1:ZHANGSAN", "2:LISI"})
    int type;

    String name;

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
