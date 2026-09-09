package cn.xuqiudong.basic.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Description:
 * id为String 类型的mybatis-plus 基类entity
 *
 * 拆分固定ID类型父类的核心目的：解决【泛型父类IdEntity<ID>带来的泛型擦除问题】
 * <p>
 * 原有方案问题回顾：
 * public class IdEntity<ID extends Serializable> {
 * protected ID id; // 编译后字节码：protected Serializable id;
 * }
 * 当MP批量saveOrUpdateBatch，新增实体id=null时：
 * MyBatis通过反射读取父类id字段 field.getType() → 拿到Serializable
 * javaType=Serializable，jdbcType=null，找不到TypeHandler，抛出异常
 * <p>
 *  当前方案解决原理：
 * 1. id字段不再使用泛型占位符ID，直接写死为 String
 * 字节码层面：protected String id; 反射field.getType()直接拿到String.class
 * 2. 批量预编译场景，即使id=null，MyBatis拿到真实javaType=String，配合jdbcType=VARCHAR
 * 可以正常匹配TypeHandler，不再退化到Serializable类型
 * 3. @TableId注解放在本层，只作用于继承本类的实体；
 *
 * @author Vic.xu
 * @since 2026-09-09 16:16
 */
@Data
public abstract class BaseStringIdEntity extends BaseMpEntity<String> {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    protected String id;
}
