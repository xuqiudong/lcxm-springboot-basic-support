package cn.xuqiudong.basic.secureid.filter;

import cn.xuqiudong.basic.secureid.util.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParameterRequestWrapperTest {

    @Test
    public void decryptsParameterMapWithoutChangingOriginalRequestBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("id", IdUtil.encrypt("100"));
        request.setContent("origin-body".getBytes(StandardCharsets.UTF_8));

        ParameterRequestWrapper wrapper = new ParameterRequestWrapper(request, request.getParameterMap());

        assertEquals("100", wrapper.getParameter("id"));
        assertEquals("origin-body", readBody(wrapper));
    }

    @Test
    public void decryptsJsonBodyAndCanReadRepeatedly() throws Exception {
        String encryptedId = IdUtil.encrypt("200");
        String json = "{\"id\":\"" + encryptedId + "\"}";
        MockHttpServletRequest request = new MockHttpServletRequest();

        ParameterRequestWrapper wrapper = new ParameterRequestWrapper(request, json, request.getParameterMap());

        assertEquals("{\"id\":\"200\"}", readBody(wrapper));
        assertEquals("{\"id\":\"200\"}", readBody(wrapper));
        assertTrue(wrapper.getInputStream().isReady());
    }

    private static String readBody(ParameterRequestWrapper wrapper) throws Exception {
        StringBuilder builder = new StringBuilder();
        String line;
        try (java.io.BufferedReader reader = wrapper.getReader()) {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
