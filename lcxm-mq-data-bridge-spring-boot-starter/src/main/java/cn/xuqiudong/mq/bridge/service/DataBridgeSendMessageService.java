package cn.xuqiudong.mq.bridge.service;

import cn.xuqiudong.common.base.service.BaseGenericService;
import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;
import cn.xuqiudong.mq.bridge.mapper.DataBridgeSendMessageMapper;
import cn.xuqiudong.mq.bridge.model.DataBridgeSendMessage;
import cn.xuqiudong.mq.bridge.model.FetchMessageLookup;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *功能: :发送到mq的消息表 Service
 * @author Vic.xu
 * @since  2025-03-03 11:11
 */
@Service
public class DataBridgeSendMessageService extends BaseGenericService<DataBridgeSendMessageMapper, DataBridgeSendMessage,Integer> {


    @Resource
    private DataBridgeProperties dataBridgeProperties;

    @Override
    protected boolean hasAttachment() {
        return false;
    }

    /**
     * 获取待待送的消息：消息状态 待发送或者发送失败的 最早创建的消息 根据id 正序排列
     *@param lastTimeId lastTimeId 上次获取到的最后的消息id， 可以为空，非空时则大于这个id
     */
    public List<DataBridgeSendMessage> fetchMessageToSend(Integer lastTimeId) {
        FetchMessageLookup lookup = new FetchMessageLookup();
        lookup.setFlag(dataBridgeProperties.getSendFlag());
        lookup.setLastTimeId(lastTimeId);
        lookup.setSize(DataBridgeConstant.SEND_MESSAGE_FETCH_SIZE);
        return mapper.fetchMessageToSend(lookup);
    }

}
