package cn.xuqiudong.basic.core.util.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 异步操作, 应该注入到spring, 利用spring的单例bean特性进行一步操作
 * 使用方法：
 * 1. 注入AsyncOperation 到spring， 或者new 一个AsyncOperation
 * 2. AsyncOperation.put(Runnable)
 * @author Vic.xu
 *
 */
public class AsyncOperation extends Thread {

    /**
     * 给BlockingQueue一个大小值, 防止线程堆积
     */
    private static final Integer CAPACITY = 200;

    private final Logger logger = LoggerFactory.getLogger(AsyncOperation.class);
    /**
     * 满了之后会阻塞
     */
    private final BlockingQueue<Runnable> sharedQueue = new LinkedBlockingQueue<>(CAPACITY);

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        for (; ; ) {
            try {
                Runnable runnable = sharedQueue.take();
                runnable.run();
            } catch (Exception ex) {
                logger.error("忽略异步操作错误:{}", ex.getMessage(), ex);
            }
        }
    }

    public void put(Runnable runnable) {
        try {
            sharedQueue.put(runnable);
            notifyNewPut();
        } catch (InterruptedException ex) {
            throw new RuntimeException("加入异步操作出错", ex);
        }
    }

    private synchronized void notifyNewPut() {
        if (!isAlive()) {
            setDaemon(true);
            start();
        }
    }

}