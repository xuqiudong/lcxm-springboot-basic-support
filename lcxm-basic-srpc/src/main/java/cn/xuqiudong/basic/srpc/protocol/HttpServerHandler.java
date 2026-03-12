package cn.xuqiudong.basic.srpc.protocol;

import cn.xuqiudong.basic.srpc.model.XqdRequest;
import cn.xuqiudong.basic.srpc.model.XqdResponse;
import cn.xuqiudong.basic.srpc.provider.XqdServiceHolder;
import cn.xuqiudong.basic.srpc.serializer.XqdSerializer;
import cn.xuqiudong.basic.srpc.serializer.hessian.Hessian2Serializer;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * 描述:
 *    服务端处理器
 * @author Vic.xu
 * @since 2024-07-01 9:51
 */
public class HttpServerHandler {

    private static XqdSerializer serializer = new Hessian2Serializer();

    public static void  handle(ServletRequest req, ServletResponse res){
        XqdResponse response;
        try {
            byte[] bytes = HttpProtocol.toByteArray(req.getInputStream());
            XqdRequest request = serializer.deserialize(bytes, XqdRequest.class);
            // 处理数据
            Object data = XqdServiceHolder.invokeMethod(request);
            response = XqdResponse.success(data);
        } catch (Exception e) {
            response = XqdResponse.error(e.getMessage());
        }
        try {
            byte[] bytes = serializer.serialize(response);
            IOUtils.write(bytes, res.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
