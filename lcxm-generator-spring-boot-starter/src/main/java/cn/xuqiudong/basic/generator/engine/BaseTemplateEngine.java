package cn.xuqiudong.basic.generator.engine;

import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 描述:
 * 模板引擎基类
 *
 * @author Vic.xu
 * @since 2025-09-15 9:58
 */
public abstract class BaseTemplateEngine {
    /**
     * 初始化引擎, 会被最先调用
     */
    public abstract BaseTemplateEngine init(ConfigBundle bundle);

    /**
     * 模板后缀: 需要加上`.`, 如 .vm,  .ftl
     */
    public abstract String templateSuffix();

    /**
     * 渲染模板并输出到Writer
     *
     * @param templatePath 模板路径
     * @param model        模板参数
     * @param writer       输出目标（如FileWriter、response.getWriter()）
     * @throws Exception 渲染异常
     * @see TemplateContext#toMapContext()  -> Map<String, Object>
     */
    protected abstract void render(String templatePath, Map<String, Object> model, Writer writer) throws Exception;

    /**
     * 渲染模板 到字符串
     *
     * @param templatePath
     * @param model        模板参数
     * @return String
     * @throws Exception
     */
    public String render(String templatePath, TemplateContext model) {
        try (StringWriter writer = new StringWriter()) {
            // 复用Writer重载方法，避免代码重复
            render(getTemplatePath(templatePath), model.toMapContext(), writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 渲染模板并输出到OutputStream（默认实现，基于Writer重载）
     *
     * @param templatePath 模板路径
     * @param model        模板参数
     * @param outputStream 输出流（如FileOutputStream、response.getOutputStream()）
     * @throws Exception 渲染异常
     */
    public void render(String templatePath, TemplateContext model, OutputStream outputStream) throws Exception {
        // 基于UTF-8编码将OutputStream包装为Writer
        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            render(getTemplatePath(templatePath), model.toMapContext(), writer);
        }
    }

    /**
     * 获取模板路径 : 加上后悔
     */
    public String getTemplatePath(String templatePath) {
        if (templatePath.endsWith(templateSuffix())) {
            return templatePath;
        }
        return templatePath + templateSuffix();
    }
}

