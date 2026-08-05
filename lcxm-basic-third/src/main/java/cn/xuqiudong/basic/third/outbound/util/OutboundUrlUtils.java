package cn.xuqiudong.basic.third.outbound.util;

import cn.hutool.core.util.StrUtil;
import cn.xuqiudong.basic.third.common.exception.ThirdException;

/**
 * 出站 URL 工具。
 *
 * @author Vic.xu
 */
public final class OutboundUrlUtils {

    private OutboundUrlUtils() {
    }

    /**
     * 拼接第三方 host 和接口 path。
     */
    public static String joinHostPath(String host, String path, String thirdCode) {
        if (StrUtil.isBlank(host)) {
            throw new ThirdException("outbound partner host can not be blank: " + thirdCode);
        }
        if (StrUtil.isBlank(path)) {
            return host;
        }
        boolean hostEndsWithSlash = host.endsWith("/");
        boolean pathStartsWithSlash = path.startsWith("/");
        if (hostEndsWithSlash && pathStartsWithSlash) {
            return host + path.substring(1);
        }
        if (!hostEndsWithSlash && !pathStartsWithSlash) {
            return host + "/" + path;
        }
        return host + path;
    }
}
