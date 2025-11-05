package cn.xuqiudong.common.base.authentication.thymeleaf;

import cn.xuqiudong.common.base.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.lang.reflect.Method;


/**
 * 描述:枚举select标签处理器 <br/>
 * 例如 <select enum="com.kjlink.entrance.base.enums.FlowEnum.EntityTypeFlowEnum"
 * data-value-method="name" data-text-method="getText" data-value="aa"
 * data-option="请选择"></select> <br/>
 * data-value-method="name" data-text-method="getText" 可以省略， <br/>
 * 分别指代option的 value 为 枚举的name（默认）方法，option的text显示值为枚举的getText（默认）方法 <br/>
 *
 * @author Vic.xu
 * @since 2022-02-11 9:38
 */
public class EnumElementTagProcessor extends AbstractAttributeTagProcessor {

    protected static final Logger logger = LoggerFactory.getLogger(EnumElementTagProcessor.class);

    public static final String ATTRIBUTE_NAME = "enum";

    static int PRECEDENCE = 1000;

    private static final String SELECT_TAG = "select";

    /**
     * data-value-method属性
     */
    private static String DATA_VALUE_METHOD_ATTRIBUTE = "data-value-method";
    /**
     * 枚举获取option value值的默认方法为name()
     */
    private static String VALUE_METHOD_DEFAULT = "name";

    /**
     * data-text-method属性
     */
    private static String DATA_TEXT_METHOD_ATTRIBUTE = "data-text-method";
    /**
     * 枚举获取option展示值的默认方法为getText()
     */
    private static String TEXT_METHOD_DEFAULT = "getText";

    public EnumElementTagProcessor(String dialectPrefix) {
        super(
                // 处理thymeleaf 的模型
                TemplateMode.HTML,
                // 标签前缀名 相当于th:if中的th
                dialectPrefix,
                // No tag name: match any tag name
                null,
                // No prefix to be applied to tag name
                false,
                // 标签前缀的 属性 例如：<kjlink:auth="">
                ATTRIBUTE_NAME,
                // Apply dialect prefix to attribute name
                true,
                // Precedence (inside dialect'.s precedence)
                PRECEDENCE,
                // Remove the matched attribute afterwards
                true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
                             String attributeValue, IElementTagStructureHandler structureHandler) {
        final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * 获得Thymeleaf标准表达式解析器
         */
        final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

        /*
         * 将属性值解析为Thymeleaf标准表达式
         */
        final IStandardExpression expression = parser.parseExpression(context, attributeValue);

        // 获得枚举的全限定名
        String enumName = String.valueOf(expression.execute(context));

        // 获得标签名
        String tagName = tag.getElementCompleteName();

        // 暂时只处理select标签
        if (SELECT_TAG.equalsIgnoreCase(tagName)) {
            try {
                appendSelectOptions(enumName, context, tag, structureHandler);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("处理枚举select标签出错了：" + e.getMessage());
            }
        }

    }

    private void appendSelectOptions(String enumName, ITemplateContext context, IProcessableElementTag tag,
                                     IElementTagStructureHandler structureHandler) throws Exception {
        if (StringUtils.isBlank(enumName)) {
            return;
        }

        // 这里没有使用 Class.forName(enumName)，可以使用下文的两种classLoader
        // SpringContextUtils.getApplicationContext(context).getClassLoader();
        Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(enumName);
        // 获取option 的value对应的枚举的方法
        String valueMethodName = tag.getAttributeValue(DATA_VALUE_METHOD_ATTRIBUTE);
        if (StringUtils.isBlank(valueMethodName)) {
            valueMethodName = VALUE_METHOD_DEFAULT;
        }

        // 获取option 的text对应的枚举的方法
        String textMethodName = tag.getAttributeValue(DATA_TEXT_METHOD_ATTRIBUTE);
        if (StringUtils.isBlank(textMethodName)) {
            valueMethodName = TEXT_METHOD_DEFAULT;
        }

        Method valueMethod = cls.getMethod(valueMethodName);
        Method textMethod = cls.getMethod(textMethodName);

        // 需要选中的值
        String currentValue = tag.getAttributeValue("data-value");

        IModelFactory factory = context.getModelFactory();
        IModel model = factory.createModel();

        // 第一个option
        String firstOption = tag.getAttributeValue("data-option");
        if (StringUtils.isNotBlank(firstOption)) {
            addOption(model, factory, firstOption, "", false);
        }

        for (Object option : cls.getEnumConstants()) {
            String text = textMethod.invoke(option) + "";
            String value = valueMethod.invoke(option) + "";
            addOption(model, factory, text, value, StringUtils.equals(currentValue, value));
        }
        structureHandler.insertImmediatelyAfter(model, true);

    }

    private void addOption(IModel model, IModelFactory factory, String name, String value, boolean selected) {
        String open = "option value=\"" + value + "\"";
        if (selected) {
            open += " selected=\"selected\"";
        }
        model.add(factory.createOpenElementTag(open));
        model.add(factory.createText(name));
        model.add(factory.createCloseElementTag("option"));
    }

}
