package cn.xuqiudong.common.base.srpc.serializer.hessian.extention;

import com.caucho.hessian.io.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializerFactory extends AbstractSerializerFactory {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        if (cl == LocalDate.class)
            return new LocalDateSerializer();
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        if (cl == LocalDate.class)
            return new LocalDateDeserializer();
        return null;
    }

    private static class LocalDateSerializer extends AbstractSerializer {
        public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
            LocalDate localDate = (LocalDate) obj;
            out.writeString(localDate.format(FORMATTER));
        }

    }

    private static class LocalDateDeserializer extends AbstractDeserializer {
        public Object readObject(AbstractHessianInput in) throws IOException {
            String str = in.readString();
            return LocalDate.parse(str, FORMATTER);
        }

        @Override
        public Class<?> getType() {
            return LocalDate.class;
        }
    }
}