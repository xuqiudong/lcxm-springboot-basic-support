package cn.xuqiudong.basic.srpc.protocol;


import cn.xuqiudong.basic.core.exception.CommonException;
import cn.xuqiudong.basic.srpc.SrpcrAutoConfiguration;
import cn.xuqiudong.basic.srpc.model.Invoker;
import cn.xuqiudong.basic.srpc.model.SrpcInvocationMeta;
import cn.xuqiudong.basic.srpc.model.SrpcRequestUrl;
import cn.xuqiudong.basic.srpc.model.XqdRequest;
import cn.xuqiudong.basic.srpc.model.XqdResponse;
import cn.xuqiudong.basic.srpc.serializer.XqdSerializer;
import cn.xuqiudong.basic.srpc.serializer.hessian.Hessian2Serializer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 描述:基于http的通信
 *
 * @author Vic.xu
 * @date 2022-03-04 10:09
 */
public class HttpProtocol implements Protocol {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProtocol.class);

    private XqdSerializer serializer = new Hessian2Serializer();


    @Override
    public Object send(Invoker invoker) throws Exception {
        SrpcRequestUrl url = SrpcrAutoConfiguration.srpcRequestUrl;
        if (url == null) {
            throw new CommonException("Srpc provider address not configured");
        }

        XqdRequest xqdRequest = invoker.getXqdRequest();
        byte[] bytesToSend = serializer.serialize(xqdRequest);
        byte[] bytes = sendPost(invoker.getInvocationMeta(), url, bytesToSend);
        //反序列化
        XqdResponse response = serializer.deserialize(bytes, XqdResponse.class);
        return response.getResultData();
    }

    public static byte[] sendPost(SrpcInvocationMeta meta, SrpcRequestUrl url, byte[] bytesToSend) throws IOException {
        CloseableHttpClient httpClient = HttpClientProvider.getHttpClient();
        // 创建 POST 请求  srpcUrl 通过serviceCode 获取
        String srpcUrl = url.getUrl(meta.getServiceCode());
        HttpPost httpPost = buildAndCustomizeRequestConfig(srpcUrl, meta);
        // 设置请求体为字节流
        HttpEntity entity = new ByteArrayEntity(bytesToSend);
        httpPost.setEntity(entity);
        if (url.getSessionInfo() != null) {
            url.getSessionInfo().accept(httpPost);
        }
        // 发送请求并获取响应
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 处理响应
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new CommonException("HTTP 请求失败，状态码: " + statusCode + ", " + response.getStatusLine().getReasonPhrase());
            }
            // 如果需要获取响应内容
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                return null;
            }
            // 获取响应流
            InputStream inputStream = responseEntity.getContent();
            return toByteArray(inputStream);
        }

    }

    /**
     * 构建 HttpPost, 并自定义请求级配置: <br />
     * 比如  超时时间
     *
     * @param url  请求  url
     * @param meta 请求元数据
     * @return HttpPost
     */
    private static HttpPost buildAndCustomizeRequestConfig(String url, SrpcInvocationMeta meta) {
        HttpPost httpPost = new HttpPost(url);
        int socketTimeoutMs = meta.getTimeout();
        // 1：根据是否传入自定义超时，设置请求级配置
        if (socketTimeoutMs > 0) {
            // 特殊业务：用自定义传输超时（其他超时沿用全局默认）
            RequestConfig requestConfig = HttpClientProvider.createCustomSocketTimeoutConfig(socketTimeoutMs);
            // 给当前请求设置配置（仅当前生效）
            httpPost.setConfig(requestConfig);
        }
        return httpPost;

    }


    /**
     * InputStream 转byte数组
     *
     * @param input InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
