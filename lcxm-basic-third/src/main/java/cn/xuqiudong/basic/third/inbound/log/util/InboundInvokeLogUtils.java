package cn.xuqiudong.basic.third.inbound.log.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.xuqiudong.basic.core.util.JsonUtil;
import cn.xuqiudong.basic.third.inbound.log.annotation.InboundLogIgnore;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.InputStreamSource;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 第三方入站调用日志工具。
 *
 * @author Vic.xu
 */
public final class InboundInvokeLogUtils {

    private InboundInvokeLogUtils() {
    }

    /**
     * 序列化方法入参，过滤文件流、Servlet 对象、标记忽略的参数和项目补充忽略类型。
     */
    public static String serializeArgs(Method method, Object[] args, Class<?>[] extraIgnoreTypes) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        Annotation[][] parameterAnnotations = method == null ? new Annotation[0][0] : method.getParameterAnnotations();
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (isParameterIgnored(parameterAnnotations, i)) {
                values.add("[ignored:InboundLogIgnore]");
                continue;
            }
            values.add(toLoggableValue(args[i], extraIgnoreTypes));
        }
        return toJson(values);
    }

    /**
     * 序列化方法出参。
     */
    public static String serializeValue(Object value, Class<?>[] extraIgnoreTypes) {
        return toJson(toLoggableValue(value, extraIgnoreTypes));
    }

    /**
     * 将异常堆栈转为字符串。
     */
    public static String stackTraceToString(Throwable error) {
        if (error == null) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        error.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private static boolean isParameterIgnored(Annotation[][] parameterAnnotations, int index) {
        if (parameterAnnotations == null || index >= parameterAnnotations.length) {
            return false;
        }
        for (Annotation annotation : parameterAnnotations[index]) {
            if (annotation instanceof InboundLogIgnore) {
                return true;
            }
        }
        return false;
    }

    private static Object toLoggableValue(Object value, Class<?>[] extraIgnoreTypes) {
        if (value == null) {
            return null;
        }
        if (value instanceof MultipartFile) {
            MultipartFile file = (MultipartFile) value;
            return "[MultipartFile name=" + file.getName() + ", filename=" + file.getOriginalFilename()
                    + ", size=" + file.getSize() + "]";
        }
        if (shouldIgnore(value, extraIgnoreTypes)) {
            return "[ignored:" + value.getClass().getSimpleName() + "]";
        }
        return value;
    }

    private static boolean shouldIgnore(Object value, Class<?>[] extraIgnoreTypes) {
        if (isDefaultIgnored(value)) {
            return true;
        }
        if (extraIgnoreTypes == null || extraIgnoreTypes.length == 0) {
            return false;
        }
        for (Class<?> ignoreType : extraIgnoreTypes) {
            if (ignoreType != null && ignoreType.isInstance(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDefaultIgnored(Object value) {
        return value instanceof ServletRequest
                || value instanceof ServletResponse
                || value instanceof HttpSession
                || value instanceof File
                || value instanceof InputStream
                || value instanceof OutputStream
                || value instanceof byte[]
                || value instanceof InputStreamSource
                || value instanceof BindingResult
                || value instanceof Model
                || value instanceof ModelMap;
    }

    private static String toJson(Object value) {
        try {
            String json = JsonUtil.toJson(value);
            return json == null ? String.valueOf(value) : json;
        } catch (RuntimeException e) {
            return "[serialize failed:" + e.getMessage() + "]";
        }
    }
}
