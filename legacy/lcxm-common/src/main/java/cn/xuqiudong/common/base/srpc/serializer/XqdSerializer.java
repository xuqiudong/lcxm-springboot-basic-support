package cn.xuqiudong.common.base.srpc.serializer;


import java.io.IOException;

/**
 * 描述: 序列化方式的接口
 * @author Vic.xu
 * @since 2024-06-25
 */
public interface XqdSerializer {

    /**
     * 序列化对象
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化对象
     * @param data
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] data,Class<T> clazz) throws IOException;

    /**
     * 当前序列化/反序列化方式是否自描述
     * @return
     */
    boolean selfDescribed();
}
