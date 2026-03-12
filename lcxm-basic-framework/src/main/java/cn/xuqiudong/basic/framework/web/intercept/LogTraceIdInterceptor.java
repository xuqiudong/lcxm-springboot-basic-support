package cn.xuqiudong.basic.framework.web.intercept;

import cn.xuqiudong.basic.framework.web.intercept.log.TraceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 描述: 通过拦截为日志加入traceId，方便统一追加
 * @author Vic.xu
 * @since 2022-12-12 10:57
 */
public class LogTraceIdInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TraceUtils.createTraceId();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TraceUtils.destroyTraceId();
    }


}
