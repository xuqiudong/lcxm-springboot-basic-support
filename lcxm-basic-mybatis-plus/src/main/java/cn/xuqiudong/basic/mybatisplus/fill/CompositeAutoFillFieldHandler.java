package cn.xuqiudong.basic.mybatisplus.fill;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.List;

/**
 * 描述:
 *   mybatis-plus 默认字段填充处理: 持有多个MetaFieldHandler 分别做填充
 * @author Vic.xu
 * @since 2025-09-10 14:30
 */
public class CompositeAutoFillFieldHandler implements MetaObjectHandler {

    private List<AutoFillFieldHandler> metaFieldHandlers;

    public CompositeAutoFillFieldHandler(List<AutoFillFieldHandler> metaFieldHandlers) {
        this.metaFieldHandlers = metaFieldHandlers;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        for (AutoFillFieldHandler metaFieldHandler : metaFieldHandlers) {
            metaFieldHandler.insertFill(metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        for (AutoFillFieldHandler metaFieldHandler : metaFieldHandlers) {
            metaFieldHandler.updateFill(metaObject);
        }
    }
}
