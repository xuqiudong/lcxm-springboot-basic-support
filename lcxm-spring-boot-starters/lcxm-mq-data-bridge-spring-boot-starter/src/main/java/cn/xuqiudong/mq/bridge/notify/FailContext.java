package cn.xuqiudong.mq.bridge.notify;

import cn.xuqiudong.mq.bridge.enums.OperationEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述:
 *   失败通知上下文
 * @author Vic.xu
 * @since 2026-02-05 14:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailContext {

    /**
     * 操作类型
     */
    private OperationEnum operation;

    /**
     * 消息id
     */
    private Integer messageId;

    /**
     * 错误信息
     */
    private String errorMsg;


}
