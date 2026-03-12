package cn.xuqiudong.basic.framework.aspect.advice;


import cn.xuqiudong.basic.framework.aspect.annotation.SerialRequest;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

/**
 * 描述: 通过分布式锁串行化请求切面，根据请求中的业务标识添加锁
 * 如操作用户时候，针对同一个用户id，同一时刻只能被一个请求操作
 * @author Vic.xu
 * @since 2024-02-02 10:28
 */
public class SerialRequestAdvice implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialRequestAdvice.class);

    private final SpelExpressionParser spelParser = new SpelExpressionParser();

    private final RedissonClient redissonClient;

    public SerialRequestAdvice(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 串行化切面的处理：加锁和释放锁
     * 通过redisson锁住资源，直到业务处理完毕后才释放
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        SerialInfo serialInfo = initSerialInfo(invocation);
        String lockName = serialInfo.lockName;
        RLock lock = redissonClient.getLock(lockName);
        try {
            //此处不设置加锁时间， 通过看门狗默认一直延期锁的生命周期，直到业务流程处理完毕
            boolean obtained = lock.tryLock(serialInfo.waitTime, TimeUnit.SECONDS);
            if (!obtained) {
                throw new RuntimeException("当前资源[" + lockName + "]正在被其他人操作，请稍后再试!");
            }
            LOGGER.info("SerialRequest 锁定 {}", lockName);
            return invocation.proceed();
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
                LOGGER.info("SerialRequest 释放 {}", lockName);
            }

        }
    }

    /**
     * 初始化加锁的相关信息
     */
    private SerialInfo initSerialInfo(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        SerialRequest serialRequest = method.getAnnotation(SerialRequest.class);

        String lockParameter = serialRequest.lockParameter();
        Parameter[] parameters = method.getParameters();
        String lockParameterValue = null;

        if (!ObjectUtils.isEmpty(parameters) && StringUtils.hasLength(lockParameter)) {
            //将方法的参数名和参数值一一对应的放入上下文中
            EvaluationContext ctx = new StandardEvaluationContext();
            Object[] arguments = invocation.getArguments();
            for (int i = 0; i < parameters.length; i++) {
                String name = parameters[i].getName();
                Object argument = arguments[i];
                ctx.setVariable(name, argument);
            }
            lockParameterValue = String.valueOf(spelParser.parseExpression(lockParameter).getValue(ctx));
        }
        return new SerialInfo(serialRequest, lockParameterValue);
    }

    /**
     * 串行化的相关数据
     */
    static class SerialInfo {

        String lockName;

        long waitTime;

        SerialInfo(SerialRequest serialRequest, String lockParameterValue) {
            if (!StringUtils.hasLength(lockParameterValue)) {
                lockParameterValue = "none";
            }
            this.lockName = "sr:" + serialRequest.name() + ":" + lockParameterValue;
            this.waitTime = serialRequest.waitTimeSeconds();
        }
    }

}
