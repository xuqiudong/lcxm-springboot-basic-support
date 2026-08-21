package cn.xuqiudong.basic.secureid.aspect;

import org.jspecify.annotations.Nullable;
import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * Description:
 * id 加密的切面处理 的基类
 * 暂定在return之后， 所以只修改引用类 内部的id
 * @author Vic.xu
 * @since 2026-08-20 17:46
 */
public abstract class AbstractSecureIdAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable {

    }

    // 1  没有request 跳过
    // 2  匿名的url直接 跳过
    // 3  有跳过注解 也 逃过
    // 4 是否是通用的响应值BaseResponse，如果不是 也跳过(如何交给子类判断呢)
    // 5 开始处理 返回值， 比如BaseResponse 中的data



    // 处理响应值 记录深度
    // 1 超过层级 或者为null 则跳过
    // 2 如果是单对象， 判断是否 IdEncryptable
    // 3 如果是特殊对象(比如项目中业务常见的类型： 分页model 等)， 是不是交给业务子类自己处理
    // 4 如果是集合， 循环递归处理(传递深度)
    // 5 map  则变量values 递归处理
    // 6 遍历 对象的class, 获取属性，判断属性类型，判断注解，按需加密或 递归



}
