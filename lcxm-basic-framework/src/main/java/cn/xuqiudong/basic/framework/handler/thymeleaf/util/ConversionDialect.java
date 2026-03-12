package cn.xuqiudong.basic.framework.handler.thymeleaf.util;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述:转换工具字典，比如把用户id转为用户name； 使用方式${#conversion.methodName(args)}
 * 需要使用的项目把此类注入到springboot
 * @see <a href="https://www.thymeleaf.org/doc/tutorials/3.0/extendingthymeleaf.html#expression-object-dialects-iexpressionobjectdialect">iexpressionobjectdialect</a>
 *
 * @author Vic.xu
 * @since 2022-04-07 17:40
 */
@SuppressWarnings("PMD")
public class ConversionDialect implements IExpressionObjectDialect {

    private static final String name = "conversion";

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {
            @Override
            public Set<String> getAllExpressionObjectNames() {
                //工具类可以其多个名字
                Set<String> names = new HashSet<>();
                names.add(getName());
                return names;
            }

            @Override
            public Object buildObject(IExpressionContext context, String expressionObjectName) {
                return new ThymeleafConversionUtils();
            }

            @Override
            public boolean isCacheable(String expressionObjectName) {
                return true;
            }
        };
    }

    @Override
    public String getName() {
        return name;
    }
}
