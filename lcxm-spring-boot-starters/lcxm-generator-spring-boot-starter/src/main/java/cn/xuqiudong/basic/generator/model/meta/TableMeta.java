package cn.xuqiudong.basic.generator.model.meta;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 * 表的元数据
 *
 * @author Vic.xu
 * @since 2025-09-11 10:00
 */
@Getter
@Setter
@ToString
public class TableMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表的名称
     */
    private String tableName;
    /**
     * 表的备注
     */
    private String comments;

    /**
     * 表引擎
     */
    private String engine;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    /* *************** ↑↑↑以上为需要查询出来的字段↑↑*********************************** */


}

