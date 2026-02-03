package cn.xuqiudong.mq.bridge.archive;

import org.springframework.stereotype.Service;

/**
 * 描述:
 * 消息发送归档服务
 *
 * @author Vic.xu
 * @since 2026-02-02 14:56
 */
@Service
public class SendMessageArchiveService extends BaseArchiveService {

    @Override
    protected String getTableName() {
        return "t_data_bridge_send_message";
    }
}
