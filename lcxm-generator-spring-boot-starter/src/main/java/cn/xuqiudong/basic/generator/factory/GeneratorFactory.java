package cn.xuqiudong.basic.generator.factory;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.config.DataSourceConfig;
import cn.xuqiudong.basic.generator.config.GlobalConfig;
import cn.xuqiudong.basic.generator.config.StrategyConfig;
import cn.xuqiudong.basic.generator.config.template.BaseTemplateConfig;
import cn.xuqiudong.basic.generator.engine.BaseTemplateEngine;
import cn.xuqiudong.basic.generator.enums.TemplateType;
import cn.xuqiudong.basic.generator.model.context.BaseContext;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.plugin.BaseGeneratorPlugin;
import cn.xuqiudong.basic.generator.plugin.IGeneratorPlugin;
import cn.xuqiudong.basic.generator.util.NameConvertUtils;
import cn.xuqiudong.basic.generator.util.RuntimeUtils;
import jdk.dynalink.beans.StaticClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-09-12 17:29
 */
public class GeneratorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticClass.class);

    ConfigBundle bundle;

    private final DataAssemblyFactory dataAssemblyFactory;

    public GeneratorFactory(DataSourceConfig dataSourceConfig, GlobalConfig globalConfig, StrategyConfig strategyConfig,
                            BaseTemplateEngine templateEngine, List<IGeneratorPlugin> customizedPlugins) {
        Set<IGeneratorPlugin> plugins = initPlugins(customizedPlugins);
        bundle = new ConfigBundle(dataSourceConfig, globalConfig, strategyConfig, templateEngine, plugins);
        dataAssemblyFactory = new DataAssemblyFactory(bundle);
        // init template engine
        bundle.getTemplateEngine().init(bundle);
    }

    /**
     * 初始化插件: 默认插件 + 自定义插件
     */
    private Set<IGeneratorPlugin> initPlugins(List<IGeneratorPlugin> customizedPlugins) {
        Set<IGeneratorPlugin> plugins = new HashSet<>(BaseGeneratorPlugin.getDefaultPlugins());
        if (CollectionUtils.isNotEmpty(customizedPlugins)) {
            plugins.addAll(customizedPlugins);
        }
        return plugins;
    }


    /**
     * 生成: 加载配置 → 读取元数据 → 调用前置插件 → 渲染模板 →  调用后置插件 →  输出(文件)
     */
    public void generate() {
        // 连接数据库 查询表的信息, 根据配置构建导出的表的上下文信息
        List<TemplateContext> templateContexts = dataAssemblyFactory.listTemplateContexts();
        for (TemplateContext context : templateContexts) {
            // 前置插件处理
            beforeGenerate(context);
            //渲染模板 写入文件
            batchOutput(context);
        }
        // 打开文件路径
        open();
    }

    private void batchOutput(TemplateContext context) {
        // 渲染Entity
        renderEntity(context);
        // 渲染query
        renderQuery(context);
        // 渲染Mapper 接口
        renderMapperInterface(context);
        // 渲染Mapper  xml
        renderMapperXml(context);
        // 渲染Service
        renderService(context);
        // 渲染Controller
        renderController(context);
    }

    /**
     * 渲染service
     */
    public void renderService(TemplateContext context) {
        render(TemplateType.XML, context,
                bundle.getStrategyConfig().getServiceTemplateConfig(), context.getService());
    }

    /**
     * 渲染 controller
     */
    public void renderController(TemplateContext context) {
        render(TemplateType.XML, context,
                bundle.getStrategyConfig().getControllerTemplateConfig(), context.getController());
    }

    /**
     * 渲染 Mapper.xml
     */
    private void renderMapperXml(TemplateContext context) {
        render(TemplateType.XML, context,
                bundle.getStrategyConfig().getXmlTemplateConfig(), context.getMapper());
    }

    /**
     * 渲染 mapper 接口 和 mapper xml
     */
    private void renderMapperInterface(TemplateContext context) {
        render(TemplateType.ENTITY, context,
                bundle.getStrategyConfig().getMapperTemplateConfig(), context.getMapper());
    }

    /**
     * 渲染 entity
     */
    public void renderEntity(TemplateContext context) {
        render(TemplateType.ENTITY, context,
                bundle.getStrategyConfig().getEntityTemplateConfig(), context.getEntity());
    }

    /**
     * 渲染 query
     */
    public void renderQuery(TemplateContext context) {
        render(TemplateType.QUERY, context,
                bundle.getStrategyConfig().getQueryTemplateConfig(), context.getQuery());
    }

    /**
     * 渲染
     */
    public void render(TemplateType templateType, TemplateContext context,
                       BaseTemplateConfig config, BaseContext baseContext) {
        if (config.isDisable()) {
            LOGGER.warn("{} 模板渲染禁用, tableName: {}", templateType, context.getTable().getTableName());
            return;
        }
        // 1. 获取模板内容
        String content = getContent(config, context);
        // 2. 获取输出文件地址
        String path = getOutPutPath(config, baseContext);
        // 3. 写入文件
        writeFile(path, content);
    }

    /**
     * 前置插件处理
     */
    public void beforeGenerate(TemplateContext templateContext) {
        Set<IGeneratorPlugin> plugins = bundle.getPlugins();
        if (CollectionUtils.isNotEmpty(plugins)) {
            for (IGeneratorPlugin plugin : plugins) {
                if (plugin.enable(bundle)) {
                    LOGGER.info("插件: {} 开始处理", plugin.getClass().getName());
                    plugin.beforeGenerate(templateContext);
                }
            }
        }
    }

    /**
     * 获取渲染后的 模板内容
     */
    private String getContent(BaseTemplateConfig config, TemplateContext context) {
        return bundle.getTemplateEngine().render(config.getTemplatePath(), context);
    }

    /**
     * 获得某个模板的输出文件地址:  全局输出路径 + 包路径 + 类名 + 文件后缀
     *
     * @param config      模板配置
     * @param baseContext 基础上下文
     */
    private String getOutPutPath(BaseTemplateConfig config, BaseContext baseContext) {
        String packageName = baseContext.getPackageName();
        String fileSuffix = config.getFileSuffix();
        return NameConvertUtils.getFullOutputFilePath(bundle.getGlobalConfig().getOutputDir(), packageName,
                baseContext.getClassName(),
                fileSuffix);
    }


    /**
     * 打开输出目录: 如果配置了打开的话
     */
    private void open() {
        if (bundle.getGlobalConfig().isOpen()) {
            LOGGER.info("打开输出目录");
        }
        // 判断输出目录是否存在
        String outputDir = bundle.getGlobalConfig().getOutputDir();
        if (StringUtils.isBlank(outputDir)) {
            LOGGER.warn("输出目录未配置");
            return;
        }
        LOGGER.info("输出目录: {}", outputDir);
        if (!Files.exists(Paths.get(outputDir))) {
            LOGGER.warn("输出目录不存在, 请先创建目录: {}", outputDir);
            return;
        }
        // 打开目录
        try {
            RuntimeUtils.openDir(outputDir);
        } catch (Exception e) {
            LOGGER.error("打开输出目录失败: {}", outputDir, e);
        }

    }

    /**
     * 写入文件, 存在则log记录 ,不写入
     */
    private void writeFile(String filePath, String content) {
        // 判断文件是否存在
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {

            if (!bundle.getStrategyConfig().isFileOverride()) {
                LOGGER.warn("文件已存在, 不写入: {}", filePath);
                return;
            }
            // 文件已存在，覆盖写入
            try {
                LOGGER.warn("文件已存在, 覆盖写入: {}", filePath);
                Files.write(path,
                        content.getBytes(StandardCharsets.UTF_8),
                        // 清空再写
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);
            } catch (IOException e) {
                LOGGER.error("覆盖文件失败: {}", filePath, e);
            }

            return;
        }
        try {
            LOGGER.info("写入文件: {}", filePath);
            // 确保父目录存在
            Files.createDirectories(path.getParent());
            // 创建文件
            Files.createFile(path);
            // 写入内容
            Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.WRITE);

        } catch (IOException e) {
            LOGGER.error("写入文件失败: {}", filePath, e);
        }
    }


    /**
     * 手动确认生成
     */
    public void confirmGenerate() {
        if (!bundle.getGlobalConfig().isConfirm()) {
            return;
        }
        // 如果不存在覆盖文件, 则不需要手动确认
        if (!bundle.getStrategyConfig().isFileOverride()) {
            return;
        }
        String tables = String.join(",", bundle.getStrategyConfig().getTables());
        LOGGER.warn(" 将生成[｛｝]表的代码, 该操作可能会覆盖相关文件，请确认是否继续？(yes/no):", tables);

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        String input = scanner.nextLine().trim();

        if (!"yes".equalsIgnoreCase(input)) {
            LOGGER.warn("生成操作已取消!");
            System.exit(0);
        }
        LOGGER.warn("生成操作已确认，开始执行...");
    }


}
