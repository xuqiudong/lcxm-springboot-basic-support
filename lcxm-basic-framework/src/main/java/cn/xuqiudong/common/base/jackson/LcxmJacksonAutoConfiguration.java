package cn.xuqiudong.common.base.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
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

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * JDK8 日期时间格式化
     */
    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer dateTimeFormatCustomizer() {
        var dtf = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withResolverStyle(ResolverStyle.SMART);
        var df = DateTimeFormatter.ofPattern(DATE_FORMAT).withResolverStyle(ResolverStyle.SMART);
        var tf = DateTimeFormatter.ofPattern(TIME_FORMAT).withResolverStyle(ResolverStyle.SMART);

        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dtf))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dtf))
                .serializerByType(LocalDate.class, new LocalDateSerializer(df))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(df))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(tf))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(tf))
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
