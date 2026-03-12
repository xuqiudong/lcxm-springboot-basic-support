package cn.xuqiudong.basic.core.aspect;

import cn.xuqiudong.basic.core.aspect.annotation.LogPrint;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import java.util.Enumeration;

/**
 * 接口的出入参日志记录
 *    LogPrint && RequestMapping
 * @author VIC
 *
 */
@Aspect
@Component
public class RequestLogPrintAspect {

    @Resource
    private HttpServletRequest request;

    public Logger logger = LoggerFactory.getLogger(RequestLogPrintAspect.class);

    private static final String CURRENT_REQUEST_TIME = "current_request_time";

    private static final String CURRENT_REQUEST_FLAG = "request_log_print_aspect_flag";

    private static final String LOG_NAME = "【request log】";

    private static ObjectMapper objectMapper = new ObjectMapper();


    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 日志记录的切面 LogPrint && RequestMapping
     * (execution(* pers.vic.api.*.controller.*.*(..))
     * "(@annotation(pers.vic.boot.base.aspect.annotation.LogPrint)) && (@annotation(org.springframework.web.bind.annotation.RequestMapping))"
     */
    @Pointcut("@annotation(cn.xuqiudong.basic.core.aspect.annotation.LogPrint)")
    public void log() {

    }

    /**
     * 进入请求的时候拼装相关信息
     *
     * @param joinPoint
     */
    @Before("log()")
    public void before(JoinPoint joinPoint) {
        long inTime = System.currentTimeMillis();
        //本次请求时间
        this.request.setAttribute(CURRENT_REQUEST_TIME, inTime);
        String controller = joinPoint.getTarget().getClass().getSimpleName();
        String method = joinPoint.getSignature().getName();
        //本次请求的日志标识
        StringBuffer flag = new StringBuffer(String.valueOf(inTime));
        flag.append("-").append(controller).append(".").append(method);
        // 把日志标识放入request,request是线程安全的
        this.request.setAttribute(CURRENT_REQUEST_FLAG, flag.toString());

        LogPrint logPrint = getLogPrint(joinPoint);
        if (!logPrint.in()) {
            //不打印入参日志 则返回
            return;
        }

        StringBuilder paramsStringBuilder = new StringBuilder();
        Enumeration<String> e = request.getParameterNames();
        if (e.hasMoreElements()) {
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    paramsStringBuilder.append(name).append("=").append(values[0]);
                } else {
                    paramsStringBuilder.append(name).append("[]={").append(String.join(",", values)).append("}");
                }
                paramsStringBuilder.append(",");
            }
        }
        if (paramsStringBuilder.length() > 0) {
            paramsStringBuilder.deleteCharAt(paramsStringBuilder.length() - 1);
        }
        printLog(flag.toString(), "入参", paramsStringBuilder.toString());

    }


    /**
     * 打印请求以及返回值
     */
    @AfterReturning(pointcut = "log()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        LogPrint logPrint = getLogPrint(joinPoint);
        String flag = (String) request.getAttribute(CURRENT_REQUEST_FLAG);
        //打印耗时
        if (logPrint.time()) {
            long in = (long) request.getAttribute(CURRENT_REQUEST_TIME);
            long time = System.currentTimeMillis() - in;
            printLog(flag, "耗时", time + "ms");
        }
        if (logPrint.out()) {
            String out = null;
            try {
                out = objectMapper.writeValueAsString(returnValue);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                out = returnValue + "";
            }
            printLog(flag, "出参", out);
        }
    }

    /**
     * 前缀
     */
    private void printLog(String flag, String type, String content) {
        String info = LOG_NAME + flag + " " + type + ":{" + content + "}";
        logger.info(info);
    }

    /**
     * 获取方法注解
     */
    private LogPrint getLogPrint(@NotNull JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogPrint annotation = signature.getMethod().getAnnotation(LogPrint.class);
        return annotation;

    }
}