package cn.xuqiudong.common.base.authentication.thymeleaf;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
 * 描述:权限标签处理器 依赖于当前session中的用户的权限列表信息 SessionUserVo#getResourceSet
 *
 * @author Vic.xu
 * @since 2022-02-11 9:38
 */
public class AuthorityElementTagProcessor extends AbstractAttributeTagProcessor {

    public static final String ATTRIBUTE_NAME = "auth";

    static int PRECEDENCE = 1000;

    public AuthorityElementTagProcessor(String dialectPrefix) {
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

    @SuppressFBWarnings(value = "DLS_DEAD_LOCAL_STORE")
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

//        String permissionCode = String.valueOf(expression.execute(context));
		/*
        SessionUserVo userVo = SystemSessionHelper.currentUserInfo();
        if (userVo == null) {
            structureHandler.removeElement();
            return;
        }
        Set<String> resourceSet = userVo.getResourceSet();
        if (resourceSet == null || !resourceSet.contains(permissionCode)) {
            structureHandler.removeElement();
        }

		 */

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.getDialectPrefix())
                .append(this.getPrecedence())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AuthorityElementTagProcessor other = (AuthorityElementTagProcessor) obj;
        return new EqualsBuilder()
                .append(this.getDialectPrefix(), other.getDialectPrefix())
                .append(this.getPrecedence(), other.getPrecedence())
                .isEquals();
    }
}
