package cn.xuqiudong.basic.core.authentication.thymeleaf;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * 描述:权限属性处理器 依赖于当前session中的用户的权限列表信息 SessionUserVo#getResourceSet。
 *
 * 将权限属性列表转为换 boolean 值列表，标识对应位置的权限是否具备，如： r1, r2 => true,false：标识具备 r1 权限，不具备
 * r2 权限
 *
 * @author Vic.xu
 * @since 2022-02-11 9:38
 */
public class AuthorityAttributeTagProcessor extends AbstractAttributeTagProcessor {

    private static final String DATA_ATTRIBUTE_NAME = "data-resources-required";

    public static final String ATTRIBUTE_NAME = "resources-required";

    static int PRECEDENCE = 1000;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public AuthorityAttributeTagProcessor(String dialectPrefix) {
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

        String[] resourceArr = (String[]) (expression.execute(context));

        /*
         * 如果标签上未指定具体权限，表示无权限限制： kjlink:resources-required="${new String[] {}}"
         */
        if (resourceArr.length == 0) {
            structureHandler.setAttribute(DATA_ATTRIBUTE_NAME, String.valueOf(Boolean.TRUE));
            return;
        }

        /*
         * 如果用户信息未获取到，无任何权限，不允许任何操作
         */
		/*
        SessionUserVo userVo = SystemSessionHelper.currentUserInfo();
        if (userVo == null) {
            structureHandler.setAttribute(DATA_ATTRIBUTE_NAME, String.valueOf(Boolean.FALSE));
            return;
        }
	*/
        /*
         * 如果用户权限未获取到，无任何权限，不允许任何操作
         */
       /* Set<String> resourceSet = userVo.getResourceSet();
        if (resourceSet == null) {
            structureHandler.setAttribute(DATA_ATTRIBUTE_NAME, String.valueOf(Boolean.FALSE));
            return;
        }
*/
        /*
         * 逐个解析权限
         */
        /*
        List<String> resourcePresent = new ArrayList<>();
        for (String resource : resourceArr) {
            if (StringUtils.isBlank(resource)) {
                // null或空白字符串标识无权限限制
                resourcePresent.add(String.valueOf(Boolean.TRUE));
            } else if (resourceSet.contains(resource.trim())) {
                resourcePresent.add(String.valueOf(Boolean.TRUE));
            } else {
                resourcePresent.add(String.valueOf(Boolean.FALSE));
            }
        }


        structureHandler.setAttribute(DATA_ATTRIBUTE_NAME, String.join(",", resourcePresent));
         */
    }

}
