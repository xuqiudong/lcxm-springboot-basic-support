package cn.xuqiudong.basic.framework.jackson;

import cn.xuqiudong.basic.framework.jackson.desr.NullableEnumDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.TimeZone;

/**
 * 描述:
 * jackson自动配置类
 *
 * @author Vic.xu
 * @since 2026-01-07 14:42
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnProperty(prefix = "lcxm.framework.jackson", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LcxmJacksonAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LcxmJacksonAutoConfiguration.class);

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * JDK8 日期时间格式化
     */


    @Bean("dateTimeFormatCustomizer")
    @ConditionalOnMissingBean(name = "dateTimeFormatCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer dateTimeFormatCustomizer() {
        LOGGER.info("注册JDK8日期时间格式化");
        // 年月日时分秒
        var dtf = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withResolverStyle(ResolverStyle.SMART);
        //年月日
        var df = DateTimeFormatter.ofPattern(DATE_FORMAT).withResolverStyle(ResolverStyle.SMART);

        // 输出固定 HH:mm:ss
        var timeSerializerFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT).withResolverStyle(ResolverStyle.SMART);

        // 输入兼容 HH:mm 和 HH:mm:ss
        var timeDeserializerFormatter = new DateTimeFormatterBuilder()
                .appendPattern("HH:mm")
                .optionalStart()
                .appendPattern(":ss")
                .optionalEnd()
                .toFormatter()
                .withResolverStyle(ResolverStyle.SMART);

        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dtf))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dtf))
                .serializerByType(LocalDate.class, new LocalDateSerializer(df))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(df))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(timeSerializerFormatter))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(timeDeserializerFormatter))
                // 禁用转时间戳
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                //  兼容Date 等
                .timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }


    /**
     * 枚举反序列化可以传入空字符串
     */
    @Bean("nullableEnumDeserializer")
    @ConditionalOnMissingBean(name = "nullableEnumDeserializer")
    public Jackson2ObjectMapperBuilderCustomizer nullableEnumDeserializer() {
        LOGGER.info("注册枚举反序列化可以传入空字符串");
        return builder -> builder.deserializerByType(Enum.class, new NullableEnumDeserializer());
    }
}
