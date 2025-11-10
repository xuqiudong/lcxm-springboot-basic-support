package cn.xuqiudong.common.base.transmission.base;

import cn.xuqiudong.common.base.craw.CrawlConnect;
import cn.xuqiudong.common.base.transmission.log.model.ThirdLogModel;
import cn.xuqiudong.common.base.transmission.log.service.ThirdLogService;
import cn.xuqiudong.common.base.vo.BooleanWithMsg;
import cn.xuqiudong.common.util.async.AsyncOperation;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述: 数据传输处理基类
 * @author Vic.xu
 * @since 2022-08-19 10:30
 */
public abstract class BaseApiService<R extends ThirdRequest> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 第三放请求工具基类 设置一些请求的基本信息
     */
    protected BaseThirdRequest thirdRequest;

    /**
     * 第三方日志记录service
     *
     */
    protected ThirdLogService thirdLogService;

    /**
     *异步操作
     */
    protected AsyncOperation asyncOperation;


    public BaseApiService(BaseThirdRequest thirdRequest, ThirdLogService thirdLogService, AsyncOperation asyncOperation) {
        this.thirdRequest = thirdRequest;
        this.thirdLogService = thirdLogService;
        this.asyncOperation = asyncOperation;
    }

    /**
     * 异步进行数据传输， 依赖于AsyncOperation
     * @param url ApiUrl
     * @param data data
     * @param <T> 请求数据泛型
     */
    public <T extends R> void apiAsynchronous(ApiUrl url, T data) {
        asyncOperation.put(() -> {
            this.api(url, data);
        });
    }


    /**
     * 进行数据传输；
     * 1. 进行参数处理
     * 2. 进行数据请求  和数据处理
     * 3. 进行日志记录
     *
     * @param url ApiUrl
     * @param data data
     * @param <T> 请求数据泛型
     */
    public <T extends ThirdRequest> void api(ApiUrl url, T data) {
        logger.info("{}第三方数据传输,请求接口地址{}, 接口说明{}", thirdType(), url.getUrl(), url.getText());
        //请求参数
        String requestText = "";
        //响应参数
        String responseText = "";
        // 1 -成功 0 -失败
        int status = 0;
        try {
            CrawlConnect con = thirdRequest.con(url.getUrl());
            //处理请求参数
            requestText = handlerRequest(url, data, con);
            //处理响应结果
            BooleanWithMsg response = handlerResponse(url, con);
            if (response.isSuccess()) {
                status = 1;
                responseText = response.getMessage();
            }
        } finally {
            //记录请求日志
            if (log()) {
                ThirdLogModel model = buildLogModel(requestText, responseText, data.getFid(), status);
                thirdLogService.insertWithTransaction(model);
            }
        }
    }

    /**
     *对CrawlConnect  中的请求参数进行处理， 并返回请求参数的toString后的值，用于日志记录
     * @param url ApiUrl
     * @param data data
     * @param connect 请求连接
     * @param <T> 请求数据泛型
     * @return 参数的toString
     */
    protected abstract <T extends ThirdRequest> String handlerRequest(ApiUrl url, T data, CrawlConnect connect);

    /**
     * 获取 CrawlConnect 的请求结果，并进入处理，且返回
     * @param url ApiUrl
     * @param connect 请求连接
     * @return BooleanWithMsg  成功 则返回响应的 String 值
     */
    protected abstract BooleanWithMsg handlerResponse(ApiUrl url, CrawlConnect connect);

    /**
     * 第三方类型
     * @return String
     */
    @NotEmpty
    protected abstract String thirdType();

    /**
     *  是否需要记录日志到表
     * @return boolean
     */
    protected abstract boolean log();

    /**
     * 当前操作用户id
     * @return String
     */
    protected abstract String currentUserId();


    /**
     * 构建请求日志
     * @param request request String
     * @param response response String
     * @param fid fid
     * @param status status
     * @return ThirdLogModel
     */
    private ThirdLogModel buildLogModel(String request, String response, String fid, int status) {
        ThirdLogModel model = new ThirdLogModel();
        model.setThird(thirdType());
        model.setRequest(request);
        model.setResponse(response);
        model.setCreateUserId(currentUserId());
        model.setStatus(status);
        model.setFid(fid);
        return model;
    }

}
