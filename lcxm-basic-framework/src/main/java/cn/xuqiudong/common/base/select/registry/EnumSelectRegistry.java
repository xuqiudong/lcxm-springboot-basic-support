package cn.xuqiudong.common.base.select.registry;

import cn.xuqiudong.common.base.model.SelectOption;
import cn.xuqiudong.common.base.select.EnumSelectable;
import cn.xuqiudong.common.base.select.convert.EnumSelectConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述:
 * 枚举注册中心，管理所有需要暴露的枚举
 *
 * @author Vic.xu
 * @since 2025-11-13 17:18
 */
public class EnumSelectRegistry {

    /**
     * 存储：枚举标识 -> 枚举类
     */
    private static final Map<String, Class<? extends EnumSelectable>> ENUM_MAP = new HashMap<>();
    /**
     * 存储：枚举标识 -> 枚举描述
     */
    private static final Map<String, String> DESC_MAP = new HashMap<>();

    /**
     * 注册枚举
     *
     * @param enumKey   枚举标识（前端访问用）
     * @param enumClass 枚举类
     * @param desc      枚举描述
     */
    public static void register(String enumKey, Class<? extends EnumSelectable> enumClass, String desc) {
        Assert.notNull(enumKey, "枚举标识不能为空");
        Objects.requireNonNull(enumClass, "枚举类不能为空");
        Assert.notNull(enumClass, "枚举不能为空");
        if (ENUM_MAP.containsKey(enumKey)) {
            throw new IllegalArgumentException("枚举标识[" + enumKey + "]已被注册");
        }
        ENUM_MAP.put(enumKey, enumClass);
        DESC_MAP.put(enumKey, desc);
    }

    /**
     * 根据标识获取枚举类
     */
    public static Class<? extends EnumSelectable> getEnumClass(String enumKey) {
        return ENUM_MAP.get(enumKey);
    }

    /**
     * 获取所有已注册的枚举标识
     */
    public static Map<String, String> getRegisteredEnums() {
        return new HashMap<>(DESC_MAP); // 返回副本，避免外部修改
    }

    public static Map<String, List<SelectOption>> getMultipleOptions(List<String> enumKeys) {
        Map<String, List<SelectOption>> optionsMap = new HashMap<>();
        if (CollectionUtils.isEmpty(enumKeys)) {
            return optionsMap;
        }
        for (String enumKey : enumKeys) {
            optionsMap.put(enumKey, EnumSelectConverter.convert(getEnumClass(enumKey)));
        }
        return optionsMap;
    }
}
