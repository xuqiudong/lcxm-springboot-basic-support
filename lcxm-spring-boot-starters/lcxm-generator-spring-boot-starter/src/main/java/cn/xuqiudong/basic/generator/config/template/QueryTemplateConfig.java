package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.config.IConfigBuilder;
import cn.xuqiudong.basic.generator.constant.GeneratorConstant;
import cn.xuqiudong.basic.core.query.PageQuery;

/**
 * 描述:
 * Query 模板配置:  其实默认同 Entity 模板配置 只是修改下模板文件路径和包路径等， 然后写死一些默认属性
 *
 * @author Vic.xu
 * @see #afterPropertySet()
 * @since 2025-11-03 9:09
 */
public class QueryTemplateConfig extends BaseTemplateConfig {


    /**
     * 构造函数: 构件默认的 子包和模板路径
     */
    public QueryTemplateConfig() {
        super(GeneratorConstant.PACKAGE_QUERY, GeneratorConstant.TEMPLATE_QUERY_JAVA);
    }

    @Override
    public String getFileSuffix() {
        return GeneratorConstant.JAVA_SUFFIX;
    }

    @Override
    protected void afterPropertySet() {
        // query 写死一些属性
        supperClass = PageQuery.class;
        supperClassName = PageQuery.class.getName();
        supperClassWithGeneric = false;

    }


    public static class Builder extends BaseConfigBuilder<QueryTemplateConfig.Builder, QueryTemplateConfig> implements IConfigBuilder<QueryTemplateConfig> {
        public Builder() {
            super(new QueryTemplateConfig());
        }
    }
}
