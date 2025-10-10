package cn.xuqiudong.common.base.srpc.serializer.hessian;


import cn.xuqiudong.common.base.exception.CommonException;
import cn.xuqiudong.common.base.srpc.serializer.XqdSerializer;
import cn.xuqiudong.common.base.srpc.serializer.hessian.extention.jsr310.JavaTimeSerializerFactory;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 描述: 基于Hessian2的的序列化方式
 *
 * @author Vic.xu
 * @since 2024-06-25
 */
public class Hessian2Serializer implements XqdSerializer {

    public static final SerializerFactory serializerFactory = SerializerFactory.createDefault();

    static {

        //新增一些数据类型的支持
//        serializerFactory.addFactory(new LocalDateTimeSerializerFactory());
//        serializerFactory.addFactory(new LocalDateSerializerFactory());
        serializerFactory.addFactory(JavaTimeSerializerFactory.getInstance());
    }


    @Override
    public <T> byte[] serialize(T obj) {

        Hessian2Output hessianOutput = null;
        // try with  resource
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            hessianOutput = new Hessian2Output(os);
            hessianOutput.setSerializerFactory(serializerFactory);
            hessianOutput.writeObject(obj);
            hessianOutput.flush();
            byte[] serialize = os.toByteArray();
            return serialize;
        } catch (Exception e) {
            throw new CommonException("Hessian2 序列化失败", e);
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        Hessian2Input hessianInput = null;

        //try with  resource
        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            hessianInput = new Hessian2Input(is);
            hessianInput.setSerializerFactory(serializerFactory);
            return (T) hessianInput.readObject(clazz);
        } catch (Exception e) {
            throw new CommonException("Hessian2 反序列化失败", e);
        } finally {
            if (hessianInput != null) {
                try {
                    hessianInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean selfDescribed() {
        return true;
    }

}
