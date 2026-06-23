package cn.xuqiudong.basic.framework.code2text.enrich;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.framework.code2text.helper.Code2TextHelper;
import cn.xuqiudong.basic.framework.code2text.type.Code2TextType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * entity 转 dto : 通过字段上的Code2TextEnrich  填充数据
 *
 * @author Vic.xu
 * @since 2026-06-22 21:15
 */
public class Code2TextEnrichHelper {

    /**
     * 填充DTO字段
     */
    public static <S, T> T enrichObject(S source, Class<T> dtoClass) {
        if (source == null || dtoClass == null) {
            return null;
        }
        T dto = BeanUtil.copyProperties(source, dtoClass);
        enricherField(dto, dtoClass);
        return dto;
    }

    /**
     * 批量填充DTO字段
     */
    public static <S, T> List<T> enrichList(List<S> source, Class<T> dtoClass) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>();
        for (S s : source) {
            result.add(enrichObject(s, dtoClass));
        }
        return result;
    }
    /**
     * 填充 page DTO字段
     */
    public static <S, T>PageInfo<T> enrichPage(PageInfo<S> pageInfo, Class<T> dtoClass) {
        if (pageInfo == null) {
            return null;
        }
        PageInfo<T> dtoPageInfo = pageInfo.convertWithoutData();
        List<S> datas = pageInfo.getDatas();
        if (CollectionUtils.isEmpty(datas)) {
            return dtoPageInfo;
        }
        List<T> dtoDatas = enrichList(datas, dtoClass);
        dtoPageInfo.setDatas(dtoDatas);
        return dtoPageInfo;
    }


    /**
     * 根据 Code2TextEnrich 注解 填充DTO字段
     */
    public static <T> T enricherField(T dto, Class<T> dtoClass) {
        if (dto == null || dtoClass == null) {
            return dto;
        }
        List<EnrichFieldModel> enricherFieldModels = EnrichFieldMetaBuilder.getEnricherFieldModels(dtoClass);
        if (CollectionUtils.isEmpty(enricherFieldModels)) {
            return dto;
        }
        for (EnrichFieldModel model : enricherFieldModels) {
            PropDesc targetProp = model.getPropDesc();
            PropDesc fromProp = model.getFromPropDesc();
            //对应解析器类型
            Class<? extends Code2TextType> type = model.getCode2TextEnrich().type();
            if (targetProp == null || fromProp == null || type == null) {
                continue;
            }
            Object value = fromProp.getValue(dto);
            if (value == null) {
                continue;
            }
            String text = Code2TextHelper.getText(type, String.valueOf(value));
            // 找不到文本时返回原值
            if (text == null && model.isFallbackToRaw()) {
                targetProp.setValue(dto, String.valueOf(value));
                continue;
            }
            targetProp.setValue(dto, text);
        }
        return dto;
    }

}
