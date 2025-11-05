package cn.xuqiudong.common.util.thread;

import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 描述:多线程批量处理 ☆☆☆
 *                      如果要用集合收集处理结果，切记使用线程安全的集合，如CopyOnWriteArrayList，ConcurrentHashMap等☆☆☆
 *                      使用方法：BatchExecteOperation.init([int
 *                      partitionSize]).execute(List batchList,
 *                      BatchExecteOperationCallback callback).shutdown();
 * @author  Vic.xu
 * @since  2020年3月11日 上午10:32:25
 */
@SuppressWarnings("PMD")
public class BatchExecuteOperation {

    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * list分割的默认大小
     */
    private int partitionSize = 40;
    /**
     * 是否是因异常终止
     */
    private volatile boolean terminationByException = false;
    /**
     * 因异常终止的信息
     */
    private volatile String terminationMsg = "";

    private BatchExecuteOperation() {
    }

    /**
     *
     * 说明 :  初始化批量执行操作， 使用默认的分片size
     * @author  Vic.xu
     * @since  2020年3月11日 下午1:00:59
     * @return
     */
    public static BatchExecuteOperation init() {

        return init(DEFAULT_PARTITION_SIZE);
    }

    /**
     *
     * 说明 :  初始化批量执行操作
     * @author  Vic.xu
     * @since  2020年3月11日 上午11:23:47
     * @param partitionSize list数据需要分割的默认大小(每个部分的数据大小)
     * @return
     */
    public static BatchExecuteOperation init(int partitionSize) {
        BatchExecuteOperation operation = new BatchExecuteOperation();
        operation.partitionSize = partitionSize;
        operation.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new CustomThreadFactory("BatchExecteOperation"));
        return operation;
    }

    /**
     *
     * 说明 :  执行批量操作，多线程执行完毕会自动关闭；
     * @author  Vic.xu
     * @since  2020年3月11日 下午12:42:46
     * @param batchList 需要批量操作的对象list，会根据partitionSize切分成n个list， 然后启动n个线程同步执行
     * @param callback  ☆☆☆对切片后的list进行处理，如果要用集合收集处理结果，切记使用线程安全的集合，如CopyOnWriteArrayList，ConcurrentHashMap等
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public  <T> BatchExecuteOperation execute(List<T> batchList, Consumer<List<T>> callback) {
        // 切片成lists.size()个 并启动size 个线程
        List<List<T>> lists = ListUtils.partition(batchList, partitionSize);
        lists.forEach(partition -> {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (terminationByException) {
                        return;
                    }
                    try {
                        callback.accept(partition);
                    } catch (Exception e) {
                        /**
                         * 标记线程异常 并记录异常MSG,另终止线程
                         * 在shutdown方法中抛出异常
                         */
                        terminationByException = true;
                        terminationMsg = e.getMessage();
                        threadPoolExecutor.shutdown();

                    }

                }

            });
        });
        threadPoolExecutor.shutdown();
        return this;
    }

    /**
     *
     * 说明 :  阻塞，等待线程池中的线程全部执行完毕
     * @author  Vic.xu
     * @since  2020年3月11日 下午12:43:15
     * @return
     * @throws InterruptedException
     */
    public boolean shutdown() throws Exception {


        if (!threadPoolExecutor.isShutdown()) {
            threadPoolExecutor.shutdown();
        }
        //如果线程没有终止，并且也没有异常则一直阻塞
        while (!threadPoolExecutor.awaitTermination(2, TimeUnit.SECONDS) && !terminationByException) {
        }

        if (terminationByException) {
            throw new RuntimeException(terminationMsg);
        }

        return true;
    }

    /**
     * 测试使用
     */
    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = Arrays.stream("1,2,3,4,5,6,7,8,9".split(",")).map(Integer::valueOf)
                .collect(Collectors.toList());

        List<Integer> result = new CopyOnWriteArrayList<Integer>();
        long t1 = System.currentTimeMillis();
        System.out.println(t1);
        try {

            BatchExecuteOperation.init(3).execute(list, partions->{
                partions.forEach(a -> {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (a == 5) {
                            System.out.println("a=" + a);
                            throw new RuntimeException("5555555555555555");
                        }
                        System.out.println("add to result-->" + a);

                        result.add(a * 2);
                    });
            }).shutdown();

        } catch (Exception e) {
            System.err.println("main" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        long t2 = System.currentTimeMillis();
        System.out.println(t2);
        System.out.println("耗时：" + ((t2 - t1) / 1000) + "s");
        System.out.println(result.size());
        result.forEach(a -> {
            System.out.print(a + "\t");
        });
    }

    private static int corePoolSize = 4;

    private static int maximumPoolSize = 4;

    /**
     *  单位秒
     */
    private static int KEEP_ALIVE_TIME = 30;

    /**
     * list分割的默认大小
     */
    private static final int DEFAULT_PARTITION_SIZE = 40;

    static {
        corePoolSize = Runtime.getRuntime().availableProcessors();

        maximumPoolSize = corePoolSize;
    }

}
