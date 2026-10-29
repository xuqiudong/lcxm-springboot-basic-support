package cn.xuqiudong.basic.quartz.job;

import cn.xuqiudong.basic.quartz.annotation.TaskJobFlag;


/**
 * description：
 * 所有定时任务的基类，任务处理器注册到 Spring。
 * <p>
 * 在 public 非静态方法上标记 TaskJobFlag，接收 TaskJobLog 参数并返回 TaskJobExecutionResult。
 * 方法扫描和签名校验由任务注册中心负责。
 * 一个子类可以定义多个相关任务方法。
 * </p>
 *
 * @author Vic.xu
 * @see TaskJobFlag
 * @since 2024-09-25 17:56
 */
public abstract class AbstractTaskJob {


}
