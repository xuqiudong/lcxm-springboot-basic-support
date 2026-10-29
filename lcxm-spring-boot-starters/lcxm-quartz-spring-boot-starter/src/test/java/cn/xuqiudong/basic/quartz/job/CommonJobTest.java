package cn.xuqiudong.basic.quartz.job;

import cn.xuqiudong.basic.quartz.annotation.TaskJobFlag;
import cn.xuqiudong.basic.quartz.constant.QuartzConstant;
import cn.xuqiudong.basic.quartz.entity.TaskJob;
import cn.xuqiudong.basic.quartz.entity.TaskJobLog;
import cn.xuqiudong.basic.quartz.entry.TaskHandlerRegistry;
import cn.xuqiudong.basic.quartz.entry.UnifyTaskEntry;
import cn.xuqiudong.basic.quartz.enums.TaskJobLogPolicy;
import cn.xuqiudong.basic.quartz.enums.TaskJobResult;
import cn.xuqiudong.basic.quartz.helper.JobUserHolder;
import cn.xuqiudong.basic.quartz.model.TaskJobExecutionResult;
import cn.xuqiudong.basic.quartz.service.TaskJobLogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证实际注册和反射调用链上的日志策略、异常传播及执行用户清理，不依赖数据库。
 *
 * @author Vic.xu
 * @since 2026-10-29
 */
@Tag("manual")
class CommonJobTest {

    /**
     * 记录主日志创建、业务调用和日志收尾的实际顺序。
     */
    private final List<String> events = new ArrayList<>();

    /**
     * 同一个类中声明两种策略的任务。
     */
    private final BusinessJob business = new BusinessJob(events);

    /**
     * 记录日志调用，并可模拟持久化异常。
     */
    private final RecordingLogService logs = new RecordingLogService(events);

    /**
     * 待验证的 Quartz 总入口。
     */
    private final CommonJob commonJob = new CommonJob();

    /**
     * 使用真实注册中心和统一入口，只替换数据库日志服务。
     *
     * @throws ReflectiveOperationException 注入测试依赖失败
     */
    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        UnifyTaskEntry entry = new UnifyTaskEntry(new TaskHandlerRegistry(List.of(business)));
        inject("unifyTaskEntry", entry);
        inject("taskJobLogService", logs);
    }

    /**
     * 所有正常及异常路径都必须释放线程用户信息。
     */
    @AfterEach
    void verifyUserCleared() {
        assertTrue(JobUserHolder.getUserId().isEmpty());
    }

    /**
     * 默认策略保留成功、主动失败和跳过三种结果。
     *
     * @param result 本次业务结论
     * @throws JobExecutionException 任务执行异常
     */
    @ParameterizedTest
    @EnumSource(TaskJobResult.class)
    void allPolicyKeepsEveryResult(TaskJobResult result) throws JobExecutionException {
        business.result = resultOf(result);
        commonJob.executeInternal(context("all"));
        assertEquals(List.of("start", "business", "end:" + result), events);
        assertEquals(result, logs.savedResult);
        assertNotNull(logs.cost);
        assertTrue(logs.cost >= 0);
    }

    /**
     * 忽略策略只删除跳过记录，成功与主动失败仍正常完成主日志。
     *
     * @param result 本次业务结论
     * @throws JobExecutionException 任务执行异常
     */
    @ParameterizedTest
    @EnumSource(TaskJobResult.class)
    void ignorePolicyDeletesOnlySkipped(TaskJobResult result) throws JobExecutionException {
        business.result = resultOf(result);
        commonJob.executeInternal(context("ignore"));
        String finishEvent = result == TaskJobResult.SKIPPED ? "delete:log-id" : "end:" + result;
        assertEquals(List.of("start", "business", finishEvent), events);
    }

    /**
     * 反射包装应被解开，Quartz 收到真实业务异常，但不立即重试或取消后续调度。
     */
    @Test
    void propagatesOriginalBusinessExceptionWithoutReschedulingFlags() {
        RuntimeException failure = new IllegalStateException("业务异常");
        business.failure = failure;
        JobExecutionException thrown = assertThrows(JobExecutionException.class,
                () -> commonJob.executeInternal(context("ignore")));
        assertSame(failure, thrown.getCause());
        assertFalse(thrown.refireImmediately());
        assertFalse(thrown.unscheduleFiringTrigger());
        assertFalse(thrown.unscheduleAllTriggers());
        assertEquals(List.of("start", "business", "end:FAILURE"), events);
        assertEquals(TaskJobResult.FAILURE, logs.savedResult);
        assertTrue(logs.message.contains("业务异常"));
    }

    /**
     * 失败日志也保存失败时，原始业务异常仍为主异常。
     */
    @Test
    void preservesBusinessFailureWhenFailureLogAlsoFails() {
        business.failure = new IllegalArgumentException("业务失败");
        logs.writeFailure = new IllegalStateException("日志保存失败");
        JobExecutionException thrown = assertThrows(JobExecutionException.class,
                () -> commonJob.executeInternal(context("all")));
        assertSame(business.failure, thrown.getCause());
        assertEquals(1, thrown.getCause().getSuppressed().length);
        assertSame(logs.writeFailure, thrown.getCause().getSuppressed()[0]);
    }

    /**
     * 保存或删除日志失败不能再按业务失败重写主日志。
     *
     * @param policy 本次日志策略
     */
    @ParameterizedTest
    @EnumSource(TaskJobLogPolicy.class)
    void reportsLogFailureWithoutRewritingBusinessResult(TaskJobLogPolicy policy) {
        business.result = TaskJobExecutionResult.skipped("无需处理");
        logs.writeFailure = new IllegalStateException("日志收尾失败");
        String code = policy == TaskJobLogPolicy.ALL ? "all" : "ignore";
        JobExecutionException thrown = assertThrows(JobExecutionException.class,
                () -> commonJob.executeInternal(context(code)));
        assertSame(logs.writeFailure, thrown.getCause());
        assertTrue(thrown.getMessage().contains("业务结果：SKIPPED"));
        String finishEvent = policy == TaskJobLogPolicy.ALL ? "end:SKIPPED" : "delete:log-id";
        assertEquals(List.of("start", "business", finishEvent), events);
    }

    /**
     * 主日志创建失败时不进入业务，且执行用户仍会被清理。
     */
    @Test
    void doesNotExecuteBusinessWhenStartLogFails() {
        logs.startFailure = new IllegalStateException("创建主日志失败");
        JobExecutionException thrown = assertThrows(JobExecutionException.class,
                () -> commonJob.executeInternal(context("all")));
        assertSame(logs.startFailure, thrown.getCause());
        assertEquals(List.of("start"), events);
    }

    /**
     * 空返回值属于任务实现错误，应记录失败并上报，而非默认为成功。
     */
    @Test
    void rejectsNullBusinessResult() {
        business.result = null;
        JobExecutionException thrown = assertThrows(JobExecutionException.class,
                () -> commonJob.executeInternal(context("all")));
        assertTrue(thrown.getCause() instanceof NullPointerException);
        assertEquals(TaskJobResult.FAILURE, logs.savedResult);
    }

    /**
     * 任务未注册时在创建主日志前报告错误。
     */
    @Test
    void rejectsMissingHandlerBeforeCreatingLog() {
        JobExecutionException thrown = assertThrows(JobExecutionException.class,
                () -> commonJob.executeInternal(context("missing")));
        assertTrue(thrown.getCause() instanceof IllegalStateException);
        assertTrue(events.isEmpty());
    }

    /**
     * 为参数化测试构造业务能够公开返回的三种结果。
     *
     * @param result 执行结论
     * @return 对应结果对象
     */
    private TaskJobExecutionResult resultOf(TaskJobResult result) {
        return switch (result) {
            case SUCCESS -> TaskJobExecutionResult.success("完成");
            case FAILURE -> TaskJobExecutionResult.failure("主动失败");
            case SKIPPED -> TaskJobExecutionResult.skipped("没有变化");
        };
    }

    /**
     * 构造入口实际使用的 Quartz 上下文，提供任务定义与执行用户。
     *
     * @param code 已注册或待验证的任务编码
     * @return 调度上下文
     */
    private JobExecutionContext context(String code) {
        JobDetail detail = JobBuilder.newJob(CommonJob.class).withIdentity(code).build();
        JobDataMap data = JobUserHolder.buildJobDataMap("user-id", "测试用户");
        TaskJob task = new TaskJob();
        task.setName("测试任务");
        data.put(QuartzConstant.TASK_JOB_HOLDER_KEY, task);
        return (JobExecutionContext) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{JobExecutionContext.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "getJobDetail" -> detail;
                    case "getMergedJobDataMap" -> data;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    /**
     * 注入入口字段，避免为测试引入完整 Spring 容器与数据库。
     *
     * @param name  入口字段名
     * @param value 测试依赖
     * @throws ReflectiveOperationException 字段不存在或不可访问
     */
    private void inject(String name, Object value) throws ReflectiveOperationException {
        Field field = CommonJob.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(commonJob, value);
    }

    /**
     * 通过真实注解扫描和反射调用的业务任务。
     *
     * @author Vic.xu
     * @since 2026-10-29
     */
    public static class BusinessJob extends AbstractTaskJob {

        /**
         * 调用顺序记录。
         */
        private final List<String> events;

        /**
         * 本次业务返回值，可由测试设为空。
         */
        private TaskJobExecutionResult result = TaskJobExecutionResult.success("完成");

        /**
         * 待模拟的实际业务异常。
         */
        private RuntimeException failure;

        /**
         * @param events 调用顺序记录
         */
        BusinessJob(List<String> events) {
            this.events = events;
        }

        /**
         * @param log 框架已创建的主日志
         * @return 本次业务结果
         */
        @TaskJobFlag("all")
        public TaskJobExecutionResult all(TaskJobLog log) {
            return execute(log);
        }

        /**
         * @param log 框架已创建的主日志
         * @return 本次业务结果
         */
        @TaskJobFlag(value = "ignore", logPolicy = TaskJobLogPolicy.IGNORE_SKIPPED)
        public TaskJobExecutionResult ignore(TaskJobLog log) {
            return execute(log);
        }

        /**
         * 确认业务调用前已有日志 ID 与执行用户，再返回结果或抛出异常。
         *
         * @param log 框架主日志
         * @return 本次业务结果
         */
        private TaskJobExecutionResult execute(TaskJobLog log) {
            assertEquals("log-id", log.getId());
            assertEquals("user-id", JobUserHolder.getUserIdNotNull());
            events.add("business");
            if (failure != null) {
                throw failure;
            }
            return result;
        }
    }

    /**
     * 记录持久化调用和结果，用于验证编排及异常行为；不模拟数据库事务。
     *
     * @author Vic.xu
     * @since 2026-10-29
     */
    private static class RecordingLogService extends TaskJobLogService {

        /**
         * 调用顺序记录。
         */
        private final List<String> events;

        /**
         * 创建日志时模拟的异常。
         */
        private RuntimeException startFailure;

        /**
         * 完成或删除日志时模拟的异常。
         */
        private RuntimeException writeFailure;

        /**
         * 主日志最后保存的业务结果。
         */
        private TaskJobResult savedResult;

        /**
         * 主日志最后保存的说明。
         */
        private String message;

        /**
         * 主日志记录的毫秒耗时。
         */
        private Long cost;

        /**
         * @param events 调用顺序记录
         */
        RecordingLogService(List<String> events) {
            this.events = events;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TaskJobLog startLog(TaskJob taskJob) {
            events.add("start");
            if (startFailure != null) {
                throw startFailure;
            }
            TaskJobLog log = new TaskJobLog();
            log.setId("log-id");
            log.setTaskJobName(taskJob.getName());
            return log;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TaskJobLog endLog(TaskJobLog log, TaskJobResult result, String note, Long costTime) {
            events.add("end:" + result);
            savedResult = result;
            message = note;
            cost = costTime;
            if (writeFailure != null) {
                throw writeFailure;
            }
            return log;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int delete(String id) {
            events.add("delete:" + id);
            if (writeFailure != null) {
                throw writeFailure;
            }
            return 1;
        }
    }
}
