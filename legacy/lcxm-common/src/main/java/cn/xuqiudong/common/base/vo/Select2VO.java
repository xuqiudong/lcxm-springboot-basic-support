package cn.xuqiudong.common.base.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明 :  对应select2的插件的data数据
 *
 * @author Vic.xu
 * @since 2019年12月3日 上午11:29:07
 */
public class Select2VO {

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
     * 额外的信息
     */
    private Object additional;


    /**
     * @param id   id
     * @param text text
     */
    public Select2VO(Object id, String text) {
        this();
        this.id = id;
        this.text = text;
    }

    /**
     *
     */
    public Select2VO() {
        super();
        this.children = new ArrayList<>();

    }

    /**
     * 子选项
     */
    private List<Select2VO> children;

    public Object getId() {
        return id;
    }

    /**
     * 给前端多返回一个  code
     */
    public Object getCode() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Select2VO> getChildren() {
        return children;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setChildren(List<Select2VO> children) {
        this.children = children;
    }

    public Object getPid() {
        return pid;
    }

    public void setPid(Object pid) {
        this.pid = pid;
    }

    public Object getAdditional() {
        return additional;
    }

    public void setAdditional(Object additional) {
        this.additional = additional;
    }

    @Override
    public String toString() {
        return "Select2VO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", pid=" + pid +
                ", additional=" + additional +
                ", children=" + children +
                '}';
    }
}
