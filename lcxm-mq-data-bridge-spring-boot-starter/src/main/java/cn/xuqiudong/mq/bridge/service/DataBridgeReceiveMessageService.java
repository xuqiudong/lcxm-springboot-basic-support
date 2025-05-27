package cn.xuqiudong.mq.bridge.service;

import cn.xuqiudong.common.base.service.BaseGenericService;
import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;
import cn.xuqiudong.mq.bridge.mapper.DataBridgeReceiveMessageMapper;
import cn.xuqiudong.mq.bridge.model.DataBridgeReceiveMessage;
import cn.xuqiudong.mq.bridge.model.FetchMessageLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 功能: :接收自mq的消息表 Service
 *
 * @author Vic.xu
 * @since 2025-03-03 11:11
 */
@Service
public class DataBridgeReceiveMessageService extends BaseGenericService<DataBridgeReceiveMessageMapper, DataBridgeReceiveMessage, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBridgeReceiveMessageService.class);

    @Resource
    private DataBridgeProperties dataBridgeProperties;

    @Override
    protected boolean hasAttachment() {
        return false;
    }

    /**
     * 每次提取消息 消费： 解析失败的/ 初始化的 /失败的  按照时间正序排列（保持顺序）
     *
     * @param lastTimeId 上一次提取到的最后的消息id
     */
    public List<DataBridgeReceiveMessage> fetchMessageToConsumer(Integer lastTimeId) {
        FetchMessageLookup lookup = new FetchMessageLookup();
        lookup.setFlag(dataBridgeProperties.getConsumeFlag());
        lookup.setLastTimeId(lastTimeId);
        lookup.setSize(DataBridgeConstant.Consumer_MESSAGE_FETCH_SIZE);
        return mapper.fetchMessageToConsumer(lookup);
    }

    public void checkMessageIdThenInsert(DataBridgeReceiveMessage entity) {
        long count = mapper.countByColumnValue(null, entity.getMessageId(), "message_id");
        if (count > 0) {
            //messageId重复的消息 忽略
            LOGGER.warn("忽略messageId重复的消息入表!  messageId:{}", entity.getMessageId());
            return;
        }
        insert(entity);
    }
}
