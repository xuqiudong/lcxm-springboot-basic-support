package cn.xuqiudong.common.fill;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 描述:
 * mybatis-plus 更新和插入字段填充处理  <br/>
 *  mp只支持一个, 所以这里新增一个接口, 区分于 MetaObjectHandler, 然后在实现类中添加多个实现类
 * @author Vic.xu
 * @see MetaObjectHandler
 * @since 2025-09-10 14:25
 */
public interface AutoFillFieldHandler {

    /**
     * 插入填充
     */
    void insertFill(MetaObject metaObject);

    /**
     * 更新填充
     */
    void updateFill(MetaObject metaObject);
}
