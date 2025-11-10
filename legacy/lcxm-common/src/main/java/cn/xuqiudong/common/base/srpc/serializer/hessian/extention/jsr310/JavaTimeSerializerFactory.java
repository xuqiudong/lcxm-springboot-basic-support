package cn.xuqiudong.common.base.srpc.serializer.hessian.extention.jsr310;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.Serializer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 描述:
 * JDK8 时间类型 Hessian 序列化工厂
 * * 支持 LocalDate、LocalTime、LocalDateTime，可扩展注册其他类型
 *
 * @author Vic.xu
 * @since 2025-08-25 13:48
 */
public class JavaTimeSerializerFactory extends AbstractSerializerFactory {

    private static final JavaTimeSerializerFactory instance = new JavaTimeSerializerFactory();

    public static JavaTimeSerializerFactory getInstance() {
        return instance;
    }

    /**
     * JDK8 时间类型的解析/格式化逻辑  处理器
     */
    private final Map<Class<?>, JavaTimeHandler<?>> handlers = new HashMap<>();
    /**
     * JDK8 时间类型的序列化逻辑  处理器 缓存
     */
    private final Map<Class<?>, Serializer> serializerCache = new HashMap<>();
    /**
     * JDK8 时间类型的反序列化逻辑  处理器 缓存
     */
    private final Map<Class<?>, Deserializer> deserializerCache = new HashMap<>();

    private JavaTimeSerializerFactory() {
        registerDefaults();
    }

    /**
     * 注册默认的时间类型: LocalDate、LocalDateTime、LocalTime
     */
    private void registerDefaults() {
        // LocalDate
        register(LocalDate.class, DateTimeFormatter.ISO_LOCAL_DATE,
                str -> LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE),
                date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE));

        // LocalDateTime
        register(LocalDateTime.class, DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                str -> LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                dt -> dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // LocalTime
        register(LocalTime.class, DateTimeFormatter.ISO_LOCAL_TIME,
                str -> LocalTime.parse(str, DateTimeFormatter.ISO_LOCAL_TIME),
                t -> t.format(DateTimeFormatter.ISO_LOCAL_TIME));

        // OffsetDateTime
        register(OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                str -> OffsetDateTime.parse(str, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                dt -> dt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        // ZonedDateTime
        register(ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME,
                str -> ZonedDateTime.parse(str, DateTimeFormatter.ISO_ZONED_DATE_TIME),
                dt -> dt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        // YearMonth
        register(YearMonth.class, DateTimeFormatter.ofPattern("yyyy-MM"),
                str -> YearMonth.parse(str, DateTimeFormatter.ofPattern("yyyy-MM")),
                ym -> ym.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        // Instant
        register(
                Instant.class,
                DateTimeFormatter.ISO_INSTANT,
                str -> Instant.from(DateTimeFormatter.ISO_INSTANT.parse(str)),
                instant -> DateTimeFormatter.ISO_INSTANT.format(instant)
        );
    }

    /**
     * 注册时间类型
     */
    public <T> void register(Class<T> clazz,
                             DateTimeFormatter formatter,
                             Function<String, T> parser,
                             Function<T, String> formatterFunc) {
        handlers.put(clazz, new JavaTimeHandler<>(clazz, formatter, parser, formatterFunc));
    }

    @Override
    public Serializer getSerializer(Class cl) {
        return serializerCache.computeIfAbsent(cl, c -> {
            JavaTimeHandler<?> handler = handlers.get(c);
            return handler != null ? new JavaTimeSerializer<>(handler) : null;
        });
    }

    @Override
    public Deserializer getDeserializer(Class cl) {
        return deserializerCache.computeIfAbsent(cl, c -> {
            JavaTimeHandler<?> handler = handlers.get(c);
            return handler != null ? new JavaTimeDeserializer<>(handler) : null;
        });
    }
}
