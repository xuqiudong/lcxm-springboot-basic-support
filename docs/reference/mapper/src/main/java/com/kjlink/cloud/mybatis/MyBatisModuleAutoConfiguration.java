package com.kjlink.cloud.mybatis;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusInnerInterceptorAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;

import com.kjlink.cloud.mybatis.dataperm.DataPermissionSqlResolver;
import com.kjlink.cloud.mybatis.dataperm.TableDataPermissionInterceptor;
import com.kjlink.cloud.mybatis.injector.DefaultSqlInjectorPlus;
import com.kjlink.cloud.mybatis.interceptor.TrashInnerInterceptor;
import com.kjlink.cloud.mybatis.interceptor.TrashPluginProperties;
import com.kjlink.cloud.mybatis.interceptor.TrashPluginTableNameCondition;
import com.kjlink.cloud.mybatis.legacy.LegacyEntityMetaPropertyHandler;
import com.kjlink.cloud.mybatis.meta.BaseBusinessEntityMetaPropertyHandler;
import com.kjlink.cloud.mybatis.meta.BaseEntityMetaPropertyHandler;
import com.kjlink.cloud.mybatis.meta.MetaPropertyHandler;
import com.kjlink.cloud.mybatis.meta.MetaPropertyHandlerComposite;
import com.kjlink.cloud.mybatis.query.ILikeBuilder;
import com.kjlink.cloud.mybatis.tsid.TsIdentifierGenerator;

/**
 * SpringBoot自动装配
 *
 * @author Fulai
 * @since 2024-01-18
 */
@AutoConfiguration(before = MybatisPlusInnerInterceptorAutoConfiguration.class)
@EnableConfigurationProperties(TrashPluginProperties.class)
public class MyBatisModuleAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(MyBatisModuleAutoConfiguration.class);

    @Bean
    public static TsIdentifierGenerator tsIdentifierGenerator() {
        return TsIdentifierGenerator.INSTANCE;
    }

    /**
     * BaseEntity属性填充
     *
     * @return
     */
    @Bean
    public BaseEntityMetaPropertyHandler baseEntityMetaPropertyHandler() {
        return new BaseEntityMetaPropertyHandler();
    }

    /**
     * BaseBusinessEntity属性填充
     *
     * @return
     */
    @Bean
    public BaseBusinessEntityMetaPropertyHandler baseBusinessEntityMetaPropertyHandler() {
        return new BaseBusinessEntityMetaPropertyHandler();
    }

    /**
     * 兼容历史项目
     *
     * @return
     */
    @Deprecated
    @Bean
    public LegacyEntityMetaPropertyHandler longEntityMetaPropertyHandler() {
        return new LegacyEntityMetaPropertyHandler();
    }

    /**
     * 扩展了Mybatis Plus的MetaObjectHandler，支持多个组合
     *
     * @param propertyHandlerObjectProvider
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaPropertyHandlerComposite metaPropertyHandlerComposite(
            ObjectProvider<MetaPropertyHandler> propertyHandlerObjectProvider) {
        List<MetaPropertyHandler> propertyHandlers = new ArrayList<>();
        for (MetaPropertyHandler handler : propertyHandlerObjectProvider) {
            LOG.debug("启用MyBatis实体属性注入插件: {}", handler.getClass().getSimpleName());
            propertyHandlers.add(handler);
        }
        return new MetaPropertyHandlerComposite(propertyHandlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(List<InnerInterceptor> innerInterceptors) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        for (InnerInterceptor innerInterceptor : innerInterceptors) {
            LOG.debug("启用MyBatisPlus插件: {}", innerInterceptor.getClass().getSimpleName());
            interceptor.addInnerInterceptor(innerInterceptor);
        }
        return interceptor;
    }

    /**
     * 注册分页插件
     *
     * @return
     */
    @Order(100)
    @Bean
    @ConditionalOnBean(DataSourceProperties.class)
    @ConditionalOnMissingBean
    public PaginationInnerInterceptor paginationInnerInterceptor(DataSourceProperties dataSourceProperties) {
        DatabaseDriver driver = DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl());
        if (driver == DatabaseDriver.MYSQL) {
            return new PaginationInnerInterceptor(DbType.MYSQL);
        } else if (driver == DatabaseDriver.ORACLE) {
            return new PaginationInnerInterceptor(DbType.ORACLE);
        } else if (driver == DatabaseDriver.POSTGRESQL) {
            return new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        } else if (driver == DatabaseDriver.SQLSERVER) {
            return new PaginationInnerInterceptor(DbType.SQL_SERVER);
        }
        throw new IllegalStateException(StrUtil.format("未实现数据库{}的分页插件", driver));
    }

    @Bean
    @ConditionalOnBean(DataSourceProperties.class)
    @ConditionalOnMissingBean
    public ILikeBuilder iLikeBuilder(DataSourceProperties dataSourceProperties) {
        DatabaseDriver driver = DatabaseDriver.fromJdbcUrl(dataSourceProperties.getUrl());
        if (driver == DatabaseDriver.MYSQL) {
            return ILikeBuilder.MYSQL;
        } else if (driver == DatabaseDriver.ORACLE) {
            return ILikeBuilder.ORACLE;
        } else if (driver == DatabaseDriver.POSTGRESQL) {
            return ILikeBuilder.POSTGRE_SQL;
        }
        throw new IllegalStateException(StrUtil.format("未实现数据库{}的ilike插件", driver));
    }

    /**
     * 删除日志插件，因为要额外创建表，为方便开发，改成手动启用
     * 生产环境统一开启
     *
     * @return
     */
    @Order(200)
    @Bean
    @ConditionalOnBean(DataSourceProperties.class)
    @ConditionalOnMissingBean
    @Conditional(TrashPluginTableNameCondition.class)
    public TrashInnerInterceptor trashInnerInterceptor(TrashPluginProperties properties) {
        TrashInnerInterceptor interceptor = new TrashInnerInterceptor();
        interceptor.setProperties(properties);
        return interceptor;
    }

    /**
     * 扩展MybatisPlus的Mapper方法注入
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultSqlInjectorPlus defaultSqlInjectorPlus() {
        return new DefaultSqlInjectorPlus();
    }


    @Order(-100)
    @Bean
    @ConditionalOnSingleCandidate(DataPermissionSqlResolver.class)
    public TableDataPermissionInterceptor tableDataPermissionInterceptor(DataPermissionSqlResolver resolver) {
        LOG.debug("发现DataPermissionSqlResolver自动注册数据权限拦截器");
        return new TableDataPermissionInterceptor(resolver);
    }
}
