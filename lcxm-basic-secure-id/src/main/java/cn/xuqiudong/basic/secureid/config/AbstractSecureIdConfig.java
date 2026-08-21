package cn.xuqiudong.basic.secureid.config;

import cn.xuqiudong.basic.secureid.aspect.AbstractSecureIdAdvice;
import cn.xuqiudong.basic.secureid.aspect.SecureIdAdvisor;
import cn.xuqiudong.basic.secureid.util.IdUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

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
        return buildSecureIdAdvice();
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
     * 子类实现， 构建 id 加密的切面处理器
     */
    public abstract AbstractSecureIdAdvice buildSecureIdAdvice();


    /**
     * 子类实现，id 加密的切面 表达式， 可为空， 则只处理 @EnableSecureId 注解的方法
     */
    public abstract String secureIdPointCutExpression();
}
