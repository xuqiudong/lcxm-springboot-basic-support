package cn.xuqiudong.basic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Description:
 * id为Long 类型的mybatis-plus 基类entity
 *
 * @author Vic.xu
 * @see BaseStringIdEntity
 * @since 2026-09-09 16:23
 */
public class BaseLongAutoIdEntity extends BaseMpEntity<Long> {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;
}
