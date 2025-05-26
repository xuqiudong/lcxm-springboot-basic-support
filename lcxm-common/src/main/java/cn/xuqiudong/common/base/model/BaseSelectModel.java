package cn.xuqiudong.common.base.model;

import java.io.Serializable;

/**
 * 通用下拉框对象
 *
 * @author Vic.xu
 * @since 2021/08/23
 */
public class BaseSelectModel implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 下拉框的值 可能是id code等
     */
    private Object code;

    /**
     * 下拉框显示的字段
     */
    private String text;

    /**
     * 是否选中
     */
    private boolean selected;

    /**
     * @param code
     * @param text
     */
    public BaseSelectModel(Object code, String text) {
        super();
        this.code = code;
        this.text = text;
    }

    public BaseSelectModel() {
        super();
    }

    /**
     * @return the code
     */
    public Object getCode() {
        return code;
    }

    /**给前端多返回一个id*/
    public Object getId() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(Object code) {
        this.code = code;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected
     *            the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
