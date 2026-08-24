package cn.xuqiudong.basic.secureid.config;

import cn.xuqiudong.basic.secureid.aspect.AbstractSecureIdAdvice;
import cn.xuqiudong.basic.secureid.aspect.SecureIdAdvisor;
import cn.xuqiudong.basic.secureid.filter.ParamDecryptFilter;
import cn.xuqiudong.basic.secureid.util.IdUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;

import java.util.List;
import java.util.function.Supplier;

/**
 * Description:
 * 平行越权 隐藏id 配置基类， 具体项目继承此类，并实现具体方法
 *
 * @author Vic.xu
 * @since 2026-08-20 16:44
 */
public abstract class AbstractSecureIdConfig {


    @PostConstruct
    public void init() {
        IdUtil.setSaltSupplier(saltSupplier());
    }


    /**
     * id 加密的盐值， 可以考虑使用 sessionId， username等动态值， 不然导致id固定， 依然会越权
     */
    public abstract Supplier<String> saltSupplier();


    /**
     * id 加密的切面处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public AbstractSecureIdAdvice secureIdAdvice() {
        return realSecureIdAdvice();
    }

    /**
     * id 加密的切面 配置
     */
    @Bean
    @ConditionalOnMissingBean
    public SecureIdAdvisor secureIdAdvisor(AbstractSecureIdAdvice secureIdAdvice) {
        return new SecureIdAdvisor(secureIdPointCutExpression(), secureIdAdvice);
    }

    /**
     * id 解密过滤器。
     * <p>
     * 通过配置类注入项目自身的 multipartResolver，避免 filter 内部从 Spring 上下文按名称查找。
     * 如果项目启用此配置但没有 multipartResolver，应启动失败，避免文件表单普通字段未解密却静默放行。
     */
    @Bean
    @ConditionalOnMissingBean
    public ParamDecryptFilter paramDecryptFilter(MultipartResolver multipartResolver) {
        return new ParamDecryptFilter(multipartResolver);
    }

    /**
     * id 解密过滤器注册配置。
     * <p>
     * 子类明确提供拦截路径、order、enabled，避免基础组件假设所有项目的过滤器顺序。
     */
    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<ParamDecryptFilter> paramDecryptFilterRegistration(ParamDecryptFilter paramDecryptFilter) {
        FilterRegistrationBean<ParamDecryptFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(paramDecryptFilter);
        registrationBean.setName("paramDecryptFilter");
        registrationBean.setUrlPatterns(paramDecryptFilterUrlPatterns());
        registrationBean.setOrder(paramDecryptFilterOrder());
        registrationBean.setEnabled(paramDecryptFilterEnabled());
        return registrationBean;
    }

    /**
     * 子类实现， 构建 id 加密的切面处理器
     */
    public abstract AbstractSecureIdAdvice realSecureIdAdvice();


    /**
     * 子类实现，id 加密的切面 表达式， 可为空， 则只处理 @EnableSecureId 注解的方法
     */
    public abstract String secureIdPointCutExpression();

    /**
     * 子类实现，id 解密过滤器拦截路径。
     * <p>
     * 常见值为 {@code List.of("/*")}；如果只想处理接口路径，可以返回如 {@code /api/*}。
     */
    public abstract List<String> paramDecryptFilterUrlPatterns();

    /**
     * 子类实现，id 解密过滤器顺序。
     * <p>
     * 如果 salt 依赖某个前置 filter 设置的用户上下文，此 order 必须晚于该前置 filter。
     * 如果希望在 controller 参数绑定前处理请求参数，此 order 必须早于 Spring MVC 进入 controller。
     */
    public abstract int paramDecryptFilterOrder();

    /**
     * 子类实现，是否启用 id 解密过滤器。
     */
    public abstract boolean paramDecryptFilterEnabled();
}
