package cn.xuqiudong.basic.generator.dialect.keywords;

import java.util.Locale;
import java.util.Set;

/**
 * 描述:
 * 关键字处理器 基类
 *
 * @author Vic.xu
 * @since 2025-09-11 17:51
 */
public abstract class BaseKeyWordsHandler {

    private Set<String> keywordsSet;


    public BaseKeyWordsHandler(Set<String> keywordsSet) {
        this.keywordsSet = keywordsSet;
    }

    /**
     * 获取关键字
     */
    public Set<String> getKeyWords() {
        return keywordsSet;
    }

    /**
     * 格式化关键字格式
     */
    public abstract String formatStyle();


    /**
     * 格式化字段
     */
    public String formatColumn(String columnName) {
        return String.format(formatStyle(), columnName);
    }

    /**
     * 是否为关键字
     */
    public boolean isKeyWords(String columnName) {
        return getKeyWords().contains(columnName.toUpperCase(Locale.ENGLISH));
    }
}
