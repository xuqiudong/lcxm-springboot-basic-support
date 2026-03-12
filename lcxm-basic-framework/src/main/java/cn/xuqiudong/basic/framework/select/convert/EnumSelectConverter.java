package cn.xuqiudong.basic.framework.select.convert;

import cn.xuqiudong.basic.core.model.SelectOption;
import cn.xuqiudong.basic.framework.select.EnumSelectable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述:
 *  枚举转下拉选项的工具类（带缓存）
 * @author Vic.xu
 * @since 2025-11-13 17:05
 */
public class EnumSelectConverter {

    /**
     * 缓存：枚举类 -> 转换后的下拉列表（按sort排序）
     */
    private static final Map<Class<? extends EnumSelectable>, List<SelectOption>> CACHE = new ConcurrentHashMap<>();

    /**
     * 将枚举类转换为下拉选项列表
     * @param enumClass 实现EnumSelectable的枚举类
     * @return 排序后的下拉选项列表
     */
    public static List<SelectOption> convert(Class<? extends EnumSelectable> enumClass) {
        // 校验枚举合法性
        validate(enumClass);

        // 缓存中获取，无则创建并缓存
        return CACHE.computeIfAbsent(enumClass, clazz -> {
            List<SelectOption> options = new ArrayList<>();
            // 遍历枚举常量，转换为SelectOption
            for (EnumSelectable enumConstant : clazz.getEnumConstants()) {
                SelectOption option = new SelectOption();
                option.setId(enumConstant.getValue());
                option.setText(enumConstant.getText());
                option.setSort(enumConstant.getSort());
                options.add(option);
            }
            // 按sort排序（相同sort则按枚举定义顺序）
            options.sort(Comparator.comparingInt(SelectOption::getSort));
            return options;
        });
    }

    /**
     * 清除缓存（用于开发环境热重载）
     */
    public static void clearCache() {
        CACHE.clear();
    }

    // 校验枚举类是否合法
    private static void validate(Class<? extends EnumSelectable> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("枚举类不能为空");
        }
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("类[" + enumClass.getName() + "]不是枚举");
        }
        if (!EnumSelectable.class.isAssignableFrom(enumClass)) {
            throw new IllegalArgumentException("枚举[" + enumClass.getName() + "]未实现EnumSelectable接口");
        }
    }

}
