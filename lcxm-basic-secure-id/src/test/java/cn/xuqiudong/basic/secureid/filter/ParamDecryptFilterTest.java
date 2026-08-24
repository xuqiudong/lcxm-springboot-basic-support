package cn.xuqiudong.basic.secureid.filter;

import cn.xuqiudong.basic.secureid.util.IdUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ParamDecryptFilterTest {

    @Test
    public void decryptsQueryParameter() throws Exception {
        ParamDecryptFilter filter = new ParamDecryptFilter(new NoopMultipartResolver());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        request.addParameter("id", IdUtil.encrypt("300"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new AssertParameterChain("id", "300"));
    }

    @Test
    public void decryptsJsonBodyAndKeepsBodyReadable() throws Exception {
        ParamDecryptFilter filter = new ParamDecryptFilter(new NoopMultipartResolver());
        String encryptedId = IdUtil.encrypt("400");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/user");
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);
        request.setContent(("{\"id\":\"" + encryptedId + "\"}").getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
                HttpServletRequest wrappedRequest = (HttpServletRequest) servletRequest;
                assertEquals("{\"id\":\"400\"}", readBody(wrappedRequest));
                assertEquals("{\"id\":\"400\"}", readBody(wrappedRequest));
            }
        });
    }

    @Test
    public void decryptsMultipartFieldAndKeepsMultipartFileAvailable() throws Exception {
        ParamDecryptFilter filter = new ParamDecryptFilter(new NoopMultipartResolver());
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/upload");
        request.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        request.addParameter("id", IdUtil.encrypt("500"));
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "file-content".getBytes(StandardCharsets.UTF_8));
        request.addFile(file);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {
                HttpServletRequest wrappedRequest = (HttpServletRequest) servletRequest;
                assertEquals("500", wrappedRequest.getParameter("id"));
                MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(wrappedRequest, MultipartHttpServletRequest.class);
                assertNotNull(multipartRequest);
                assertSame(file, multipartRequest.getFile("file"));
            }
        });
    }

    private static String readBody(HttpServletRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private static class AssertParameterChain implements FilterChain {
        private final String name;
        private final String expectedValue;

        private AssertParameterChain(String name, String expectedValue) {
            this.name = name;
            this.expectedValue = expectedValue;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            assertEquals(expectedValue, request.getParameter(name));
        }
    }

    private static class NoopMultipartResolver implements MultipartResolver {
        @Override
        public boolean isMultipart(HttpServletRequest request) {
            return false;
        }

        @Override
        public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) {
            throw new IllegalStateException("resolveMultipart should not be called in this test");
        }

        @Override
        public void cleanupMultipart(MultipartHttpServletRequest request) {
        }
    }
}
