package cn.xuqiudong.basic.generator.config;

import cn.xuqiudong.basic.generator.config.template.ControllerTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.EntityTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.MapperTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.MapperXmlTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.QueryTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.ServiceTemplateConfig;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 描述:
 * 生成细节配置: 生成策略
 * <p>
 * 1.  要生成哪些表, 表的前缀
 * 2. 各个输出文件的配置, OutputFile
 * 2.1 entity 配置
 * 2.2 mapper 配置
 * 2.3 xml 配置
 * 2.4 service 配置
 * 2.5 controller 配置
 * 2.6 query 配置
 * </p>
 *
 * @author Vic.xu
 * @since 2025-09-12 16:40
 */
@Getter
public class StrategyConfig {

    /**
     * 要生成的表
     */
    private Set<String> tables;

    /**
     * 表前缀
     */
    private final Set<String> tablePrefix = new HashSet<>();

    /**
     * 是否覆盖文件
     */
    private boolean fileOverride;

    /**
     * entity 配置 builder
     */
    @Getter(AccessLevel.NONE)
    private final EntityTemplateConfig.Builder entityConfigBuilder = new EntityTemplateConfig.Builder();

    /**
     * entity 配置
     */
    private EntityTemplateConfig entityTemplateConfig;


    /**
     * query 配置 builder
     */
    @Getter(AccessLevel.NONE)
    private final QueryTemplateConfig.Builder queryConfigBuilder = new QueryTemplateConfig.Builder();

    /**
     * query 配置
     */
    private QueryTemplateConfig queryTemplateConfig;


    /**
     * mapper 配置 builder
     */
    @Getter(AccessLevel.NONE)
    private final MapperTemplateConfig.Builder mapperConfigBuilder = new MapperTemplateConfig.Builder();

    /**
     * mapper 配置
     */
    private MapperTemplateConfig mapperTemplateConfig;

    /**
     * xml 配置 builder
     */
    @Getter(AccessLevel.NONE)
    private final MapperXmlTemplateConfig.Builder xmlConfigBuilder = new MapperXmlTemplateConfig.Builder();
    /**
     * xml 配置
     */
    private MapperXmlTemplateConfig xmlTemplateConfig;

    /**
     * service 配置 builder
     */
    @Getter(AccessLevel.NONE)
    private final ServiceTemplateConfig.Builder serviceConfigBuilder = new ServiceTemplateConfig.Builder();

    private ServiceTemplateConfig serviceTemplateConfig;


    /**
     * controller 配置 builder
     */
    @Getter(AccessLevel.NONE)
    private final ControllerTemplateConfig.Builder controllerConfigBuilder = new ControllerTemplateConfig.Builder();
    /**
     * controller 配置
     */
    private ControllerTemplateConfig controllerTemplateConfig;

    /**
     * 配置属性
     */
    private void afterPropertySet() {
        this.entityTemplateConfig = this.entityConfigBuilder.build();
        this.queryTemplateConfig = this.queryConfigBuilder.build();
        this.mapperTemplateConfig = this.mapperConfigBuilder.build();
        this.xmlTemplateConfig = this.xmlConfigBuilder.build();
        this.serviceTemplateConfig = this.serviceConfigBuilder.build();
        this.controllerTemplateConfig = this.controllerConfigBuilder.build();
    }


    public static class Builder implements IConfigBuilder<StrategyConfig> {

        final StrategyConfig strategyConfig;

        public Builder() {
            this.strategyConfig = new StrategyConfig();
        }

        public Builder tables(String... tables) {
            Assert.notNull(tables, "必须要指定生成的表名");
            if (this.strategyConfig.tables == null) {
                strategyConfig.tables = new HashSet<>();
            }
            this.strategyConfig.tables.addAll(Arrays.asList(tables));
            return this;
        }

        public Builder tables(List<String> tableList) {
            Assert.notNull(tableList, "必须要指定生成的表名");
            if (this.strategyConfig.tables == null) {
                strategyConfig.tables = new HashSet<>();
            }
            this.strategyConfig.tables.addAll(tableList);
            return this;
        }

        /**
         * 指定表名前缀
         */
        public Builder tablePrefix(String... tablePrefix) {
            this.strategyConfig.tablePrefix.addAll(Arrays.asList(tablePrefix));
            return this;
        }

        public Builder tablePrefix(List<String>tablePrefix) {
            this.strategyConfig.tablePrefix.addAll(tablePrefix);
            return this;
        }

        /**
         * entity 配置
         */
        public Builder entityConfig(Consumer<EntityTemplateConfig.Builder> consumer) {
            consumer.accept(this.strategyConfig.entityConfigBuilder);
            return this;
        }

        /**
         * query 配置
         */
        public Builder queryConfig(Consumer<QueryTemplateConfig.Builder> consumer) {
            consumer.accept(this.strategyConfig.queryConfigBuilder);
            return this;
        }

        /**
         * mapper 配置
         */
        public Builder mapperConfig(Consumer<MapperTemplateConfig.Builder> consumer) {
            consumer.accept(this.strategyConfig.mapperConfigBuilder);
            return this;
        }

        public Builder xmlConfig(Consumer<MapperXmlTemplateConfig.Builder> consumer) {
            consumer.accept(this.strategyConfig.xmlConfigBuilder);
            return this;
        }

        public Builder serviceConfig(Consumer<ServiceTemplateConfig.Builder> consumer) {
            consumer.accept(this.strategyConfig.serviceConfigBuilder);
            return this;
        }

        public Builder controllerConfig(Consumer<ControllerTemplateConfig.Builder> consumer) {
            consumer.accept(this.strategyConfig.controllerConfigBuilder);
            return this;
        }

        public Builder fileOverride(boolean fileOverride) {
            this.strategyConfig.fileOverride = fileOverride;
            return this;
        }


        @Override
        public StrategyConfig build() {
            strategyConfig.afterPropertySet();
            return strategyConfig;
        }
    }
}
