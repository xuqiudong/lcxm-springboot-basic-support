package cn.xuqiudong.basic.trial.kjlink.warning.util;

import cn.xuqiudong.basic.trial.kjlink.warning.enums.WarningRuleEnum;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * 描述:
 *      预警规则相关工具类
 * @author Vic.xu
 * @since 2025-12-05 11:01
 */
public class WarningUtil {

    /**
     * 获取预警规则说明
     */
    public static String getWarningRuleText(WarningRuleEnum rule) {
        if (rule == null) {
            return  "";
        }
        return getWarningRuleText(rule.getText(), rule.getDefaultThreshold());
    }

    /**
     * 获取预警规则说明
     */

    public static String getWarningRuleText(String text, String  threshold) {
        if (StringUtils.isBlank(threshold) || StringUtils.isBlank(text)) {
            return text;
        }
        return MessageFormat.format(text, threshold.split( ","));
    }

}
