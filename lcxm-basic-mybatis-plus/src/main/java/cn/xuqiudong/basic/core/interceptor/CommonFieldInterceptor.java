package cn.xuqiudong.basic.core.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.util.function.Consumer;

/**
 * 描述:
 * mybatis 通用字段拦截器
 * <p>
 * 使用方式1： 可以继承本类 重写填充通用字段的方法
 * <plugin interceptor="cn.xuqiudong.common.base.mybatis.CommonFieldInterceptor">
 * </p>
 * <p>
 * 使用方式2： 通过ConfigurationCustomizer配置
 * <code>
 *
 * @author Vic.xu
 * @Bean public ConfigurationCustomizer mybatisConfigurationCustomizer() {
 * return configuration ->
 *  configuration.addInterceptor(new CommonFieldInterceptor((obj) -> {...}, (obj) -> {...}));
 * }
 * </code>
 * </p>
 * @see Executor#update(MappedStatement, Object)
 * @since 2025-03-20 14:18
 */
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class CommonFieldInterceptor implements Interceptor {

    /**
     * 插入时填充通用字段  可以考虑判断当前object instanceof Xxx 然后自行插入通用字段
     */
    protected Consumer<Object> fillInsertCommonField;

    /**
     * 更新时填充通用字段 可以考虑判断当前object instanceof Xxx 然后自行插入通用字段
     */
    protected Consumer<Object> fillUpdateCommonField;

    public CommonFieldInterceptor() {
        this.fillInsertCommonField = (obj) -> {
        };
        this.fillUpdateCommonField = (obj) -> {
        };
    }


    public CommonFieldInterceptor(Consumer<Object> fillInsertCommonField, Consumer<Object> fillUpdateCommonField) {
        this.fillInsertCommonField = fillInsertCommonField;
        this.fillUpdateCommonField = fillUpdateCommonField;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        fillCommonField(invocation);
        return invocation.proceed();
    }

    /**
     * 追加通用字段
     *
     * @param invocation
     */
    private void fillCommonField(Invocation invocation) {
        //Executor的update  方法有两个参数, 此处不再额外的判断
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        Object parameter = args[1];
        switch (sqlCommandType) {
            case UPDATE:
                fillUpdateCommonField.accept(parameter);
                break;
            case INSERT:
                fillInsertCommonField.accept(parameter);
                break;
            default:
                break;
        }
    }
}
