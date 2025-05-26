/**
 *
 */
package cn.xuqiudong.common.util.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 *  说明 :  web相关的一些工具类
 *  @author Vic.xu
 * @since  2020年5月20日上午10:36:50
 */
public final class WebCommonUtils {

    private static Logger logger = LoggerFactory.getLogger(WebCommonUtils.class);

    /**
     * 主要是为了在文件上传表单中也能从request中读取到普通参数;
     * 判断是否是文件上传 并且是否已经被SpringMVC解析过, 没有则解析(此处解析后则DispatcherServlet中不再解析);
     * 注: 因为只能解析一次,后续是解析不到数据的;
     * 只能解析一次原因是springMVC使用的是common-fileUplad的工具类解析数据的
     * 参照代码ServletFileUpload.parseRequest(request); 其中的copy方法会从HttpServletRequest中读取流,,而读完后的position会到-1,
     * 在未显式调用reset方法之前,再次读取流是都不到的,而ServletInputStream中并未重写该方法.
     *
     * Tips:若想重复利用request的流,可以利用HttpServletRequestWrapper,重新包裹request,保存住流的数据,以达到重复利用,此处不细表.
     * @param request
     * @return request
     * @author Vic.xu
     */
    public static HttpServletRequest checkMultipart(HttpServletRequest request) {
        String enctype = request.getContentType();
        String method = request.getMethod();
        ////是否是文件表单
        boolean isFileForm = HttpMethod.POST.matches(method) && StringUtils.isNotBlank(enctype)
                && enctype.contains(MediaType.MULTIPART_FORM_DATA_VALUE);
        if (isFileForm) {
            //参见 DispatcherServlet#checkMultipart
            MultipartHttpServletRequest multipartHttpServletRequest = WebUtils.getNativeRequest(request,
                    MultipartHttpServletRequest.class);
            if (multipartHttpServletRequest == null) {
                // 文件解析器 默认一般为CommonsMultipartResolver ,若项目中重写了,则此处理应替换为项目默认的文件解析器
                MultipartResolver resolver = new CommonsMultipartResolver(
                        ((HttpServletRequest) request).getSession().getServletContext());
                multipartHttpServletRequest = resolver.resolveMultipart((HttpServletRequest) request);
                request = multipartHttpServletRequest;
            } else {
                logger.debug("Request is already a MultipartHttpServletRequest");
            }
        }
        return request;
    }

    /**
     * 把request 中的参数存入到session中
     * @param request HttpServletRequest
     */
    public static void storageRequestParameterToSession(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        HttpSession session = request.getSession(true);
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            session.setAttribute(name, request.getParameter(name));
        }
    }

    /**
     * 从session 中获取参数，并移除
     * @param request HttpServletRequest
     * @param name attribute name
     * @return value of attribute
     */
    public static Object getAndRemoveSessionAttribute(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return null;
        }
        HttpSession session = request.getSession(true);
        Object value = session.getAttribute(name);
        session.removeAttribute(name);
        return value;
    }
}