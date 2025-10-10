package com.kjlink.cloud.mybatis.entity;

/**
 * desc
 *
 * @author hedd
 * @since 2023/8/2
 */
public enum Language {
    /**
     * 中文
     */
    ZH_CN("中文"),
    /**
     * 英文
     */
    EN_US("英文");
    private final String text;
    Language(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
