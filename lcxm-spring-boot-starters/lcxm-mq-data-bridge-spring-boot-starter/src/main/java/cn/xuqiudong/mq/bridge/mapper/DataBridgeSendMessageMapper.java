package cn.xuqiudong.mq.bridge.mapper;

import cn.xuqiudong.basic.mybatisplus.mapper.BaseGenericMapper;
import cn.xuqiudong.mq.bridge.model.DataBridgeSendMessage;
import cn.xuqiudong.mq.bridge.model.FetchMessageLookup;

import java.util.List;

/**
 *功能: :发送到mq的消息表 Mapper
 * @author Vic.xu
 * @since  2025-03-03 14:55
 */
public interface DataBridgeSendMessageMapper extends BaseGenericMapper<DataBridgeSendMessage,Integer> {

    List<DataBridgeSendMessage> fetchMessageToSend(FetchMessageLookup lookup);
}
