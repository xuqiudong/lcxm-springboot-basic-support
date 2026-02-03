package cn.xuqiudong.basic.generator.config.template;

import cn.xuqiudong.basic.generator.config.IConfigBuilder;
import lombok.Getter;
import org.springframework.util.Assert;

/**
 * 描述:
 * 各个模板的详细配置 的基类
 *
 * @author Vic.xu
 * @since 2025-09-17 14:55
 */
@Getter
public abstract class BaseTemplateConfig {

    /**
     * 所在包:
     */
    protected String subPackage;

    /**
     * 模板路径  不带后缀的 如: /templates/entity.java , 后缀由具体的模板引擎提供
     */
    protected String templatePath;

    /**
     * 实体类父类
     */
    protected Class<?> supperClass;

    /**
     * 父类名称 全限定名, 可以反射出来
     */
    protected String supperClassName;

    /**
     * 实体类父类 是否带泛型: 取自主键类型
     */
    protected boolean supperClassWithGeneric = true;

    /**
     * 是否禁用: 禁用后, 该模板将不会被渲染
     */
    protected boolean disable;

    /**
     * 构造函数: 构件默认的 子包和模板路径
     *
     * @param subPackage   子包
     * @param templatePath 模板路径
     */
    public BaseTemplateConfig(String subPackage, String templatePath) {
        this.subPackage = subPackage;
        this.templatePath = templatePath;
    }

    /**
     * 生成的文件的后缀
     */
    public abstract String getFileSuffix();

    /**
     * 属性设置后, 可以对属性进行一些处理
     */
    protected abstract void afterPropertySet();


    /**
     * 各个类型模板配置构建器 的基类, 用于构件一些共用的属性
     *
     * @param <T> 具体的子类 建造器
     * @param <C> 模板配置
     */
    public static abstract class BaseConfigBuilder<T extends BaseConfigBuilder<T, C>, C extends BaseTemplateConfig> implements IConfigBuilder<C> {

        protected final C config;

        protected BaseConfigBuilder(C config) {
            this.config = config;
        }

        protected T self() {
            return (T) this;
        }

        /**
         * 设置模板所在子包
         */
        public T subPackage(String subPackage) {
            config.subPackage = subPackage;
            return self();
        }

        /**
         * 模板路径  不带后缀的 如: /templates/entity.java , 后缀由具体的模板引擎提供
         */
        public T templatePath(String templatePath) {
            config.templatePath = templatePath;
            return self();
        }

        /**
         * 设置实体类父类
         */
        public T supperClass(Class<?> supperClass) {
            Assert.isNull(config.supperClass, "已设置实体类父类Class, 无需额外设置父类名称");
            config.supperClass = supperClass;
            return self();
        }

        /**
         * 父类名称 全限定名, 可以反射出来
         */
        public T supperClassName(String supperClassName) {
            Assert.isNull(config.supperClass, "已设置实体类父类Class, 无需额外设置父类名称");
            config.supperClassName = supperClassName;
            return self();
        }

        /**
         * 父类是否带泛型:
         */
        public T supperClassWithGeneric(boolean supperClassWithGeneric) {
            config.supperClassWithGeneric = supperClassWithGeneric;
            return self();
        }

        /**
         * 是否禁用: 禁用后, 该模板将不会被渲染
         */
        public T disable(boolean disable) {
            config.disable = disable;
            return self();
        }

        @Override
        public C build() {
            config.afterPropertySet();
            return config;
        }
    }
}
