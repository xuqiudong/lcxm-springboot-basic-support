package cn.xuqiudong.basic.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述:
 *   某些表可能没有公共字段 只有id
 * @author Vic.xu
 * @since 2025-11-07 17:32
 */
@Data
public class IdEntity <ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    protected ID id;
}
