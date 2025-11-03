package cn.xuqiudong.common.autoconfigure;

import cn.xuqiudong.common.base.fill.AutoFillFieldHandler;
import cn.xuqiudong.common.base.fill.CompositeAutoFillFieldHandler;
import cn.xuqiudong.common.base.fill.impl.BaseMpEntityAutoFillFieldHandler;
import cn.xuqiudong.common.injector.LcxmDefaultSqlInjector;
import cn.xuqiudong.common.permission.MPDataPermissionHandler;
import cn.xuqiudong.common.util.MpDbTypeUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * mybatis (plus) 相关的一些自动装配
 *
 * @author Vic.xu
 * @since 2025-10-27 9:12
 */
@AutoConfiguration(before = MybatisPlusInterceptor.class)
@EnableConfigurationProperties(LcxmMybatisPlusProperties.class)
@ConditionalOnExpression("${lcxm.mp.enabled:true}")
public class LcxmMybatisPlusAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LcxmMybatisPlusAutoConfiguration.class);

    /**
     * BaseMpEntity 属性填充
     *
     * @see #compositeMetaFieldHandler(ObjectProvider)
     */
    @Bean
    public BaseMpEntityAutoFillFieldHandler baseMpEntityMetaPropertyHandler() {
        return new BaseMpEntityAutoFillFieldHandler();
    }

    /**
     * 扩展 mybatis-plus 的MetaObjectHandler 组合多个
     */
    @Bean
    @ConditionalOnMissingBean
    public CompositeAutoFillFieldHandler compositeMetaFieldHandler(ObjectProvider<AutoFillFieldHandler> metaFieldHandlers) {
        List<AutoFillFieldHandler> handles = new ArrayList<>();
        for (AutoFillFieldHandler metaFieldHandler : metaFieldHandlers) {
            LOGGER.info("启用mybatis-plus实体自动填充字段: {}", metaFieldHandler.getClass().getSimpleName());
            handles.add(metaFieldHandler);
        }
        return new CompositeAutoFillFieldHandler(handles);
    }

    /**
     * 自定义的 mybatis-plus 数据权限插件
     *
     * @see MPDataPermissionHandler
     */
    @Bean
    public DataPermissionInterceptor dataPermissionInterceptor() {
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        MPDataPermissionHandler handler = new MPDataPermissionHandler();
        dataPermissionInterceptor.setDataPermissionHandler(handler);
        return dataPermissionInterceptor;
    }

    /**
     * 注册mybatis-plus拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor addMybatisPlusInterceptor(ObjectProvider<InnerInterceptor> innerInterceptors) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInterceptor = null;
        for (InnerInterceptor innerInterceptor : innerInterceptors) {
            if (innerInterceptor instanceof PaginationInnerInterceptor) {
                paginationInterceptor = (PaginationInnerInterceptor) innerInterceptor;
                continue;
            }
            LOGGER.info("启用mybatis-plus拦截器: {}", innerInterceptor.getClass().getSimpleName());
            interceptor.addInnerInterceptor(innerInterceptor);
        }
        // 分页插件放在最后
        if (paginationInterceptor == null) {
            LOGGER.info("启用mybatis-plus分页插件");
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        }
        return interceptor;
    }

    /**
     * 分页 拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DataSourceProperties.class)
    public PaginationInnerInterceptor paginationInnerInterceptor(DataSourceProperties dataSourceProperties) {
        DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl());
        String driverName = databaseDriver.name();
        DbType dbType = MpDbTypeUtil.getDbType(driverName);
        return new PaginationInnerInterceptor(dbType);
    }

    /**
     *  扩展 mybatis-plus 的 方法注入
     * link <a href="https://baomidou.com/guides/sql-injector/">sql-injector</a>
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultSqlInjector defaultSqlInjector() {
        return new LcxmDefaultSqlInjector();
    }

}
