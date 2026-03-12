package cn.xuqiudong.basic.generator;

import cn.xuqiudong.basic.generator.config.template.ControllerTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.CustomizeTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.EntityTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.MapperTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.MapperXmlTemplateConfig;
import cn.xuqiudong.basic.generator.config.template.ServiceTemplateConfig;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.basic.core.entity.BaseMpEntity;
import cn.xuqiudong.basic.core.mapper.StringCrudMapper;
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
import java.util.function.Consumer;

/**
 * 描述:
 *   通用代码生成器配置
 * @see  CommonGeneratorFacade#build(CommonFacadeConfig)
 * @author Vic.xu
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

    private CustomizeTemplateConfig customizeTemplateConfig;


    public static CommonFacadeConfig mysql(String host, String port, String database, String username, String password) {
        CommonFacadeConfig config = new CommonFacadeConfig();
        config.setDatabaseType(DatabaseType.mysql);
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowMultiQueries=true";
        config.setUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return config;

    }

    public CommonFacadeConfig setOutputDir(String outputDir) {
        this.outputDir = outputDir;
        return this;
    }
    public String getOutputDir() {
        if (StringUtils.isBlank(outputDir)) {
            return defaultOutputDir();
        }
        return  outputDir;
    }

    private String defaultOutputDir() {
        Path projectHome = findProjectHome();
        return projectHome.resolve("src/main/java").toAbsolutePath().toString();
    }

    private Path findProjectHome(){
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

    public CommonFacadeConfig addTable(String table) {
        this.tables.add(table);
        return this;
    }

    public CommonFacadeConfig addTable(String... tables) {
        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    public CommonFacadeConfig addTablePrefix(String tablePrefix) {
        this.tablePrefix.add(tablePrefix);
        return this;
    }

    public CommonFacadeConfig addTablePrefix(String... tablePrefix) {
        this.tablePrefix.addAll(Arrays.asList(tablePrefix));
        return this;
    }

}