package cn.xuqiudong.basic.quartz.entry;

import cn.xuqiudong.basic.quartz.annotation.TaskJobFlag;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.job.AbstractTaskJob;
import cn.xuqiudong.basic.quartz.model.TaskJobExecutionResult;
import cn.xuqiudong.basic.quartz.model.TaskJobHandlerModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在启动时扫描并注册定时任务，跳过不符合新签名的方法。
 *
 * @author Vic.xu
 * @since 2026-10-28
 */
public class TaskHandlerRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHandlerRegistry.class);

    /**
     * 启动时构建的任务编码索引，执行阶段不再修改。
     */
    private final Map<String, TaskJobHandlerModel> handlers;

    /**
     * 注册 Spring 中所有任务基类实例上标注了 {@link TaskJobFlag} 的方法。
     * 签名或编码无效的方法记录警告并跳过；编码重复时保留先注册的方法并告警。
     *
     * @param jobs 已由 Spring 创建的任务实例
     */
    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW",
            justification = "任务注册中心必须在构造阶段完成反射扫描，避免对外暴露未初始化的任务索引")
    public TaskHandlerRegistry(List<AbstractTaskJob> jobs) {
        Map<String, TaskJobHandlerModel> registered = new HashMap<>();
        for (AbstractTaskJob job : jobs) {
            // 从目标类读取注解和方法，调用时仍使用 Spring 管理的实例。
            for (Method method : AopProxyUtils.ultimateTargetClass(job).getDeclaredMethods()) {
                TaskJobFlag flag = method.getAnnotation(TaskJobFlag.class);
                if (flag == null) {
                    continue;
                }
                if (!isValidTaskMethod(method, flag)) {
                    LOGGER.warn("跳过无效的定时任务方法：{}，要求 public 非静态方法，"
                                    + "接收一个 TaskJobLog 参数并返回 TaskJobExecutionResult，任务编码非空",
                            method);
                    continue;
                }
                TaskJobHandlerModel handler = new TaskJobHandlerModel(job, method, flag.value(), flag.text(),
                        flag.logPolicy());
                TaskJobHandlerModel existing = registered.putIfAbsent(flag.value(), handler);
                if (existing != null) {
                    LOGGER.warn("跳过重复的定时任务编码：{}，已有任务描述：{}，重复处理方法：{}",
                            flag.value(), existing.getText(), method);
                }
            }
        }
        this.handlers = Map.copyOf(registered);
    }

    /**
     * 校验任务签名：public、非静态、单个 TaskJobLog 参数、结构化执行结果及非空编码。
     *
     * @param method 待注册的方法
     * @param flag   方法上的任务标记
     * @return 方法签名及任务编码是否符合约定
     */
    private boolean isValidTaskMethod(Method method, TaskJobFlag flag) {
        boolean validSignature = Modifier.isPublic(method.getModifiers())
                && !Modifier.isStatic(method.getModifiers())
                && method.getReturnType() == TaskJobExecutionResult.class
                && method.getParameterCount() == 1
                && method.getParameterTypes()[0] == TaskJobLog.class;
        return validSignature && StringUtils.isNotBlank(flag.value());
    }

    /**
     * 根据任务编码查找已校验的执行方法。
     *
     * @param code 任务编码
     * @return 对应的处理器，未注册时返回 null
     */
    public TaskJobHandlerModel get(String code) {
        return handlers.get(code);
    }

    /**
     * 判断任务编码是否已注册。
     *
     * @param code 任务编码
     * @return 已注册返回 true
     */
    public boolean contains(String code) {
        return handlers.containsKey(code);
    }
}
