package cn.xuqiudong.mq.bridge.archive;

import org.springframework.stereotype.Service;

/**
 * 描述:
 *   接收消息归档服务
 * @author Vic.xu
 * @since 2026-02-02 14:57
 */
@Service
public class ReceiveMessageArchiveService extends BaseArchiveService{

    @Override
    protected String getTableName() {
        return "t_data_bridge_receive_message";
    }
}
