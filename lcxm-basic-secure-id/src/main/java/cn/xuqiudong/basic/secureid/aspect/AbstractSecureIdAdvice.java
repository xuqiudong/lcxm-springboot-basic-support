package cn.xuqiudong.basic.secureid.aspect;

import cn.xuqiudong.basic.secureid.annotation.SecureId;
import cn.xuqiudong.basic.secureid.annotation.SkipIdSecure;
import cn.xuqiudong.basic.secureid.core.IdEncryptable;
import cn.xuqiudong.basic.secureid.model.SecureIdContext;
import cn.xuqiudong.basic.secureid.model.SecureIdFieldMetadata;
import cn.xuqiudong.basic.secureid.util.IdUtil;
import cn.xuqiudong.basic.secureid.util.SecureIdClassUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 * id 加密的切面处理 的基类
 * 暂定在return之后， 所以只修改引用类 内部的id
 * <p>
 * 当前父类已经实现的通用流程：
 * 1. 判断方法是否跳过；
 * 2. 判断当前请求 URL 是否跳过；
 * 3. 调用子类 {@link #extractData(Object, Method, Object[], Object)} 提取需要处理的数据；
 * 4. 按深度递归处理 IdEncryptable、@SecureId 字段、集合、数组、Map value 和普通对象字段；
 * 5. 使用 {@link SecureIdContext} 控制递归深度和循环引用；
 * 6. 单个字段处理失败时跳过该字段，避免影响整个接口返回。
 * <p>
 * 当前父类刻意不实现、交给子类处理的内容：
 * 1. BaseResponse 或其他统一响应对象如何取 data；
 * 2. PageInfo、IPage、自定义分页对象等特殊结构如何展开；
 * 3. 项目约定的常见外键字段名；
 * 4. URL 白名单、匿名接口等项目级跳过规则。
 * <p>
 * 性能说明：
 * 普通对象字段处理元数据会按 Class 缓存，避免每个响应对象重复扫描字段和注解。
 * commonIdFieldNames 应保持稳定，不建议做成请求级动态值。
 *
 * @author Vic.xu
 * @since 2026-08-20 17:46
 */
public abstract class AbstractSecureIdAdvice implements AfterReturningAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSecureIdAdvice.class);

    /**
     * URL 路径匹配器。
     * <p>
     * 用于匹配 skipUrls 中的 Ant 风格路径，比如 /api/public/**。
     */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 普通对象字段处理元数据缓存。
     * <p>
     * key 是对象 Class，value 是已经过滤 static/transient 后的字段元数据。
     * 元数据包含 Field 和该字段是否按 ID 字段处理，后续递归时不再重复扫描注解和字段名。
     */
    private final Map<Class<?>, List<SecureIdFieldMetadata>> fieldMetadataCache = new ConcurrentHashMap<>();

    /**
     * 响应加密入口。
     * <p>
     * 主流程：
     * 1. 空返回值、跳过注解、URL 白名单、业务跳过规则直接跳过；
     * 2. 由子类从项目响应模型中提取真正的数据部分，如 BaseResponse.data；
     * 3. 父类从数据对象开始做通用递归遍历和 ID 加密；
     * 4. 单个字段或对象处理失败时只记录日志，不中断接口返回。
     */
    @Override
    public void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable {
        if (returnValue == null || shouldSkip(method, args, target)) {
            return;
        }
        Object data = extractData(returnValue, method, args, target);
        if (data == null) {
            return;
        }
        try {
            encryptValue(data, new SecureIdContext());
        } catch (Exception e) {
            LOGGER.warn("secure id response encrypt skipped, method={}", method.toGenericString(), e);
        }
    }

    /**
     * 从返回值中提取真正需要加密的数据。
     * <p>
     * 比如某些项目统一返回 BaseResponse，则子类可以在这里判断响应类型并返回 data；
     * 如果返回 null，表示当前返回值不需要处理。
     */
    protected abstract Object extractData(Object returnValue, Method method, Object[] args, @Nullable Object target);

    /**
     * 子类可在这里接管项目特有对象，如分页对象、树结构、特殊 wrapper。
     * <p>
     * 这个扩展点位于父类内置 instanceof 分支之前：
     * 如果项目希望特殊处理 PageInfo、IPage、TreeNode、不可变 wrapper 等类型，
     * 可以在这里识别类型，并对其内部数据调用 {@link #encryptValue(Object, SecureIdContext)}。
     * 例如分页对象通常不是直接处理分页对象本身，而是取出 records/list/datas 后继续递归。
     * <p>
     * 传入的 context 已经是当前对象的下一层上下文，子类处理内部数据时直接使用即可。
     * 返回 true 表示子类已经完整处理，父类不会再执行 Map、Iterable、数组、普通对象字段遍历。
     *
     * @return true 表示子类已经完成处理，父类不再继续通用遍历。
     */
    protected abstract boolean encryptSpecialObject(Object value, SecureIdContext context);

    /**
     * 响应加密跳过 URL。
     * <p>
     * 支持 Ant 风格路径，如 /static/**、/api/no-secure-id/**。
     * 业务项目如果没有跳过 URL，也需要显式返回空集合，避免默认行为隐藏配置边界。
     */
    protected abstract Set<String> skipUrls();

    /**
     * 项目常见外键字段名。
     * <p>
     * 业务项目如果不按字段名约定处理，也需要显式返回空集合。
     */
    protected abstract Set<String> commonIdFieldNames();

    /**
     * 最大递归深度。
     * <p>
     * 建议默认值为 4，由子类明确返回，避免基础组件写死项目策略。
     */
    protected abstract int maxDepth();

    /**
     * 方法、URL、请求上下文等跳过规则的扩展点。
     */
    protected boolean shouldSkip(Method method, Object[] args, @Nullable Object target) {
        SkipIdSecure skipIdSecure = method.getAnnotation(SkipIdSecure.class);
        if (skipIdSecure != null && skipIdSecure.value()) {
            return true;
        }
        return shouldSkipCurrentUrl();
    }

    protected boolean shouldSkipCurrentUrl() {
        Set<String> skipUrls = skipUrls();
        if (skipUrls.isEmpty()) {
            return false;
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return false;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        String requestUri = servletRequestAttributes.getRequest().getRequestURI();
        String contextPath = servletRequestAttributes.getRequest().getContextPath();
        String path = requestUri;
        if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            path = requestUri.substring(contextPath.length());
        }
        for (String skipUrl : skipUrls) {
            if (PATH_MATCHER.match(skipUrl, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通用递归处理入口。
     * <p>
     * 处理顺序很重要：
     * 1. 空值、简单类型、超深、循环引用直接跳过；
     * 2. 记录当前对象引用；
     * 3. 子类特殊对象优先接管，如分页对象；
     * 3. IdEncryptable 先处理自身 id；
     * 4. 再按 Map、Iterable、数组、普通对象字段继续展开。
     * <p>
     * 这里的 instanceof 分支是父类已经确认的通用结构。
     * 如果子类要增加分页、树、项目 wrapper，不需要改这些分支，覆写 encryptSpecialObject 即可。
     */
    protected void encryptValue(@Nullable Object value, SecureIdContext context) {
        if (value == null || context.depth() > maxDepth() || SecureIdClassUtils.isSimpleValue(value.getClass())) {
            return;
        }
        if (context.isVisited(value)) {
            return;
        }
        // 记录当前对象引用，防止 A.children -> B.parent -> A 这类循环引用导致无限递归。
        context.visit(value);
        try {
            SecureIdContext childContext = context.nextDepth();
            if (encryptSpecialObject(value, childContext)) {
                return;
            }
            if (value instanceof IdEncryptable) {
                IdEncryptable idEncryptable = (IdEncryptable) value;
                idEncryptable.setId(IdUtil.encrypt(idEncryptable.getId()));
            }
            if (value instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) value;
                encryptMap(map, childContext);
                return;
            }
            if (value instanceof Iterable<?>) {
                Iterable<?> iterable = (Iterable<?>) value;
                encryptIterable(iterable, childContext);
                return;
            }
            if (value.getClass().isArray()) {
                encryptArray(value, childContext);
                return;
            }
            encryptObjectFields(value, childContext);
        } finally {
            context.leave(value);
        }
    }

    /**
     * Map 默认只递归处理 value。
     * <p>
     * 如果 key 命中 commonIdFieldNames 且 value 是 String，则认为这是一个业务约定的 ID 字段并直接加密。
     * 如果 key 命中但 value 不是 String，则继续递归 value，避免漏掉 Map value 对象内部的 ID 字段。
     * Map key 本身默认不加密。
     */
    protected void encryptMap(Map<?, ?> map, SecureIdContext context) {
        Set<String> commonIdFieldNames = commonIdFieldNames();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object mapValue = entry.getValue();
            if (entry.getKey() instanceof String && commonIdFieldNames.contains(entry.getKey())) {
                String key = (String) entry.getKey();
                if (encryptMapIdValue(map, key, mapValue)) {
                    continue;
                }
            }
            encryptValue(mapValue, context);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean encryptMapIdValue(Map map, String key, @Nullable Object value) {
        if (value instanceof String) {
            String stringValue = (String) value;
            map.put(key, IdUtil.encrypt(stringValue));
            return true;
        }
        return false;
    }

    /**
     * 集合元素逐个递归。
     */
    protected void encryptIterable(Iterable<?> iterable, SecureIdContext context) {
        for (Object item : iterable) {
            encryptValue(item, context);
        }
    }

    /**
     * 数组元素逐个递归，兼容对象数组和基础类型数组。
     */
    protected void encryptArray(Object array, SecureIdContext context) {
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            encryptValue(java.lang.reflect.Array.get(array, i), context);
        }
    }

    /**
     * 普通对象字段遍历。
     * <p>
     * 字段处理分两类：
     * 1. @SecureId 或 commonIdFieldNames 命中的字段，按 ID 字段加密；
     * 2. 其他字段继续递归展开。
     * <p>
     * 字段元数据从缓存中读取，避免对大量同类型对象重复调用反射扫描、注解判断和字段名判断。
     */
    protected void encryptObjectFields(Object value, SecureIdContext context) {
        for (SecureIdFieldMetadata metadata : getFieldMetadata(value.getClass())) {
            encryptField(value, metadata, context);
        }
    }

    /**
     * 获取某个 Class 的字段处理元数据。
     * <p>
     * @param type 正在处理的响应对象类型，不是字段类型。
     * @return 该类型及其父类中可处理字段的元数据。
     * <p>
     * 这里会使用缓存；同一个 Class 第一次处理时构建元数据，后续直接复用。
     */
    protected List<SecureIdFieldMetadata> getFieldMetadata(Class<?> type) {
        return fieldMetadataCache.computeIfAbsent(type, this::buildFieldMetadata);
    }

    /**
     * 构建某个 Class 的字段处理元数据。
     * <p>
     * @param type 正在处理的响应对象类型。
     * @return 已经提取好的字段处理元数据。
     * <p>
     * 这里不仅提取“需要加密的字段”，还会提取“需要继续递归展开的字段”：
     * 1. 命中 @SecureId 或 commonIdFieldNames 的字段，标记为 ID 加密字段；
     * 2. 其他可处理字段，标记为递归字段；
     * 3. static/transient 字段由 shouldProcessField 过滤掉。
     */
    private List<SecureIdFieldMetadata> buildFieldMetadata(Class<?> type) {
        List<SecureIdFieldMetadata> fields = new ArrayList<>();
        ReflectionUtils.doWithFields(type, field -> {
            ReflectionUtils.makeAccessible(field);
            fields.add(new SecureIdFieldMetadata(field, shouldEncryptField(field)));
        }, this::shouldProcessField);
        return Collections.unmodifiableList(fields);
    }

    /**
     * 静态字段和 transient 字段不处理，避免修改类级别状态或临时字段。
     */
    protected boolean shouldProcessField(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

    /**
     * 单字段处理。
     * <p>
     * 明确标记为 ID 字段时只做字段加密，不再继续递归；
     * 普通字段则继续递归处理其内部对象。
     * <p>
     * 字段读写失败是局部失败：只跳过当前字段，继续处理同对象的其他字段。
     */
    protected void encryptField(Object target, SecureIdFieldMetadata metadata, SecureIdContext context) {
        Field field = metadata.field();
        try {
            Object fieldValue = field.get(target);
            if (metadata.encryptField()) {
                encryptIdField(target, field, fieldValue);
                return;
            }
            encryptValue(fieldValue, context);
        } catch (Exception e) {
            LOGGER.debug("secure id field skipped, type={}, field={}", target.getClass().getName(), field.getName(), e);
        }
    }

    /**
     * 判断字段是否按 ID 字段处理。
     * <p>
     *  @SecureId 是显式规则；commonIdFieldNames 是项目约定规则。
     */
    protected boolean shouldEncryptField(Field field) {
        SecureId secureId = field.getAnnotation(SecureId.class);
        if (secureId != null) {
            return secureId.value();
        }
        return commonIdFieldNames().contains(field.getName());
    }

    /**
     * ID 字段加密目前只支持 String。
     * <p>
     * 其他类型先保留原值并记录日志，避免隐式类型转换破坏业务对象。
     */
    protected void encryptIdField(Object target, Field field, @Nullable Object fieldValue) throws IllegalAccessException {
        if (fieldValue == null) {
            return;
        }
        if (fieldValue instanceof String) {
            String stringValue = (String) fieldValue;
            field.set(target, IdUtil.encrypt(stringValue));
            return;
        }
        LOGGER.debug("secure id field skipped, type={}, field={}, fieldType={}",
                target.getClass().getName(), field.getName(), fieldValue.getClass().getName());
    }

}
