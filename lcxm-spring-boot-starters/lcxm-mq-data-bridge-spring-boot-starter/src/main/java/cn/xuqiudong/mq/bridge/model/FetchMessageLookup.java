package cn.xuqiudong.mq.bridge.model;

import cn.xuqiudong.mq.bridge.constant.DataBridgeConstant;

import java.io.Serializable;

/**
 * 描述:
 *    消费或者发送消息时的查询条件
 * @author Vic.xu
 * @since 2025-05-19 11:12
 */
public class FetchMessageLookup implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 查询的条数
     */
    private int size = DataBridgeConstant.SEND_MESSAGE_FETCH_SIZE;

    /**
     * 上一次查询的id
     */
    private Integer lastTimeId;

    /**
     * 查询的标识
     */
    private String flag;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getLastTimeId() {
        return lastTimeId;
    }

    public void setLastTimeId(Integer lastTimeId) {
        this.lastTimeId = lastTimeId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
