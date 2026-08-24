package cn.xuqiudong.basic.secureid.web;

import cn.xuqiudong.basic.secureid.util.IdUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;

import java.util.Map;

/**
 * 解密 Controller 方法上的普通 {@link PathVariable} 参数。
 * <p>
 * 这个解析器注册在 Spring 默认路径参数解析器之前，只接管明确能处理的普通路径变量：
 * <ul>
 *     <li>支持 {@code @PathVariable String/Long/Integer/...}</li>
 *     <li>不支持 {@code @PathVariable Map}，Map 场景继续交给 Spring 默认解析器</li>
 *     <li>不改写 request URI，不影响 Spring MVC 的路径匹配结果</li>
 * </ul>
 *
 * @author Vic.xu
 * @since 2026-08-24
 */
public class SecurePathVariableArgumentResolver extends PathVariableMethodArgumentResolver {

    /**
     * 只抢普通 {@code @PathVariable} 参数，并复用 Spring 默认 resolver 的基础支持判断。
     * <p>
     * Spring MVC 会按 resolver 注册顺序依次调用本方法；这里返回 false 的参数会继续交给后面的默认解析器。
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return super.supportsParameter(parameter)
                && !Map.class.isAssignableFrom(parameter.getParameterType());
    }

    /**
     * 从 Spring MVC 已经匹配出的路径变量 Map 中取值，然后只额外增加 secure-id 解密。
     * <p>
     * {@link PathVariableMethodArgumentResolver#resolveName(String, MethodParameter, NativeWebRequest)}
     * 内部读取的是 {@code HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE}，也就是 Spring MVC
     * 路由匹配后保存的路径变量 Map。这里不重新解析 URI，也不改变 URL 匹配过程。
     * <p>
     * required 校验、空值处理、String 到 Long/Integer 等类型转换继续由父类完成。
     */
    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object value = super.resolveName(name, parameter, request);
        if (value instanceof String) {
            return IdUtil.decrypt((String) value);
        }
        return value;
    }

}
