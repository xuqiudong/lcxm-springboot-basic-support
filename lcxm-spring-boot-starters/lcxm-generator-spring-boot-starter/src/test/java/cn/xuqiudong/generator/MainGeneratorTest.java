package cn.xuqiudong.generator;

import cn.xuqiudong.basic.generator.CommonFacadeConfig;
import cn.xuqiudong.basic.generator.CommonGeneratorFacade;
import cn.xuqiudong.basic.generator.Generator;
import cn.xuqiudong.basic.generator.config.template.CustomizeTemplateConfig;
import cn.xuqiudong.basic.generator.engine.FreemarkerTemplateEngine;
import cn.xuqiudong.basic.generator.enums.DatabaseType;
import cn.xuqiudong.common.base.entity.BaseMpEntity;
import cn.xuqiudong.common.base.mapper.StringCrudMapper;

import java.nio.file.Paths;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-09-13 16:26
 */
public class MainGeneratorTest {

    public static void main(String[] args) {
        useFacade();
//        test();
    }


    /**
     * 使用 facade 模式 生成代码
     */
    public static void useFacade(){
        CommonFacadeConfig config = CommonFacadeConfig
                .mysql("127.0.0.1", "3306", "qiudong", "qiudong", "qiudong12345678");
        config
                .setBasePackage("cn.xuqiudong.generator");
        config.setModule("test");
        config.setMavenModule("/lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter");
        config.addTable("test_generate");
        config.addTablePrefix("t_");
        String outputDir = config.getOutputDir();
        System.out.println(outputDir);
        Generator generator = CommonGeneratorFacade.build(config);
        generator.generate();
    }
    /**
     * 手动构建Generator 生成代码
     */
    public static void test() {
        String url = "jdbc:mysql://127.0.0.1:3306/qiudong?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true";
        String username = "qiudong";
        String password = "qiudong12345678";
        // 创建 生成类入口 :  支持链式调用
        Generator entrance = Generator.create(DatabaseType.mysql, url, username, password)
                //全局配置
                .globalConfigBuilder(builder ->
                                // 作者: 默认获取系统用户 System.getProperty("user.name")
                                builder.author("Vic.xu")
                                        // 输出目录: 可以指定为项目内 Paths.get(System.getProperty("user.dir")) + "/src/main/java"
//                                .outputDir("D:/desk/generator")
                                        .outputDir(Paths.get(System.getProperty("user.dir")) +"/lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter"+ "/src/test/java")
                                        // 基础包路径
                                        .basePackage("cn.xuqiudong.generator")
                                        // 模块名称: 基础包路径的子包, 以及controller 请求路径
                                        .module("demo")
                                        // 主键类型: 将覆盖数据库查询出的结果
                                        .pkType(String.class)
                                        // 是否开启 lombok 注解 : 默认：true
                                        .lombok(true)
                                        // 是否开启 plus 注解 : 默认：true
                                        .plus(true)
                                        // 是否开启 springdoc 注解 : 默认：true
                                        .springdoc(true)
                                        // 是否打开输出目录 : 默认：true
                                        .open(true)
                )
                // 生成策略配置 核心配置 ★
                .strategyConfigBuilder(builder ->
                                builder
                                        // 要导出的表名: 可以是多个
                                        .tables("test_generate")
//                                .tables("sys_user")
//                                .tables("sys_parameter", "sys_role")
                                        // 表前缀 也可以指定多个
                                        .tablePrefix("sys_", "test_")
                                        // 是否覆盖已有文件: 默认 false
                                        .fileOverride(true)
                                        // 生成的实体(Entity)相关配置 *********************
                                        .entityConfig(ec ->
                                                        // 实体的父类
                                                        ec.supperClass(BaseMpEntity.class)
                                                                // 实体类父类 是否带泛型: 取自主键类型
                                                                .supperClassWithGeneric(true)
                                                //实体中忽略的表字段: xml 中也不会生成
//                                                        .ignoreColumns("create_time", "update_time")
                                        )
                                        // 生成的 Mapper相关配置 *********************
                                        .mapperConfig(mc ->
                                                mc.supperClass(StringCrudMapper.class)
                                                        .supperClassWithGeneric(true)
                                        )
                                        // 生成的 Mapper XML相关配置 *********************
                                        .xmlConfig(xc ->
                                                xc.disable(false))
                                        // 生成的 Service 相关配置 *********************
                                        .serviceConfig(sc ->
                                                        sc
//                                                .supperClass(BaseService.class)
                                                                //service 泛型 只支持 <XxxMapper> 形式
                                                                .supperClassWithGeneric(false)
                                                                .disable(false)
                                        )
                                        // 生成的 Controller 相关配置 *********************
                                        .controllerConfig(cc ->
                                                        cc
//                                                        .supperClass(BaseController.class)
                                                                // controller 泛型 只支持 <XxxService> 形式
                                                                .supperClassWithGeneric(false)
                                                                .disable(false)
                                        )
                )
                // 自定义模板
                .addCustomizedTemplate(CustomizeTemplateConfig.build("customizer", "templates/customizer.java")
                        .setDisable(false)
                        .setFileSuffix(".java")
                        .setFileNameFunction(className -> className + "Customizer")
                )
                // 模板配置: 默认就是freemarker 模板引擎 可以不用配置
                .templateEngine(new FreemarkerTemplateEngine());

        entrance.generate();
        System.out.println("生成完成");
    }

}
