package cn.xuqiudong.basic.generator.engine;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 描述:
 * 基于freemarker的模板引擎
 *
 * @author Vic.xu
 * @since 2025-09-15 9:59
 */
public class FreemarkerTemplateEngine extends BaseTemplateEngine {

    private Configuration configuration;

    @Override
    public BaseTemplateEngine init(ConfigBundle bundle) {
        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, StringPool.SLASH);
        return this;
    }

    @Override
    public String templateSuffix() {
        return ".ftl";
    }


    /**
     * 渲染到Writer（核心实现，供其他方法复用）
     * 调用方需要关闭 Writer
     */
    @Override
    protected void render(String templatePath, Map<String, Object> model, Writer writer) throws Exception {
        Template template = configuration.getTemplate(templatePath);
        // 直接将渲染结果写入目标Writer
        template.process(model, writer);
        writer.flush(); // 确保数据刷入输出流
    }
}
