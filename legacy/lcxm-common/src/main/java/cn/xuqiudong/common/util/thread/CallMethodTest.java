package cn.xuqiudong.common.util.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 说明 :  超时抛出 异常
 *
 * @author Vic.xu
 * @since 2020年4月3日 下午5:42:34
 */
public class CallMethodTest {

    public static void main(String[] args) {
        test(2);
    }

    /**
     * 描述:
     *
     * @param outtime 超时时间 秒
     * @author Vic.xu
     * @since 2020年4月3日 下午5:57:23
     */
    public static void test(int outtime) {
        final ExecutorService exec = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(100), new CustomThreadFactory("CallMetho"));
        ;

        Callable<String> call = new Callable<String>() {
            @Override
            public String call() throws Exception {
                // 开始执行耗时操作
                // do something
                Thread.sleep(1000 * 5);
                return "5s后线程执行完成.";
            }
        };

        try {
            Future<String> future = exec.submit(call);
            // 任务处理超时时间设为 1 秒
            String obj = future.get(outtime, TimeUnit.MILLISECONDS);
            System.out.println("任务成功返回:" + obj);
        } catch (TimeoutException ex) {
            System.out.println("处理超时啦....");
            ex.printStackTrace();
        } catch (Exception e) {
            System.out.println("处理失败.");
            e.printStackTrace();
        }
        // 关闭线程池
        exec.shutdown();
    }

}
