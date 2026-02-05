package cn.xuqiudong.mq.bridge;

import cn.xuqiudong.common.util.JsonUtil;
import cn.xuqiudong.mq.bridge.notify.DataBridgeFailHandler;
import cn.xuqiudong.mq.bridge.notify.FailContext;
import org.springframework.stereotype.Component;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2026-02-05 15:15
 */
@Component
public class DataBridgeFailHandlerImpl implements DataBridgeFailHandler {

    @Override
    public void onFail(FailContext failContext) {
        System.out.println("DataBridgeFailHandlerImpl onFail" );
        JsonUtil.printJson(failContext);
    }
}
