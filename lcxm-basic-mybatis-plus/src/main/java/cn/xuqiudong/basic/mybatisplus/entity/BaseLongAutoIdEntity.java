package cn.xuqiudong.basic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Description:
 * id为Long 类型的mybatis-plus 基类entity
 *
 * @author Vic.xu
 * @see BaseStringIdEntity
 * @since 2026-09-09 16:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseLongAutoIdEntity extends BaseMpEntity<Long> {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @SuppressFBWarnings(value = "MF_CLASS_MASKS_FIELD",
            justification = "Concrete id type is required so MyBatis can select the correct TypeHandler "
                    + "when id is null.")
    protected Long id;
}
