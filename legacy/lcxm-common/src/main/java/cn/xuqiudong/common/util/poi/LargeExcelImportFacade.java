package cn.xuqiudong.common.util.poi;

import cn.xuqiudong.common.util.thread.ExecutorPoolUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 说明 :  通过POI SAX 事件驱动的解析xml方案 读取EXCEL大批量数据; 只支持2007
 *               使用方式LargeExcelImport.init(inputStream).readSheet(.....);readSheet方法支持链式调用
 * @author  Vic.xu
 * @since  2019年12月16日 上午10:02:07
 */
public class LargeExcelImportFacade {

    private final static Logger logger = LoggerFactory.getLogger(LargeExcelImportFacade.class);

    public static Executor executor = ExecutorPoolUtils.createExecutor(200, LargeExcelImportFacade.class.getSimpleName());

    /**
     * 标题行号 表头行号 数据行号 = 表头行号 + 1 支持读取多sheet 或者单sheet Map<sheetIndex, sheetData>
     * sheet.handler
     *
     */
    /*
     * 大体的开发思路(Vic.xu): 标题行号 @Deprecated 废弃,导入一般应该用不到标题 表头行号 必须 数据行号 = 表头行号 + 1
     * 支持读取多sheet 传入sheet下标 以回调的方式消费读取的信息:边生产边消费,是典型的生产消费者模式
     * 使用方式:LargeExcelImport2.init(inputStream).readSheet(sheetIndex,headerNumber,
     * callback); readSheet方法支持链式调用;
     *
     * 可完善的地方:
     * 1. 暂时的回调是每一行数据的时候都回调, 也可以考虑生产一定的buffer时候才回调
     * 2. 暂时的回调时候传入的数据是:Map<String, String> rowData 即每一行的 列头对应cell单元格的值的map
     *   可考虑在回调接口中多回调一个方法:用于处理数据,则处理数据回调可以支持泛型
     * 3. 因为暂时返回的rowData是map格式的所以不支持重复的列头名称
     *
     */

    private LargeExcelImport largeExcelImport;

    /**
     * 初始化门面
     */
    public static LargeExcelImportFacade init(InputStream sourceStream) throws Exception {
        LargeExcelImportFacade facade = new LargeExcelImportFacade();
        facade.largeExcelImport = LargeExcelImport.init(sourceStream);
        return facade;
    }

    /**
     * 读取某个sheet
     * @param sheetIndex   sheet的下标 从1开始
     * @param headerNumber 列头所在行 从1 开始
     * @param callback 真正使用数据的回调
     * @return LargeExcelImportFacade
     * @throws Exception ex
     */
    public LargeExcelImportFacade readSheet(int sheetIndex, int headerNumber, Consumer<Map<String, String>> callback)
            throws Exception {
        largeExcelImport.readSheet(sheetIndex, headerNumber, callback);
        return this;
    }

    @SuppressWarnings("PMD")
    private static class LargeExcelImport {

        /**
         * XSSFReader
         */
        private XSSFReader reader;

        private SharedStrings sharedStringsTable;

        private StylesTable stylesTable;

        // Sax 的XMLReader
        private XMLReader xmlReader;

        public static LargeExcelImport init(InputStream sourceStream) throws Exception {
            LargeExcelImport dataImport = new LargeExcelImport();
            OPCPackage opcPackage = OPCPackage.open(sourceStream);// 以压缩包形式打开
            // 2. 创建XSSFReader
            XSSFReader reader = new XSSFReader(opcPackage);
            dataImport.reader = reader;
            // 3. 获取SharedStringsTable
            dataImport.sharedStringsTable = reader.getSharedStringsTable();
            // 4 StylesTable
            dataImport.stylesTable = reader.getStylesTable();
            // 5. 创建Sax 的XML Reader
            dataImport.xmlReader = XMLReaderFactory.createXMLReader();
            return dataImport;
        }

        /**
         * 读取某一个sheet
         *
         * @param sheetIndex   sheet的下标 从1开始
         * @param headerNumber 列头所在行 从1 开始
         * @throws IOException
         * @throws InvalidFormatException
         */
        public LargeExcelImport readSheet(int sheetIndex, int headerNumber, Consumer<Map<String, String>> callback)
                throws Exception {
            long start = System.currentTimeMillis();
            SheetData sheetData = new SheetData(sheetIndex, headerNumber, callback);
            XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(stylesTable, sharedStringsTable,
                    new LargeDataImportHandler(sheetData), false);
            xmlReader.setContentHandler(xmlHandler);
            /*
             * 这样读取sheet会因为sheet名称改变而读取不到 InputStream sheet = reader.getSheet("rId" +
             * sheetIndex); InputSource sheetSource = new InputSource(sheet);
             * xmlReader.parse(sheetSource); sheet.close();
             */
            SheetIterator iterator = (SheetIterator) reader.getSheetsData();
            int i = 1;
            while (iterator.hasNext()) {
                if (i == sheetIndex) {
                    InputStream inputStream = iterator.next();
                    InputSource source = new InputSource(inputStream);
                    xmlReader.parse(source);
                    TimeUnit.SECONDS.sleep(1);// 虽然不知道为什么,但是就想睡眠一秒 :)
                    sheetData.finishRead();
                    break;
                }
                iterator.next();
                i++;
            }
            long end = System.currentTimeMillis();
            logger.info("读取sheet{}耗时{}毫秒, 读取的数据行数为:{}", sheetIndex, (end - start), sheetData.countRows);
            if (!logger.isInfoEnabled()) {
                logger.info("读取sheet " + sheetIndex + " 耗时" + (end - start) + "毫秒, 读取的数据行数为:" + sheetData.countRows);
            }

            return this;
        }

        public static class SheetData {
            /** 存储数据的阻塞队列 */
            private BlockingQueue<Map<String, String>> blockingQueue = new ArrayBlockingQueue<>(300);
            /**
             * 表头所在行
             */
            private int headerNumber;
            /**
             * 表的列头行数据
             */
            private final List<String> headFields = new ArrayList<>(16);

            private int sheetIndex;

            /** 当前sheet的总行数 */
            private int countRows;

            /** sheet数据是否读取完毕 */
            private volatile boolean sheetEnd = false;
            /** 每行数据的真实处理逻辑会掉 */
            private Consumer<Map<String, String>> callback;

            /**
             * 消费的线程
             */
            private Thread consumerDataHandlerThread;

            public SheetData(int sheetIndex, int headerNumber, Consumer<Map<String, String>> callback) {
                this.sheetIndex = sheetIndex;
                this.headerNumber = headerNumber;
                this.callback = callback;
                if (callback != null) {
                    this.consumerDataHandlerThread = new Thread(new ConsumerDataHandler());
                    consumerDataHandlerThread.start();
                }
            }

            /**
             * 完成sheet的读取, 中断消费线程
             */
            public void finishRead() {
                this.sheetEnd = true;
                logger.info("下标为{}的sheet读取完毕", sheetIndex);
                if (consumerDataHandlerThread != null && !consumerDataHandlerThread.isInterrupted()) {
                    logger.info("完成sheet{}的读取, 中断消费线程", sheetIndex);
                    consumerDataHandlerThread.interrupt();
                }
            }

            /** 消费数据的线程 */
            private class ConsumerDataHandler implements Runnable {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Map<String, String> rowData = blockingQueue.take();
                            if (MapUtils.isNotEmpty(rowData)) {
                                callback.accept(rowData);
                            }
                        } catch (InterruptedException e) {
                            logger.debug("中断线程{}异常, 因为存在阻塞的blockingQueue, 重新判断blockingQueue的size,且进行后续处理",
                                    Thread.currentThread().getName());
                            if (blockingQueue.size() != 0) {
                                logger.debug("blockingQueue没有被消费完毕,继续消费处理");
                                try {
                                    Map<String, String> rowData = blockingQueue.take();
                                    if (MapUtils.isNotEmpty(rowData)) {
                                        callback.accept(rowData);
                                    }
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } finally {
                            if (sheetEnd && blockingQueue.size() == 0) {
                                logger.info("消费数据的线程结束");
                            }
                        }
                    }
                }

            }

        }

        public class LargeDataImportHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

            private SheetData sheetData;

            /** 每一行的数据 */
            private Map<String, String> lineMap;

            /** 当前行 */
            private int currentLine;
            /** 当前列 */
            private int currentColumn;
            /** 当前行是否是表头 */
            private boolean isHeader;

            public LargeDataImportHandler(SheetData sheetData) {
                this.sheetData = sheetData;
            }

            /**
             * 开始解析某一行数据时候触发
             */
            @Override
            public void startRow(int rowNum) {
                currentLine = rowNum + 1;
                isHeader = currentLine == sheetData.headerNumber;
                int initialCapacity = 16;
                if (Objects.nonNull(lineMap)) {
                    initialCapacity = lineMap.size();
                }
                lineMap = new HashMap<>(initialCapacity);
            }

            /**
             * 结束解析某一行时候触发
             */
            @Override
            public void endRow(int rowNum) {
                if (currentLine <= sheetData.headerNumber) {
                    return;
                }
                sheetData.blockingQueue.add(lineMap);
            }

            /**
             * 每个单元格数据
             */
            @Override
            public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                currentColumn = columnIndexFormString(cellReference.replaceAll(String.valueOf(currentLine), ""));
                if (isHeader) {
                    sheetData.headFields.add(formattedValue);
                } else {
                    if (currentLine > sheetData.headerNumber && sheetData.headFields.size() > currentColumn) {
                        lineMap.put(sheetData.headFields.get(currentColumn), formattedValue);
                    }
                }
            }

            @Override
            public void endSheet() {
                logger.info("下标为{}的sheet读取完毕", sheetData.sheetIndex);
                sheetData.sheetEnd = true;
                sheetData.countRows = currentLine;
                sheetData.finishRead();
            }

        }

        // 单元格列转数值映射MAP: A->0, B->1....
        private static final HashMap<String, Integer> COLUMN_INDEX_CACHE = new HashMap<>();

        /**
         * 单元格转数字 A:1 Z:26 AA:27
         *
         * @param column
         * @return
         * @throws Exception
         */
        public static int columnIndexFormString(String column) throws IllegalArgumentException {
            if (COLUMN_INDEX_CACHE.containsKey(column)) {
                return COLUMN_INDEX_CACHE.get(column);
            }
            int index;
            char[] pStrings = column.toCharArray();
            if (pStrings.length == 1) {
                index = pStrings[0] - 'A';
            } else if (pStrings.length == 2) {
                index = (pStrings[0] - 'A' + 1) * 26 + pStrings[1] - 'A';
            } else if (pStrings.length == 3) {
                index = ((pStrings[0] - 'A') * 676) + ((pStrings[1] - 'A') * 26) + pStrings[2] - 'A';
            } else {
                throw new IllegalArgumentException("not support convert to number");
            }
            COLUMN_INDEX_CACHE.put(column, index);
            return index;
        }

    }

    @SuppressFBWarnings
    public static void main(String[] args) throws Exception {

        InputStream in;//= LargeExcelImport.class.getClassLoader().getResourceAsStream("abc.xlsx");

        String file = "D:/desk/期末考试/19电商1班/学生考勤情况统计-19电商1班.xlsx";
        in = new FileInputStream(new File(file));
        List<Map<String, String>> list = new ArrayList<>();
        Consumer<Map<String, String>> consumer = e -> {
            System.out.println(e);
            list.add(e);
        };
        LargeExcelImportFacade.init(in).readSheet(5, 2, consumer);
        System.out.println(list.size());
    }

}
