package cn.xuqiudong.common;

import cn.xuqiudong.common.base.fill.CompositeAutoFillFieldHandler;
import cn.xuqiudong.common.base.fill.AutoFillFieldHandler;
import cn.xuqiudong.common.base.fill.impl.BaseMpEntityAutoFillFieldHandler;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * mybatis-plus 模块的自动装配入口
 *
 * @author Vic.xu
 * @since 2025-09-10 14:36
 */
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
@ConditionalOnExpression("${lcxm.basic.mp.enabled:true}")
public class LcxmMybatisPlusAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LcxmMybatisPlusAutoConfiguration.class);

    /**
     * BaseMpEntity 属性填充
     */
    @Bean
    public BaseMpEntityAutoFillFieldHandler baseMpEntityMetaPropertyHandler() {
        return new BaseMpEntityAutoFillFieldHandler();
    }

    /**
     * 扩展 mybatis-plus 的MetaObjectHandler 组合多个
     */
    @Bean
    public CompositeAutoFillFieldHandler compositeMetaFieldHandler(ObjectProvider<AutoFillFieldHandler> metaFieldHandlers) {
        List<AutoFillFieldHandler> handles = new ArrayList<>();
        for (AutoFillFieldHandler metaFieldHandler : metaFieldHandlers) {
            LOGGER.info("启用mybatis-plus实体自动填充字段: {}", metaFieldHandler.getClass().getSimpleName());
        }
        return new CompositeAutoFillFieldHandler(handles);
    }
}
