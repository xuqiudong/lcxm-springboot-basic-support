package cn.xuqiudong.basic.generator;

import cn.xuqiudong.basic.generator.config.template.ControllerTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.CustomizeTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.EntityTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.MapperTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.MapperXmlTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.QueryTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.ServiceTemplateConfig;
import cn.xuqiudong.basic.generator.customize.VueCustomizeConfig;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.basic.mybatisplus.entity.BaseMpEntity;
import cn.xuqiudong.basic.mybatisplus.mapper.StringCrudMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 描述:
 * 通用代码生成器配置
 *
 * @author Vic.xu
 * @see CommonGeneratorFacade#build(CommonFacadeConfig)
 * @since 2026-03-11 10:01
 */
@Data
//@Accessors(chain = true)
public class CommonFacadeConfig {

    private DatabaseType databaseType = DatabaseType.mysql;

    private String url;

    private String username;

    private String password;

    private String author = System.getProperty("user.name");

    /**
     * 代码输出位置， 不配置的话 则默认为项目路径下， mavenModule模块下的src/main/java 下
     */
    @Getter(AccessLevel.NONE)
    private String outputDir;

    /**
     * maven module: 用于没有配置  outputDir 的时候 拼接处项目的地址 + maven 子模块
     */
    private String mavenModule;

    private String basePackage;

    /**
     * 包的模块 ， basePackage + module 就是代码的位置
     */
    private String module;

    private Class<?> pkType = String.class;

    private boolean lombok = true;
    private boolean springdoc = true;
    private boolean plus = true;
    private boolean open = true;

    private List<String> tables = new ArrayList<>();

    private List<String> tablePrefix = new ArrayList<>();

    private boolean fileOverride = false;

    /**
     * entity config
     */
    private Consumer<EntityTemplateConfig.Builder> entityConfig = ec ->
            // 实体的父类
            ec.supperClass(BaseMpEntity.class)
                    // 实体类父类 是否带泛型: 取自主键类型
                    .supperClassWithGeneric(true);

    /**
     * query  config
     */
    private Consumer<QueryTemplateConfig.Builder> queryConfig = ec -> {
        ec.disable(false);
    };

    /**
     * mapper config
     */
    private Consumer<MapperTemplateConfig.Builder> mapperConfig = mc ->
            mc.supperClass(StringCrudMapper.class)
                    .supperClassWithGeneric(true);

    /**
     * mapper xml config
     */
    private Consumer<MapperXmlTemplateConfig.Builder> mapperXmlConfig = mc -> {
    };

    /**
     * service config
     */
    private Consumer<ServiceTemplateConfig.Builder> serviceConfig = sc ->
            sc
                    // service 泛型 只支持 <XxxMapper> 形式
                    .supperClassWithGeneric(false)
                    .disable(false);

    /**
     * controller config
     */
    private Consumer<ControllerTemplateConfig.Builder> controllerConfig = cc ->
            cc
                    // controller 泛型 只支持 <XxxService> 形式
                    .supperClassWithGeneric(false)
                    .disable(false);

    /**
     * 自定义模板
     */

    private List<CustomizeTemplateConfig> customizeTemplateConfigs = new ArrayList<>();


    /**
     * 基于 mysql 数据库 的配置
     *
     */
    public static CommonFacadeConfig mysql(String host, String port, String database, String username, String password) {
        CommonFacadeConfig config = new CommonFacadeConfig();
        config.setDatabaseType(DatabaseType.mysql);
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowMultiQueries=true";
        config.setUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return config;

    }

    /**
     * 设置代码输出位置
     *
     */
    public CommonFacadeConfig setOutputDir(String outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    public String getOutputDir() {
        if (StringUtils.isBlank(outputDir)) {
            return defaultOutputDir();
        }
        return outputDir;
    }

    /**
     * 默认的代码输出位置: 对应项目下的 src/main/java 下
     */
    private String defaultOutputDir() {
        Path projectHome = findProjectHome();
        return projectHome.resolve("src/main/java").toAbsolutePath().toString();
    }

    private Path findProjectHome() {
        // 1. 获取项目根目录（IDEA 中运行时，user.dir 默认为项目根目录）
        String projectHome = System.getProperty("user.dir");
        Path rootPath = Paths.get(projectHome).toAbsolutePath();
        if (StringUtils.isBlank(mavenModule)) {
            return rootPath;
        }
        // 步骤1：清理开头分隔符
        String modulePath = mavenModule.trim().replaceAll("^[/\\\\]", "");
        // 步骤2：统一分隔符（兼容 Windows \ 和 Linux /）
        modulePath = modulePath.replace("/", File.separator).replace("\\", File.separator);
        return rootPath.resolve(modulePath);
    }

    /**
     * 添加要要生成的代码的表
     */
    public CommonFacadeConfig addTable(String table) {
        this.tables.add(table);
        return this;
    }

    /**
     * 添加要要生成的代码的表
     */
    public CommonFacadeConfig addTable(String... tables) {
        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    /**
     * 添加表前缀
     */
    public CommonFacadeConfig addTablePrefix(String tablePrefix) {
        this.tablePrefix.add(tablePrefix);
        return this;
    }

    /**
     * 添加表前缀
     */
    public CommonFacadeConfig addTablePrefix(String... tablePrefix) {
        this.tablePrefix.addAll(Arrays.asList(tablePrefix));
        return this;
    }

    /**
     * 添加自定义模板: 基于此生成代码
     */
    public CommonFacadeConfig addCustomizeTemplate(CustomizeTemplateConfig customizeTemplateConfig) {
        this.customizeTemplateConfigs.add(customizeTemplateConfig);
        return this;
    }

    /**
     * 添加自定义模板: 基于此生成代码
     */
    public CommonFacadeConfig addCustomizeTemplate(String subPackage, String templatePath) {
        this.customizeTemplateConfigs.add(CustomizeTemplateConfig.build(subPackage, templatePath));
        return this;
    }




    /**
     * 在默认的 entityConfig 基础上继续追加配置细节
     */
    public CommonFacadeConfig addEntityConfig(Consumer<EntityTemplateConfig.Builder> customEntityConfig) {
        this.entityConfig = this.entityConfig.andThen(customEntityConfig);
        return this;
    }

    /**
     * 在默认的 queryConfig 基础上继续追加配置细节
     */
    public CommonFacadeConfig addQueryConfig(Consumer<QueryTemplateConfig.Builder> customQueryConfig) {
        this.queryConfig = this.queryConfig.andThen(customQueryConfig);
        return this;
    }

    /**
     * 在默认的 mapperConfig 基础上继续追加配置细节
     */
    public CommonFacadeConfig addMapperConfig(Consumer<MapperTemplateConfig.Builder> customMapperConfig) {
        this.mapperConfig = this.mapperConfig.andThen(customMapperConfig);
        return this;
    }

    /**
     * 在默认的 mapperXmlConfig 基础上继续追加配置细节
     */
    public CommonFacadeConfig addMapperXmlConfig(Consumer<MapperXmlTemplateConfig.Builder> customMapperXmlConfig) {
        // 使用 andThen 将外部传入的配置，追加到现有的默认配置之后
        this.mapperXmlConfig = this.mapperXmlConfig.andThen(customMapperXmlConfig);
        return this;
    }

    /**
     * 在默认的 serviceConfig 基础上继续追加配置细节
     */
    public CommonFacadeConfig addServiceConfig(Consumer<ServiceTemplateConfig.Builder> customServiceConfig) {
        this.serviceConfig = this.serviceConfig.andThen(customServiceConfig);
        return this;
    }

    /**
     * 在默认的 controllerConfig 基础上继续追加配置细节
     */
    public CommonFacadeConfig addControllerConfig(Consumer<ControllerTemplateConfig.Builder> customControllerConfig) {
        this.controllerConfig = this.controllerConfig.andThen(customControllerConfig);
        return this;
    }

    /**
     * 配置 controller 上的请求路径
     */
    public CommonFacadeConfig addControllerRequestMapping(String tableName, String requestMapping) {
        this.controllerConfig = this.controllerConfig.andThen(cc -> cc.addRequestMapping(tableName, requestMapping));
        return this;
    }
    /**
     * 配置 controller 上的请求路径 map
     */
    public CommonFacadeConfig addControllerRequestMapping(Map<String, String> requestMappingMap) {
        this.controllerConfig = this.controllerConfig.andThen(cc -> cc.addRequestMapping(requestMappingMap));
        return this;
    }


    /**
     * 添加自定义的默认的 vue 模板
     * @see VueCustomizeConfig#getDefaultVueTemplates()
     */
    public CommonFacadeConfig addCustomizeVueTemplates() {
        this.customizeTemplateConfigs.addAll(VueCustomizeConfig.getDefaultVueTemplates());
        return this;
    }


    /**
     * disable all default template generate
     */
    public CommonFacadeConfig disableAll() {
        boolean disable = true;
        this.addEntityConfig(ec -> ec.disable(disable))
                .addQueryConfig(qc -> qc.disable(disable))
                .addMapperConfig(mc -> mc.disable(disable))
                .addMapperXmlConfig(mc -> mc.disable(disable))
                .addServiceConfig(sc -> sc.disable(disable))
                .addControllerConfig(cc -> cc.disable(disable));
        return this;
    }



}