package cn.xuqiudong.basic.generator.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述:
 * 管理全局配置、规则配置。支持 YAML/JSON/Java API。
 *
 * @author Vic.xu
 * @since 2025-09-11 10:06
 */
@Getter
@Setter
public class GlobalConfig {

    /**
     * 基础包名
     */
    String basePackage = "cn.xuqiudong.generator";

    /**
     * 模块: 作为基础包的 子包 , 以及 请求路径
     */
    String module = "module";

    /**
     * 作者
     */
    String author = System.getProperty("user.name");
    /**
     * 输出目录
     */
    String outputDir = System.getProperty("os.name").toLowerCase().contains("windows") ? "D:\\generator" : "/tmp/generator";


    /**
     * 开启 lombok 注解
     */
    private boolean lombok = true;

    /**
     * 开启 springdoc 注解
     */
    private boolean springdoc = true;

    /**
     * 开启 plus 注解
     */
    private boolean plus = true;

    /**
     * 是否打开输出目录
     */
    private boolean open = true;

    /**
     * 主键的数据类型  将覆盖从竖数据库查询出来的结果
     */
    private Class<?> pkType;


    public static class Builder implements IConfigBuilder<GlobalConfig> {

        final GlobalConfig globalConfig;

        public Builder() {
            this.globalConfig = new GlobalConfig();
        }

        /**
         * 基础包名
         */
        public Builder basePackage(String basePackage) {
            this.globalConfig.basePackage = basePackage;
            return this;
        }

        /**
         * 模块: 作为基础包的 子包 , 以及 请求路径
         */
        public Builder module(String module) {
            this.globalConfig.module = module;
            return this;
        }

        /**
         * 作者
         */
        public Builder author(String author) {
            this.globalConfig.author = author;
            return this;
        }

        /**
         * 输出目录
         */
        public Builder outputDir(String outputDir) {
            this.globalConfig.outputDir = outputDir;
            return this;
        }

        /**
         * 开启 lombok 注解
         */
        public Builder lombok(boolean lombok) {
            this.globalConfig.lombok = lombok;
            return this;
        }

        /**
         * 开启 springdoc 注解
         */
        public Builder springdoc(boolean springdoc) {
            this.globalConfig.springdoc = springdoc;
            return this;
        }

        /**
         * 开启 plus 注解
         */
        public Builder plus(boolean plus) {
            this.globalConfig.plus = plus;
            return this;
        }

        /**
         * 是否打开输出目录
         */
        public Builder open(boolean open) {
            this.globalConfig.open = open;
            return this;
        }

        /**
         * 主键 的数据类型  将覆盖从竖数据库查询出来的结果
         */
        public Builder pkType(Class<?> pkType) {
            this.globalConfig.pkType = pkType;
            return this;
        }


        @Override
        public GlobalConfig build() {
            return globalConfig;
        }
    }

}
