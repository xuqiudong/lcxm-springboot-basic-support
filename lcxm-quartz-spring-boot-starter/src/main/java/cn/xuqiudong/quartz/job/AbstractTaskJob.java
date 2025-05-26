package cn.xuqiudong.quartz.job;

import cn.xuqiudong.quartz.annotation.TaskJobFlag;
import cn.xuqiudong.quartz.model.TaskJobHandlerModel;
import cn.xuqiudong.quartz.model.TaskJobLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


/**
 * description：
 * 所有定时任务的基类，该类用于注册定时任务处理器， 处理器注册到spring
 * 在需要执行定时任务的函数上 加上 TaskJobFlag注解 ，并按需 设置参数 和定义函数
 *
 * @author Vic.xu
 * @see TaskJobFlag
 * @since 2024-09-25 17:56
 */
public abstract class AbstractTaskJob {


    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 注册定时任务处理器
     */
    public Map<String, TaskJobHandlerModel<?>> registerTasks(AbstractTaskJob selfBean) {
        Map<String, TaskJobHandlerModel<?>> taskJob = new HashMap<>();
        Class<? extends AbstractTaskJob> taskClazz = getClass();
        for (Method method : taskClazz.getDeclaredMethods()) {
            //1. 只处理public方法
            //2. 必须被标记为TaskJobFlag
            boolean isPublic = Modifier.isPublic(method.getModifiers());
            TaskJobFlag taskJobFlag = method.getAnnotation(TaskJobFlag.class);
            if (!isPublic || taskJobFlag == null) {
                continue;
            }

            //3. 返回值为String
            //4. 方法参数必须为 TaskJobLog
            boolean isValid = (String.class == method.getReturnType())
                    && (method.getParameterCount() == 1)
                    && (method.getParameterTypes()[0] == TaskJobLog.class);
            if (!isValid) {
                continue;
            }
            String code = taskJobFlag.value();
            String text = taskJobFlag.text();
            TaskJobHandlerModel<?> model = new TaskJobHandlerModel<>(taskJobFlag, selfBean, method);
            logger.info("注册定时任务[{}:{}]到统一任务入口类", code, text);
            taskJob.put(code, model);
        }
        return taskJob;
    }

}
