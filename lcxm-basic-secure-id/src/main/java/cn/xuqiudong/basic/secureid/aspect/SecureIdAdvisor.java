package cn.xuqiudong.basic.secureid.aspect;

import cn.xuqiudong.basic.secureid.annotation.EnableSecureId;
import org.aopalliance.aop.Advice;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.util.StringUtils;

/**
 * Description:
 * id 加密处理的 切面
 * 顾问 持有一个切入点和一个通知
 * 支持  EnableSecureId 注解的方法和自定义切面
 *
 * @author Vic.xu
 * @see EnableSecureId
 * @since 2026-08-20 15:32
 */
public class SecureIdAdvisor extends AbstractPointcutAdvisor {


    /**
     * 切入点表达式
     */
    private String pointcutExpression;

    private Pointcut pointcut;


    private AbstractSecureIdAdvice secureIdAdvice;


    /**
     * 构造函数
     * 默认切 EnableSecureId注解
     *
     * @param secureIdAdvice 通知
     */
    public SecureIdAdvisor(AbstractSecureIdAdvice secureIdAdvice) {
        this(null, secureIdAdvice);
    }

    /**
     * 构造函数
     *
     * @param pointcutExpression 切入点表达式 可不传， 则只切EnableSecureId注解 否则组合判断
     * @param secureIdAdvice     通知
     */
    public SecureIdAdvisor(@Nullable String pointcutExpression,
                           @NonNull AbstractSecureIdAdvice secureIdAdvice) {
        this.pointcutExpression = pointcutExpression;
        this.pointcut = initPointcut();
        this.secureIdAdvice = secureIdAdvice;
    }

    /**
     * 初始化 id 加密通知切入点。
     * 如果没有自定义切入点，则只处理 {@link EnableSecureId} 注解方法；
     * 如果存在自定义切入点，则和注解切入点取并集。
     */
    public Pointcut initPointcut() {
        AnnotationMatchingPointcut annotationMethodMatcher = AnnotationMatchingPointcut.forMethodAnnotation(EnableSecureId.class);
        if (!StringUtils.hasText(pointcutExpression)) {
            return annotationMethodMatcher;
        }
        AspectJExpressionPointcut expressionPointcut = new AspectJExpressionPointcut();
        expressionPointcut.setExpression(pointcutExpression);
        // 注解切点和自定义表达式取并集。
        ComposablePointcut composablePointcut = new ComposablePointcut(annotationMethodMatcher);
        composablePointcut.union((Pointcut) expressionPointcut);
        return composablePointcut;
    }

    @Override
    public Pointcut getPointcut() {
        if (pointcut == null) {
            pointcut = initPointcut();
        }
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return secureIdAdvice;
    }
}
