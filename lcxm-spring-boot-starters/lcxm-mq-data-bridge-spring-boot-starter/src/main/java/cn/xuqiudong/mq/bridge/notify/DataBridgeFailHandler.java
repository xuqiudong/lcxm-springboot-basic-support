package cn.xuqiudong.mq.bridge.notify;

/**
 * 描述:
 *   消息发送/消费失败通知接口， 具体实现由项目自行实现, 比如 DataBridgeFailHandlerImpl
 * @author Vic.xu
 * @since 2026-02-05 14:47
 */
public interface DataBridgeFailHandler {

    void onFail(FailContext failContext);
}
