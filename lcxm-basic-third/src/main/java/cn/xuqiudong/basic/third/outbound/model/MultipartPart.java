package cn.xuqiudong.basic.third.outbound.model;

import java.io.File;
import java.io.InputStream;

import cn.hutool.core.util.StrUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;

/**
 * multipart/form-data 中的一个字段。
 *
 * <p>使用 List 保存该模型，可以支持同一个 field name 上传多个文件。</p>
 *
 * @author Vic.xu
 */
@Getter
public class MultipartPart {

    /**
     * 字段类型。
     */
    private final Type type;

    /**
     * form field name。
     */
    private final String name;

    /**
     * 普通字段值。
     */
    private final String value;

    /**
     * 本地文件。
     */
    private final File file;

    /**
     * 输入流文件名。
     */
    private final String fileName;

    /**
     * 文件输入流，由调用方负责关闭。
     */
    private final InputStream inputStream;

    @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Fail fast for invalid multipart part input.")
    private MultipartPart(Type type, String name, String value, File file, String fileName,
            InputStream inputStream) {
        if (type == null) {
            throw new IllegalArgumentException("multipart part type can not be null");
        }
        if (StrUtil.isBlank(name)) {
            throw new IllegalArgumentException("multipart part name can not be blank");
        }
        this.type = type;
        this.name = name;
        this.value = value;
        this.file = file;
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    /**
     * 普通 multipart 字段。
     */
    public static MultipartPart field(String name, String value) {
        if (value == null) {
            throw new IllegalArgumentException("multipart field value can not be null");
        }
        return new MultipartPart(Type.FIELD, name, value, null, null, null);
    }

    /**
     * File 文件字段。
     */
    public static MultipartPart file(String name, File file) {
        if (file == null) {
            throw new IllegalArgumentException("multipart file can not be null");
        }
        return new MultipartPart(Type.FILE, name, null, file, null, null);
    }

    /**
     * File 文件字段，并显式指定上传文件名。
     */
    public static MultipartPart file(String name, File file, String fileName) {
        if (file == null) {
            throw new IllegalArgumentException("multipart file can not be null");
        }
        return new MultipartPart(Type.FILE, name, null, file, fileName, null);
    }

    /**
     * InputStream 文件字段。
     */
    public static MultipartPart stream(String name, String fileName, InputStream inputStream) {
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("multipart stream fileName can not be blank");
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("multipart inputStream can not be null");
        }
        return new MultipartPart(Type.STREAM, name, null, null, fileName, inputStream);
    }

    /**
     * multipart 字段类型。
     */
    public enum Type {

        /**
         * 普通文本字段。
         */
        FIELD,

        /**
         * 本地 File 文件。
         */
        FILE,

        /**
         * InputStream 文件。
         */
        STREAM
    }
}
