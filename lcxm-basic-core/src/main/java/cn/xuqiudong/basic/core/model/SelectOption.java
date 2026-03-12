package cn.xuqiudong.basic.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 用于前端 下拉框 选项的数据
 *
 * @author Vic.xu
 * @since 2025-11-13 16:57
 */
@Data
public class SelectOption implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选项的值
     */
    private Object id;
    /**
     * 选项的展示
     */
    private String text;

    /**
     * 父项
     */
    private Object pid;

    /**
     * 子选项
     */
    private List<SelectOption> children;

    /**
     * 额外的信息
     */
    private Object additional;

    @JsonIgnore
    private int sort;


    public SelectOption() {
        this.children = new ArrayList<>();
    }


    public SelectOption(Object id, String text) {
        this();
        this.id = id;
        this.text = text;
    }

    public boolean isHasChildren() {
        return children != null && children.size() > 0;
    }

    public void addChild(SelectOption child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

}
