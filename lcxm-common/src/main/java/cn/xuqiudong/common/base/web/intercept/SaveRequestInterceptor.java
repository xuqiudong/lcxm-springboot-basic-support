/**
 *
 */
package cn.xuqiudong.common.base.web.intercept;

import cn.xuqiudong.common.base.tool.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 保存最后一次get请求的拦截器
 *
 * @author VIC
 */
public class SaveRequestInterceptor implements HandlerInterceptor {

    public static final Logger LOGGER = LoggerFactory.getLogger(SaveRequestInterceptor.class);

    public static final String LAST_URL = "last_request_get_url";


    /**
     * 放在请求后保存到session中
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (!response.isCommitted()) {
            if (HttpMethod.GET.name().equalsIgnoreCase(request.getMethod())) {

                request.getSession().setAttribute(LAST_URL, Tools.getRequestUrl(request));
            }
        } else {
            LOGGER.warn("Response has been committed, skipping Session manipulation.");

        }


    }
}