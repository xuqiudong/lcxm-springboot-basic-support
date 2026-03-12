package cn.xuqiudong.basic.framework.aspect;

import cn.xuqiudong.basic.framework.aspect.advice.SerialRequestAdvice;
import cn.xuqiudong.basic.framework.aspect.annotation.SerialRequest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.aopalliance.aop.Advice;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotNull;

/**
 * 描述: 串行化请求切面
 * 使用方法：
 * 1. 通过@Bean把SerialRequestAdvisor注册到spring容器，并传入切点与redissonClient
 * 2. 在需要串行化处理的方法上加上SerialRequest注解
 *
 * @author Vic.xu
 * @see SerialRequest
 * @since 2024-02-02 11:15
 */
@SuppressFBWarnings(value = "SE_BAD_FIELD")
public class SerialRequestAdvisor extends AbstractPointcutAdvisor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialRequestAdvisor.class);

    private static final long serialVersionUID = 1L;

    /**
     * 切入点表达式
     */
    private final String pointcutExpression;

    private final RedissonClient redissonClient;


    @SuppressFBWarnings(value = "SE_BAD_FIELD")
    private Pointcut serialRequestPointcut;

    private SerialRequestAdvice serialRequestAdvice;

    public SerialRequestAdvisor(@Nullable String pointcutExpression,
                                @NotNull RedissonClient redissonClient) {
        LOGGER.info("SerialRequestAdvisor initialization!");
        this.pointcutExpression = pointcutExpression;
        this.redissonClient = redissonClient;
        initPointcut();
        initAdvice();
    }


    @Override
    public Pointcut getPointcut() {
        return serialRequestPointcut;
    }

    @Override
    public Advice getAdvice() {
        return serialRequestAdvice;
    }


    /**
     * 初始化 串行化请求的通知 切入点：
     * 如果没有自定义切入点，则只需要 SerialRequest注解
     */
    public void initPointcut() {
        AnnotationMatchingPointcut annotationMethodMatcher = AnnotationMatchingPointcut.forMethodAnnotation(SerialRequest.class);
        if (!StringUtils.hasText(pointcutExpression)) {
            this.serialRequestPointcut = annotationMethodMatcher;
            return;
        }
        AspectJExpressionPointcut expressionPointcut = new AspectJExpressionPointcut();
        expressionPointcut.setExpression(pointcutExpression);
        //复合切入点
        ComposablePointcut pointcut = new ComposablePointcut();
        pointcut.intersection(annotationMethodMatcher).intersection(pointcut);
        this.serialRequestPointcut = pointcut;
    }

    /**
     * 初始化串行话处理逻辑
     */
    public void initAdvice() {
        this.serialRequestAdvice = new SerialRequestAdvice(redissonClient);
    }
}
