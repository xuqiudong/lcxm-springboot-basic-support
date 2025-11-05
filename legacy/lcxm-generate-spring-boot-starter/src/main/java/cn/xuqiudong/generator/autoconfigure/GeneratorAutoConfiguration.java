package cn.xuqiudong.generator.autoconfigure;

import cn.xuqiudong.generator.contant.DatabaseType;
import cn.xuqiudong.generator.controller.GeneratorController;
import cn.xuqiudong.generator.dao.BaseGeneratorDao;
import cn.xuqiudong.generator.dao.GaussGeneratorDao;
import cn.xuqiudong.generator.dao.MysqlGeneratorDao;
import cn.xuqiudong.generator.dao.OracleGeneratorDao;
import cn.xuqiudong.generator.helper.ShowAccessUrlHelper;
import cn.xuqiudong.generator.service.GeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 说明 :  代码生成starter 入口
 * @author  Vic.xu
 * @since  2020年3月10日 上午10:54:39
 */
@Configuration
@ConditionalOnClass(value = {JdbcTemplate.class})
@ConditionalOnWebApplication
@ConditionalOnExpression("${generatir.enabled:true}")
@EnableConfigurationProperties({GeneratorProperties.class})
public class GeneratorAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(GeneratorAutoConfiguration.class);

    private GeneratorProperties generatorProperties;

    private JdbcTemplate jdbcTemplate;

    public GeneratorAutoConfiguration(GeneratorProperties generatorProperties, JdbcTemplate jdbcTemplate) {
        this.generatorProperties = generatorProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public BaseGeneratorDao generatorDao() {
        DatabaseType type = DatabaseType.getByName(generatorProperties.getDatabase());
        Assert.notNull(type, "当前项目未支持" + type + "类型的数据库代码生成。");
        BaseGeneratorDao generatorDao = null;
        switch (type) {
            case oracle:
                generatorDao = new OracleGeneratorDao(jdbcTemplate);
                break;
            case mysql:
                generatorDao = new MysqlGeneratorDao(jdbcTemplate);
                break;
            case gauss:
                generatorDao = new GaussGeneratorDao(jdbcTemplate);
            default:
                break;
        }
        logger.info("代码自动生成当前配置的数据库类型为：{}, 使用的dao为{}。", type, generatorDao.getClass().getSimpleName());
        logger.info("代码自动生成相关配置如下：\n{}", generatorProperties);
        return generatorDao;
    }

    @Bean
    @ConditionalOnMissingBean
    public GeneratorService generatorService(BaseGeneratorDao generatorDao, GeneratorProperties generatorProperties) {
        return new GeneratorService(generatorDao, generatorProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public GeneratorController generatorController(GeneratorService generatorService) {
        return new GeneratorController(generatorService);
    }

    /**
     * 打印访问地址
     */
    @Bean
    @ConditionalOnMissingBean
    public ShowAccessUrlHelper showAccessUrlHelper() {
        return new ShowAccessUrlHelper();
    }

    /**
     * 把static HTML所在的文件夹设置为静态资源
     *
     * 描述:
     * @author Vic.xu
     * @since  2020年5月15日下午5:33:38
     */
    @Configuration
    static class StaticWebMvcConfigurerAdapter implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        }
    }

}
