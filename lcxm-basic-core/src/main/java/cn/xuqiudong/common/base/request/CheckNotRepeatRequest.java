package cn.xuqiudong.common.base.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述:
 *    判断字段值在数据库是否重复 的请求参数
 * @author Vic.xu
 * @since 2025-10-31 17:44
 */
@Getter
@Setter
public class CheckNotRepeatRequest<T> implements BaseApiRequest{

    private static final long serialVersionUID = 1L;

    /**
     * ID may  null 如果不传 则判断表里的全部项,如果传了id,则排除当前id所对应的列
     */
    private T id;
    /**
     * column  table  column  列名称
     */
    private String column;
    /**
     * value 需要判断是否重复的列的值
     */
    private String value;



}
