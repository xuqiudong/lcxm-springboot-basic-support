package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.config.IConfigBuilder;
import cn.xuqiudong.basic.generator.constant.GeneratorConstant;
import lombok.Getter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 描述:
 * 实体类模板 配置
 *
 * @author Vic.xu
 * @since 2025-09-13 13:40
 */
@Getter
public class EntityTemplateConfig extends BaseTemplateConfig {

    /**
     * 实体中忽略的表字段
     */
    private Set<String> ignoreColumns;

    /**
     * 父类的全部字段
     */
    private Set<String> supperClassFields;


    /**
     * 所在包: 默认 entity 包
     * 模板路径 默认 /templates/entity.java
     */
    public EntityTemplateConfig() {
        super(GeneratorConstant.PACKAGE_ENTITY, GeneratorConstant.TEMPLATE_ENTITY_JAVA);
    }

    /**
     * 字段是否会被忽略 : 在父类中存在
     */
    public boolean isFieldIgnored(String fieldName) {
        return this.supperClassFields != null &&
                this.supperClassFields.stream()
                        .anyMatch(ignoreCol -> ignoreCol.equalsIgnoreCase(fieldName));
    }

    /**
     * 表字段是否会被忽略: 在指定的忽略的列中
     */
    public boolean isTableColumnIgnored(String columnName) {
        return this.ignoreColumns != null &&
                this.ignoreColumns.stream()
                        .anyMatch(ignoreCol -> ignoreCol.equalsIgnoreCase(columnName));
    }

    /**
     * 父类名称不为空时, 获取父类Class, 并初始化父类字段
     */
    private void initSupperClassFieldsFromSupperClassName() {
        if (this.supperClassName != null) {
            try {
                this.supperClass = Class.forName(this.supperClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(supperClassName + " not found");
            }
            initSupperClassFields();
        }
    }


    /**
     * 初始化父类字段
     */
    private void initSupperClassFields() {
        if (this.supperClass == null) {
            return;
        }
        if (this.supperClassFields == null) {
            this.supperClassFields = new HashSet<>();
        }
        // 使用doWithFields遍历所有字段（含父类），但添加过滤条件
        ReflectionUtils.doWithFields(supperClass, field -> {
            // 只添加非静态字段
            if (!Modifier.isStatic(field.getModifiers())) {
                supperClassFields.add(field.getName());
            }
        });
    }

    @Override
    public String getFileSuffix() {
        return GeneratorConstant.JAVA_SUFFIX;
    }

    @Override
    public void afterPropertySet() {
        if (this.supperClassName != null) {
            initSupperClassFieldsFromSupperClassName();
            return;
        }
        initSupperClassFields();
    }


    /**
     * Entity  模板配置  构建器
     */
    public static class Builder extends BaseConfigBuilder<Builder, EntityTemplateConfig> implements IConfigBuilder<EntityTemplateConfig> {

        public Builder() {
            super(new EntityTemplateConfig());
        }

        @Override
        public Builder templatePath(String templatePath) {
            config.templatePath = templatePath;
            return this;
        }

        /**
         * 忽略的表字段
         */
        public Builder ignoreColumns(String... ignoreColumns) {
            if (config.ignoreColumns == null) {
                config.ignoreColumns = new HashSet<>();
            }
            config.ignoreColumns.addAll(Arrays.asList(ignoreColumns));
            return this;
        }
    }
}
