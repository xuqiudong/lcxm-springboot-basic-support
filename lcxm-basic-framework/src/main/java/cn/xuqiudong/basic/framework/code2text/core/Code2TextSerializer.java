package cn.xuqiudong.basic.framework.code2text.core;

import cn.xuqiudong.basic.framework.code2text.annotation.Code2Text;
import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 描述:
 * Code2Text 核心字段级序列化器
 * *
 * * 工作模式：
 * * - 在 createContextual 阶段读取字段注解并构建字段上下文
 * * - 在 serialize 阶段仅使用已绑定的上下文进行输出
 * *
 * * 说明：
 * * Jackson 在 serialize 阶段无法再获取 BeanProperty，
 * * 因此所有与字段元数据相关的解析必须在 createContextual 阶段完成。
 *
 * @author Vic.xu
 * @see Code2Text
 * @since 2026-01-09 17:40
 */
public class Code2TextSerializer extends JsonSerializer<Object>
        implements ContextualSerializer {

    private final JsonSerializer<Object> originSerializer;
    private final Code2TextResolver resolver;
    private final Code2Text code2Text;

    private final String textFieldName;
    private final boolean fallbackToRaw;

    public Code2TextSerializer() {
        this(null, null, null, null, true);
    }

    private Code2TextSerializer(JsonSerializer<Object> originSerializer,
                                Code2TextResolver resolver,
                                Code2Text code2Text,
                                String textFieldName,
                                boolean fallbackToRaw) {
        this.originSerializer = originSerializer;
        this.resolver = resolver;
        this.code2Text = code2Text;
        this.textFieldName = textFieldName;
        this.fallbackToRaw = fallbackToRaw;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        if (originSerializer == null || resolver == null || code2Text == null) {
            serializers.defaultSerializeValue(value, gen);
            return;
        }

        originSerializer.serialize(value, gen, serializers);

        if (!(gen instanceof JsonGeneratorDelegate)
                && gen.getOutputContext().getCurrentName() == null) {
            return;
        }
        // 获取文本字段名
        String text = resolver.codeToText(value);
        if (text == null && fallbackToRaw && value != null) {
            text = String.valueOf(value);
        }

        if (text != null) {
            gen.writeFieldName(textFieldName);
            gen.writeString(text);
        }
    }

    /**
     * 创建字段级序列化器
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
            throws JsonMappingException {

        if (property == null) {
            return prov.findValueSerializer(Object.class);
        }
        // 具体的子注解
//        Annotation bizAnno = findBizAnnotation(property);
        // 读取字段上的 @Code2Text 注解
        Code2Text code2TextAnno = property.getAnnotation(Code2Text.class);
        if (code2TextAnno == null) {
            return prov.findValueSerializer(property.getType(), property);
        }

        JsonSerializer<Object> origin =
                prov.findValueSerializer(property.getType(), property);

        // 获取文本字段名
        String textFieldName = resolveTextFieldName(property, code2TextAnno, code2TextAnno);

        // 获取解析器
        Code2TextResolver resolver =
                Code2TextResolverRegistry.get(code2TextAnno.type());

        if (resolver == null) {
            throw new IllegalStateException(
                    "No Code2TextResolver found for type: " + code2TextAnno.type().getName());
        }

        boolean fallbackToRaw = code2TextAnno.fallbackToRaw();

        return new Code2TextSerializer(origin, resolver, code2TextAnno, textFieldName, fallbackToRaw);
    }

    //  获得具体的被Code2Text标注的子注解
    private Annotation findBizAnnotation(BeanProperty property) {

        AnnotatedMember member = property.getMember();
        if (member == null) {
            return null;
        }

        for (Annotation anno : member.getAllAnnotations().annotations()) {
            if (anno.annotationType().isAnnotationPresent(Code2Text.class)) {
                return anno;
            }
        }
        return null;
    }

    /**
     * 获取文本字段名:
     * 1. 如果定义了textKey，则使用textKey
     * 2. 否则使用原字段名 + 自定义后缀
     */
    private String resolveTextFieldName(BeanProperty property,
                                        Annotation bizAnno,
                                        Code2Text code2Text) {

        String textKey = getAnnoValue(bizAnno, "textKey");
        if (StringUtils.isNotBlank(textKey)) {
            return textKey;
        }

        String suffix = getAnnoValue(bizAnno, "suffix");
        if (StringUtils.isBlank(suffix)) {
            suffix = code2Text.suffix();
        }

        return property.getName() + suffix;
    }

    private String getAnnoValue(Annotation anno, String method) {
        try {
            Method m = anno.annotationType().getMethod(method);
            Object v = m.invoke(anno);
            return v == null ? null : String.valueOf(v);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
