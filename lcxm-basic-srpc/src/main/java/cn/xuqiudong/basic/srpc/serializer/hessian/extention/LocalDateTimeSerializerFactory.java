package cn.xuqiudong.basic.srpc.serializer.hessian.extention;

import cn.xuqiudong.basic.srpc.serializer.hessian.extention.jsr310.JavaTimeSerializerFactory;
import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * deprecated:  不支持日期类型为泛型, 请使用JavaTimeSerializerFactory
 * @see JavaTimeSerializerFactory
 */
@Deprecated
public class LocalDateTimeSerializerFactory extends AbstractSerializerFactory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (cl == LocalDateTime.class)
            return new LocalDateTimeSerializer();
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (cl == LocalDateTime.class)
            return new LocalDateTimeDeserializer();
        return null;
    }

    private static class LocalDateTimeSerializer extends AbstractSerializer {
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            LocalDateTime dateTime = (LocalDateTime) obj;
            out.writeString(dateTime.format(FORMATTER));
        }

    }

    private static class LocalDateTimeDeserializer extends AbstractDeserializer {
        public Object readObject(AbstractHessianInput in) throws IOException {
            String str = in.readString();
            return LocalDateTime.parse(str, FORMATTER);
        }

        @Override
        public Class<?> getType() {
            return LocalDateTime.class;
        }
    }
}