package cn.xuqiudong.common.base.web.filter.wrapper;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * 描述:
 *      用于获取 重定向地址： 即 response.sendRedirect(url)中的url
 *      其中url为原始值，可能是相对地址，也可能是绝对地址
 * @author Vic.xu
 * @since 2025-02-08 9:34
 */
public class RedirectResponseWrapper extends HttpServletResponseWrapper {

    /**
     * 重定向的地址
     */
    private String redirectUrl;
    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response The response to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public RedirectResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    /**
     * 重写sendRedirect方法，用于获取重定向地址，而非直接重定向
     * 重定向与否由后续逻辑决定
     */
    @Override
    public void sendRedirect(String location) throws IOException {
        this.redirectUrl = location;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}
