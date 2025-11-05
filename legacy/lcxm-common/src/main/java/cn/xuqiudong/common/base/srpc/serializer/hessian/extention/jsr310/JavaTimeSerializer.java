package cn.xuqiudong.common.base.srpc.serializer.hessian.extention.jsr310;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

import java.io.IOException;

/**
 * 描述:
 * JDK8 时间类型序列化器
 * <p>
 * 序列化流程:
 * LocalDate(2025-08-25) →
 * Hessian 流 = {type:"java.time.LocalDate", "_value":"2025-08-25"}
 * </p>
 *
 * @author Vic.xu
 * @since 2025-08-25 13:45
 */
public class JavaTimeSerializer<T> extends AbstractSerializer {
    private static final String KEY = "_value";
    private final JavaTimeHandler<T> handler;

    public JavaTimeSerializer(JavaTimeHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        String dateStr = handler.format(obj);
        out.writeMapBegin(handler.type.getName());
        out.writeString(KEY);
        out.writeString(dateStr);
        out.writeMapEnd();
    }
}
