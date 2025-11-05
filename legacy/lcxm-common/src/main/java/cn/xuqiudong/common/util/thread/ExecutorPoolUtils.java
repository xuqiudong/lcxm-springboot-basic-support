package cn.xuqiudong.common.util.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述: 线程池工具类
 * @author Vic.xu
 * @since 2022-02-28 10:10
 */
public final class ExecutorPoolUtils {

    /**
     * 工具类  不要new
     */
    private ExecutorPoolUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * cpu核心数
     */
    private static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors();

    /**
     * 创建一个线程池, 简单的重载 {@link #createExecutor(int, int, int, String)}
     * @param capacity  队列长度
     * @param threadName  线程名
     * @return Executor
     */
    public static Executor createExecutor(int capacity, String threadName) {

        return createExecutor(THREAD_NUMBER, THREAD_NUMBER, capacity, threadName);
    }

    /**
     * 创建一个线程池
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线城市
     * @param capacity 队列长度
     * @param threadName 线程名
     * @return Executor
     */
    public static Executor createExecutor(int corePoolSize, int maximumPoolSize, int capacity, String threadName) {

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(capacity), new CustomThreadFactory(threadName));

    }
}
