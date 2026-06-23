package cn.xuqiudong.basic.framework.code2text.enrich;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *  构建转换结果dto 的包含 Code2TextEnrich 注解的情况， 并缓存
 * @author Vic.xu
 * @since 2026-06-23 9:21
 */
public class EnrichFieldMetaBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnrichFieldMetaBuilder.class);

    private static final Map<Class<?>, List<EnrichFieldModel>> ENRICHER_FIELD_MODEL_CACHE = new ConcurrentHashMap<>();




    /**
     * 获取DTO上带填充的字段模型
     */
    public static List<EnrichFieldModel> getEnricherFieldModels(Class<?> clazz) {
        return ENRICHER_FIELD_MODEL_CACHE.computeIfAbsent(clazz,
                EnrichFieldMetaBuilder::fetchEnricherFieldModels);
    }

    /**
     * 获取DTO上带填充的字段模型
     * <p>
     * 1. 不加Code2TextEnrich 注解的字段作为 source
     * 2. 加了Code2TextEnrich 注解的字段作为 target
     * 3. from 为空忽略
     * 4. from 对应的field找不到有而忽略
     * </p>
     */
    private static List<EnrichFieldModel> fetchEnricherFieldModels(Class<?> dtoClass) {
        Assert.notNull(dtoClass, "dtoClass can not be null");

        BeanDesc beanDesc = BeanUtil.getBeanDesc(dtoClass);
        // 未加Code2TextEnrich 注解的字段作为 source : fieldName -> PropDesc
        Map<String, PropDesc> sourceFieldMap = new HashMap<>();
        // 加了Code2TextEnrich 注解的字段作为 , 缺少 fromPropDesc
        List<EnrichFieldModel> incompleteEnrichFields = new ArrayList<>();

        // STEP 1: 获取  source 源 和 target 目标
        for (PropDesc propDesc : beanDesc.getProps()) {
            Field field = propDesc.getField();
            if (field == null) {
                continue;
            }
            sourceFieldMap.put(field.getName(), propDesc);

            if (field.isAnnotationPresent(Code2TextEnrich.class)) {
                EnrichFieldModel model =
                        new EnrichFieldModel(field.getAnnotation(Code2TextEnrich.class));
                model.setPropDesc(propDesc);
                incompleteEnrichFields.add(model);
            }
        }

        List<EnrichFieldModel> result = new ArrayList<>();

        // STEP 2: 再处理 enrich
        for (EnrichFieldModel model : incompleteEnrichFields) {
            String from = model.getFrom();
            if (StringUtils.isBlank(from)) {
                LOGGER.warn("[{}] from is blank", model.getPropDesc().getField().getName());
                continue;
            }
            // from 必须来自非 enrich 字段
            PropDesc fromProp = sourceFieldMap.get(from);
            if (fromProp == null) {
                LOGGER.warn("[{}] from field not found", from);
                continue;
            }
            model.setFromPropDesc(fromProp);
            // 只有完整 的model 才加入结果
            result.add(model);
        }

        return result;
    }
}
