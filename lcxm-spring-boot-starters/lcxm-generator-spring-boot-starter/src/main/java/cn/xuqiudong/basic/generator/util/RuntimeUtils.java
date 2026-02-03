package cn.xuqiudong.basic.generator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * 描述:
 * 打开指定输出文件目录
 *
 * @author Vic.xu
 * @since 2025-09-16 11:41
 */
public class RuntimeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeUtils.class);

    /**
     * 打开指定输出文件目录
     *
     * @param outDir 输出文件目录
     */
    public static void openDir(String outDir) throws IOException {
        File file = new File(outDir);
        if (!file.isDirectory()) {
            LOGGER.error("illegal directory:{}", outDir);
            throw new IllegalArgumentException("Illegal directory " + outDir);
        }
        String osName = System.getProperty("os.name");
        if (osName != null) {
            if (osName.contains("Mac")) {
                Runtime.getRuntime().exec("open " + outDir);
            } else if (osName.contains("Windows")) {
                Runtime.getRuntime().exec(MessageFormat.format("cmd /c start \"\" \"{0}\"", outDir));
            } else {
                LOGGER.debug("file output directory:{}", outDir);
            }
        } else {
            LOGGER.warn("read operating system failed!");
        }
    }
}
