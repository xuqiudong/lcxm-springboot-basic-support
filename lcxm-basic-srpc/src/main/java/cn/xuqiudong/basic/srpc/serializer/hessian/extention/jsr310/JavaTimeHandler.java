package cn.xuqiudong.basic.srpc.serializer.hessian.extention.jsr310;


import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * 描述:
 * 包装 JDK8 时间类型的解析/格式化逻辑
 *
 * @author Vic.xu
 * @since 2025-08-25 13:44
 */
public class JavaTimeHandler<T> {
    /**
     * 时间类型
     */
    final Class<T> type;
    /**
     * 时间格式
     */
    final DateTimeFormatter formatter;
    /**
     * 时间解析: String -> 时间类型
     */
    final Function<String, T> parser;
    /**
     * 时间格式化 为  String
     */
    final Function<T, String> formatterFunc;

    public JavaTimeHandler(Class<T> type,
                           DateTimeFormatter formatter,
                           Function<String, T> parser,
                           Function<T, String> formatterFunc) {
        this.type = type;
        this.formatter = formatter;
        this.parser = parser;
        this.formatterFunc = formatterFunc;
    }

    public String format(Object obj) {
        return formatterFunc.apply(type.cast(obj));
    }

    public T parse(String str) {
        return parser.apply(str);
    }
}
