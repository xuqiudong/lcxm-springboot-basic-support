package cn.xuqiudong.basic.srpc.serializer.json;

import cn.xuqiudong.basic.srpc.serializer.XqdSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 描述: 通过json方式序列化数据
 * @author Vic.xu
 * @date 2022-02-21 11:07
 */
public class JsonSerializer implements XqdSerializer {

    ObjectMapper mapper = new ObjectMapper();
    @Override
    public <T> byte[] serialize(T obj) throws JsonProcessingException {
        return mapper.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        return mapper.readValue(data, clazz);

    }
    @Override
    public boolean selfDescribed() {
        return false;
    }
}
