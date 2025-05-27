package cn.xuqiudong.common.base.web;

import cn.xuqiudong.common.base.enums.CommonMsgEnum;
import cn.xuqiudong.common.base.exception.BadParamException;
import cn.xuqiudong.common.base.exception.CommonException;
import cn.xuqiudong.common.base.exception.UnauthorizedException;
import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.common.base.tool.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 描述:全局异常捕捉，兼容json和view
 * @author Vic.xu
 * @since 2022-03-01 9:13
 */
@ControllerAdvice
@ConditionalOnClass(HttpServletRequest.class)
public class GlobalExceptionHandler {

    protected static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    protected static final String ERROR_PAGE = "error/500";

    /**
     * 无权限访问异常
     * @param request r
     * @param e  UnauthorizedException
     * @return mv
     */
    @ExceptionHandler(value = {UnauthorizedException.class})
    public ModelAndView unauthorizedHandler(HttpServletRequest request, UnauthorizedException e) {
        logger.error("无访问{}权限", request.getRequestURI());
        BaseResponse<?> baseResponse = BaseResponse.error("无访问权限");
        return createModelAndView(request, baseResponse);
    }

    /**
     * 通用异常捕捉
     * @param request rq
     * @param e CommonException
     * @return mv
     */
    @ExceptionHandler(value = {CommonException.class})
    public ModelAndView commonHandler(HttpServletRequest request, CommonException e) {
        logger.error("通用异常", e);
        BaseResponse<?> baseResponse = BaseResponse.error(e.getCode(), e.getMessage());
        return createModelAndView(request, baseResponse);
    }

    /**
     * 参数异常捕捉
     * @param request rq
     * @param e BadParamException
     * @return mv
     */
    @ExceptionHandler(value = {BadParamException.class})
    public ModelAndView badParamHandler(HttpServletRequest request, BadParamException e) {
        logger.error("请求参数绑定异常", e);
        String msg = CommonMsgEnum.PARAM_ERROR.getMsg();
        if (e.isValidated()) {
            msg = e.getMessage();
        }
        return createModelAndView(request, BaseResponse.error(CommonMsgEnum.PARAM_ERROR.getCode(), msg));
    }

    /**
     * 可能是没有定义的异常
     * @param request r
     * @param e Exception
     * @return mv
     */
    @ExceptionHandler(value = {Exception.class})
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("非自定义异常", e);
        BaseResponse<?> baseResponse = BaseResponse.error(e.getMessage());
        return createModelAndView(request, baseResponse);
    }

    /**
     * 根据是否ajax请求返回json错误或者500页面
     * @param request r
     * @param baseResponse rs
     * @return mv
     */
    protected ModelAndView createModelAndView(HttpServletRequest request, BaseResponse<?> baseResponse) {

        boolean isAjax = Tools.isAjax(request);

        if (isAjax) {
            ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
            modelAndView.addObject(baseResponse);
            return modelAndView;
        }

        //非ajax请求返回500页面
        ModelAndView modelAndView = new ModelAndView(ERROR_PAGE);
        modelAndView.addObject("msg", baseResponse.getMsg());
        modelAndView.addObject("code", baseResponse.getCode());
        return modelAndView;
    }

}
