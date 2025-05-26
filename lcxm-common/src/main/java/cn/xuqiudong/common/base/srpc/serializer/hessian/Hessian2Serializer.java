package cn.xuqiudong.common.base.srpc.serializer.hessian;


import cn.xuqiudong.common.base.exception.CommonException;
import cn.xuqiudong.common.base.srpc.model.XqdResponse;
import cn.xuqiudong.common.base.srpc.serializer.XqdSerializer;
import cn.xuqiudong.common.base.srpc.serializer.hessian.extention.LocalDateSerializerFactory;
import cn.xuqiudong.common.base.srpc.serializer.hessian.extention.LocalDateTimeSerializerFactory;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;

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
        serializerFactory.addFactory(new LocalDateTimeSerializerFactory());
        serializerFactory.addFactory(new LocalDateSerializerFactory());
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

    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);
        byte[] serialize = new Hessian2Serializer().serialize(localDate);
        LocalDate deserialize = new Hessian2Serializer().deserialize(serialize, LocalDate.class);
        System.out.println(deserialize);

        TestModel model = new TestModel();
        model.setId("1");
        model.setName("22");
        model.setDate(localDate.now());
        XqdResponse localDateXqdResponse2 = XqdResponse.success(model);
        byte[] serialize2 = new Hessian2Serializer().serialize(localDateXqdResponse2);
        XqdResponse deserialize2 = new Hessian2Serializer().deserialize(serialize2, XqdResponse.class);
        TestModel resultData2 = (TestModel) deserialize2.getResultData();
        System.out.println(resultData2);
        LocalDate date = resultData2.getDate();
        System.out.println(date);

        /*
        XqdResponse localDateXqdResponse = XqdResponse.success(localDate);
        byte[] serialize1 = new Hessian2Serializer().serialize(localDateXqdResponse);

        XqdResponse<LocalDate> deserialize1 = new Hessian2Serializer().deserialize(serialize1, XqdResponse.class);
        LocalDate resultData = (LocalDate) deserialize1.getResultData();
        System.out.println(resultData);
        */
    }
}

class TestModel implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;

    String name;

    LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
