package cn.xuqiudong.common.base.srpc.serializer.hessian.extention.jsr310;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;

import java.io.IOException;

/**
 * 描述:
 * JDK8 时间类型反序列化器
 * <p>
 * 反序列化流程:
 * Hessian 读到 type=java.time.LocalDate → 找到 LocalDateDeserializer
 * 数据结构是 map → 调用 readMap()
 * 在 readMap() 里拿到 "_value" -> "2025-08-25" → 返回 LocalDate.parse("2025-08-25")
 * </p>
 *
 * @author Vic.xu
 * @since 2025-08-25 13:47
 */
public class JavaTimeDeserializer<T> extends AbstractDeserializer {

    private static final String KEY = "_value";

    private final JavaTimeHandler<T> handler;

    public JavaTimeDeserializer(JavaTimeHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public Object readMap(AbstractHessianInput in) throws IOException {
        String dateStr = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (KEY.equals(key)) {
                dateStr = in.readString();
            } else {
                in.readObject();
            }
        }
        in.readMapEnd();
        return dateStr == null ? null : handler.parse(dateStr);
    }

    @Override
    public Class<?> getType() {
        return handler.type;
    }
}
