package cn.xuqiudong.mq.bridge.vo;

import org.apache.tomcat.util.modeler.BaseModelMBean;

import java.io.Serializable;

/**
 * 描述:
 *      data bridge 相关mq开关状态
 * @author Vic.xu
 * @since 2025-02-27 11:41
 */
public class DataBridgeEnableVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean mqEnable;

    private boolean sendEnable;

    private boolean receiveEnable;

    private boolean consumerEnable;

    public DataBridgeEnableVO() {
    }

    public DataBridgeEnableVO(boolean receiveEnable, boolean mqEnable, boolean sendEnable, boolean consumerEnable) {
        this.receiveEnable = receiveEnable;
        this.mqEnable = mqEnable;
        this.sendEnable = sendEnable;
        this.consumerEnable = consumerEnable;
    }

    public boolean isMqEnable() {
        return mqEnable;
    }

    public void setMqEnable(boolean mqEnable) {
        this.mqEnable = mqEnable;
    }

    public boolean isSendEnable() {
        return sendEnable;
    }

    public void setSendEnable(boolean sendEnable) {
        this.sendEnable = sendEnable;
    }

    public boolean isReceiveEnable() {
        return receiveEnable;
    }

    public void setReceiveEnable(boolean receiveEnable) {
        this.receiveEnable = receiveEnable;
    }

    public boolean isConsumerEnable() {
        return consumerEnable;
    }

    public void setConsumerEnable(boolean consumerEnable) {
        this.consumerEnable = consumerEnable;
    }
}
