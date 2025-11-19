package cn.xuqiudong.common.base.tool.evn;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *   加载外部配置 ，将要覆盖内部配置 支持 properties 和 yml yaml
 *   <p>
 *       1、 配置
 *       在 META-INF 目录下的spring.factories内：
 *       org.springframework.boot.env.EnvironmentPostProcessor=cn.xuqiudong.common.base.tool.evn.ExternalConfigProcessor
 *       2、 配置项
 *         external.config.location=/data/config/config.properties
 *   </p>
 * @author Vic.xu
 * @since 2025-11-19 15:15
 */
public class ExternalConfigProcessor implements EnvironmentPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExternalConfigProcessor.class);
    /**
     * 外部配置文件路径的配置项   external.config.location=/data/config/config.properties
     */
    private static final String EXTERNAL_CONFIG_LOCATION_KEY = "external.config.location";
    private static final boolean PRINT_EXTERNAL_CONFIG = true;
    private static final boolean PRINT_CONFIG_VALUE = false;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 1. 读取你配置的外部文件路径
        String externalConfigPath = environment.getProperty(EXTERNAL_CONFIG_LOCATION_KEY);
        if (!StringUtils.hasText(externalConfigPath)) {
            logger.info("未配置{}，跳过外部配置加载", EXTERNAL_CONFIG_LOCATION_KEY);
            return;
        }
        // 2. 校验文件是否存在
        Resource externalResource = new FileSystemResource(externalConfigPath);
        if (!externalResource.exists() || !externalResource.isReadable()) {
            logger.warn("外部配置文件不存在或不可读：{}", externalConfigPath);
            return;
        }
        try {
            // 3. 加载配置：用你的文件路径作为配置源名称（替代external-config）
            List<PropertySource<?>> propertySources = loadConfig(externalResource, externalConfigPath);
            if (propertySources.isEmpty()) {
                logger.warn("外部配置文件加载为空：{}", externalConfigPath);
                return;
            }
            // 4. 注入配置（最高优先级）
            MutablePropertySources mutablePropertySources = environment.getPropertySources();
            for (PropertySource<?> propertySource : propertySources) {
                mutablePropertySources.addFirst(propertySource);

                // 5. 打印配置项（可选）
                if (PRINT_EXTERNAL_CONFIG) {
                    printExternalConfigItems(propertySource, environment);
                }
                logger.info("外部配置加载成功：{}", externalConfigPath);
            }
        } catch (IOException e) {
            logger.error("加载外部配置失败：{}", externalConfigPath, e);
            throw new RuntimeException("外部配置加载失败", e);
        }
    }

    /**
     * 加载配置文件（用文件路径作为配置源名称，固定UTF-8）
     */
    private List<PropertySource<?>> loadConfig(Resource resource, String configPath) throws IOException {
        if (configPath.endsWith(".yml") || configPath.endsWith(".yaml")) {
            YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
            // 第一个参数：用你的文件路径作为配置源名称（彻底去掉external-config）
            return yamlLoader.load(configPath, resource);
        } else if (configPath.endsWith(".properties")) {
            PropertiesPropertySourceLoader propertiesLoader = new PropertiesPropertySourceLoader();
            // 第一个参数：用你的文件路径作为配置源名称
            return propertiesLoader.load(configPath, resource);
        } else {
            throw new IllegalArgumentException("不支持的配置文件类型：" + configPath);
        }
    }

    /**
     * 打印外部配置项（简化版）
     */
    private void printExternalConfigItems(PropertySource<?> externalPropertySource, ConfigurableEnvironment environment) {
        if (!(externalPropertySource instanceof MapPropertySource)) {
            logger.warn("不支持的配置源类型，无法打印：{}", externalPropertySource.getClass().getName());
            return;
        }

        MapPropertySource mapPropertySource = (MapPropertySource) externalPropertySource;
        Map<String, Object> externalConfigMap = mapPropertySource.getSource();

        logger.info("外部配置项总数：{}", externalConfigMap.size());
        logger.info("==================== 外部配置项列表 ====================");
        for (Map.Entry<String, Object> entry : externalConfigMap.entrySet()) {
            String key = entry.getKey();
            Object innerValue = environment.getProperty(key);
            StringBuilder logMsg = new StringBuilder("Key: ").append(key);

            if (PRINT_CONFIG_VALUE) {
                logMsg.append(" | 外部值: ").append(entry.getValue())
                        .append(" | 内置值: ").append(innerValue == null ? "无" : innerValue);
            } else {
                logMsg.append(" | 状态: ").append(innerValue == null ? "新增配置" : "覆盖内置配置");
            }
            logger.info(logMsg.toString());
        }
        logger.info("=======================================================");
    }
}
