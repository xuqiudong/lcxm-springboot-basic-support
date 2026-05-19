package cn.xuqiudong.basic.core.util;

import cn.xuqiudong.basic.core.model.SelectOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Description:
 * 把一些 List对象转为  List<SelectOption>
 *
 * @author Vic.xu
 * @since 2026-05-19 10:41
 */
public class SelectOptionBuilder {

    /**
     * 通用转换：任意List<实体> → List<SelectOption>
     *
     * @param sourceList 源数据列表
     * @param idMapper   如何获取 id（如 BlogCategory::getId）
     * @param textMapper 如何获取 text（如 BlogCategory::getName）
     * @return 转换后的 SelectOption 集合
     */
    public static <T> List<SelectOption> convertToSelectOptions(
            List<T> sourceList,
            Function<T, Object> idMapper,
            Function<T, String> textMapper
    ) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }

        List<SelectOption> options = new ArrayList<>();
        for (T source : sourceList) {
            SelectOption option = new SelectOption();
            option.setId(idMapper.apply(source));
            option.setText(textMapper.apply(source));
            options.add(option);
        }
        return options;
    }

    /**
     * 通用构建树形SelectOption 自动组装children
     *
     * @param sourceList 源集合
     * @param idFunc     取id
     * @param textFunc   取展示文本
     * @param pidFunc    取父id
     * @param rootPid    根节点父id
     * @return 树形结构
     */
    public static <T> List<SelectOption> buildTreeSelect(
            List<T> sourceList,
            Function<T, Object> idFunc,
            Function<T, String> textFunc,
            Function<T, Object> pidFunc,
            Object rootPid
    ) {
        List<SelectOption> allList = new ArrayList<>();
        Map<Object, SelectOption> idMap = new HashMap<>();

        // 遍历转换
        if (sourceList != null) {
            for (T t : sourceList) {
                SelectOption option = new SelectOption();
                option.setId(idFunc.apply(t));
                option.setText(textFunc.apply(t));
                option.setPid(pidFunc.apply(t));
                allList.add(option);
                idMap.put(option.getId(), option);
            }
        }

        // 组装父子
        List<SelectOption> rootList = new ArrayList<>();
        for (SelectOption item : allList) {
            Object pid = item.getPid();
            if (rootPid.equals(pid)) {
                rootList.add(item);
            } else {
                SelectOption parent = idMap.get(pid);
                if (parent != null) {
                    parent.addChild(item);
                }
            }
        }
        return rootList;
    }

    /**
     * 普通扁平转换 不带树形
     */
    public static <T> List<SelectOption> buildFlatSelect(
            List<T> sourceList,
            Function<T, Object> idFunc,
            Function<T, String> textFunc
    ) {
        List<SelectOption> result = new ArrayList<>();
        if (sourceList == null) {
            return result;
        }
        for (T t : sourceList) {
            SelectOption option = new SelectOption();
            option.setId(idFunc.apply(t));
            option.setText(textFunc.apply(t));
            result.add(option);
        }
        return result;
    }
}
