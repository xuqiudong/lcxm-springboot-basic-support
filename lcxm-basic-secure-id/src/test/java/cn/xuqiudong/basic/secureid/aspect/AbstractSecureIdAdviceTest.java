package cn.xuqiudong.basic.secureid.aspect;

import cn.xuqiudong.basic.core.util.JsonUtil;
import cn.xuqiudong.basic.secureid.annotation.SecureId;
import cn.xuqiudong.basic.secureid.annotation.SkipIdSecure;
import cn.xuqiudong.basic.secureid.core.IdEncryptable;
import cn.xuqiudong.basic.secureid.model.SecureIdContext;
import cn.xuqiudong.basic.secureid.model.SecureIdFieldMetadata;
import cn.xuqiudong.basic.secureid.util.IdUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractSecureIdAdviceTest {

    private final TestSecureIdAdvice advice = new TestSecureIdAdvice();

    @Test
    public void encryptsIdEncryptableAndSecureIdFieldsInNestedObjects() {
        DemoDto dto = new DemoDto("1", "2");
        dto.child = new DemoDto("3", "4");

        advice.encrypt(dto);
        JsonUtil.printJson(dto);

        assertEncryptedValue("1", dto.id);
        assertEncryptedValue("2", dto.userId);
        assertEncryptedValue("3", dto.child.id);
        assertEncryptedValue("4", dto.child.userId);
        assertEquals("plain", dto.name);
    }

    @Test
    public void encryptsMapStringIdValueAndRecursesObjectValueWhenKeyMatchesIdFieldName() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", new DemoDto("11", "12"));
        map.put("deptId", "13");

        advice.encrypt(map);

        DemoDto userValue = (DemoDto) map.get("userId");
        assertEncryptedValue("11", userValue.id);
        assertEncryptedValue("12", userValue.userId);
        assertEncryptedValue("13", (String) map.get("deptId"));
    }

    @Test
    public void encryptsIterableAndArrayElements() {
        DemoDto first = new DemoDto("21", "22");
        DemoDto second = new DemoDto("23", "24");
        Object[] array = new Object[]{first, Arrays.asList(second)};

        advice.encrypt(array);

        assertEncryptedValue("21", first.id);
        assertEncryptedValue("22", first.userId);
        assertEncryptedValue("23", second.id);
        assertEncryptedValue("24", second.userId);
    }

    @Test
    public void letsSubclassExtractDataFromSpecialObject() {
        PageLike page = new PageLike(Arrays.asList(new DemoDto("31", "32")));

        advice.encrypt(page);

        DemoDto row = page.rows.get(0);
        assertEncryptedValue("31", row.id);
        assertEncryptedValue("32", row.userId);
    }

    @Test
    public void skipsMethodWithSkipAnnotation() throws Throwable {
        DemoDto dto = new DemoDto("41", "42");
        Method method = TestController.class.getDeclaredMethod("skip");

        advice.afterReturning(dto, method, new Object[0], new TestController());

        assertEquals("41", dto.id);
        assertEquals("42", dto.userId);
    }

    @Test
    public void cachesFieldMetadataByClass() {
        List<SecureIdFieldMetadata> first = advice.metadata(DemoDto.class);
        List<SecureIdFieldMetadata> second = advice.metadata(DemoDto.class);

        assertSame(first, second);
        assertFalse(first.isEmpty());
        assertTrue(hasFieldMetadata(first, "userId", true));
        assertTrue(hasFieldMetadata(first, "name", false));
    }

    private static boolean hasFieldMetadata(List<SecureIdFieldMetadata> metadataList, String fieldName, boolean encryptField) {
        for (SecureIdFieldMetadata metadata : metadataList) {
            if (metadata.field().getName().equals(fieldName) && metadata.encryptField() == encryptField) {
                return true;
            }
        }
        return false;
    }

    private static void assertEncryptedValue(String expectedPlainValue, String encryptedValue) {
        System.out.printf("secure-id encrypt: plain=%s, encrypted=%s, decrypted=%s%n",
                expectedPlainValue, encryptedValue, IdUtil.decrypt(encryptedValue));
        assertFalse(expectedPlainValue.equals(encryptedValue));
        assertEquals(expectedPlainValue, IdUtil.decrypt(encryptedValue));
    }

    private static class TestSecureIdAdvice extends AbstractSecureIdAdvice {

        void encrypt(Object value) {
            encryptValue(value, new SecureIdContext());
        }

        List<SecureIdFieldMetadata> metadata(Class<?> type) {
            return getFieldMetadata(type);
        }

        @Override
        protected Object extractData(Object returnValue, Method method, Object[] args, Object target) {
            return returnValue;
        }

        @Override
        protected boolean encryptSpecialObject(Object value, SecureIdContext context) {
            if (value instanceof PageLike) {
                encryptValue(((PageLike) value).rows, context);
                return true;
            }
            return false;
        }

        @Override
        protected Set<String> skipUrls() {
            return Collections.emptySet();
        }

        @Override
        protected Set<String> commonIdFieldNames() {
            return Collections.singleton("deptId");
        }

        @Override
        protected int maxDepth() {
            return 4;
        }
    }

    @Data
    private static class DemoDto implements IdEncryptable {
        private String id;

        @SecureId
        private String userId;

        private String name = "plain";

        private DemoDto child;

        DemoDto(String id, String userId) {
            this.id = id;
            this.userId = userId;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }
    }

    @Data
    private static class PageLike {
        private final List<DemoDto> rows;

        PageLike(List<DemoDto> rows) {
            this.rows = rows;
        }
    }


    private static class TestController {
        @SkipIdSecure
        void skip() {
        }
    }
}
