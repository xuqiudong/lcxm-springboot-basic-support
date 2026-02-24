package cn.xuqiudong.basic.trial.kjlink.warning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 描述:
 *   预警规则 vo
 * @author Vic.xu
 * @since 2025-12-05 13:13
 */
@Schema(description = "预警规则VO")
@Data
public class WarningRuleVo {

    @Schema(description = "规则标识（枚举名，如MAINTENANCE_ABNORMAL_MINOR）")
    private String ruleKey;

    @Schema(description = "预警级别名称（如轻微/中等/严重）")
    private String warningLevelName;

    @Schema(description = "阈值数量")
    private Integer thresholdCount;

    @Schema(description = "当前生效阈值（逗号分隔，如20.00,30.00）")
    private String thresholdStr;

    @Schema(description = "规则描述（替换占位符后的完整描述）")
    private String ruleDesc;

    @Schema(description = "预警内容")
    private String warningContent;

    @Schema(description = "规则启用状态（1=启用，0=禁用）")
    private Integer isEnabled = 1;

    @Schema(description = "配置人")
    private String operator;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;
}
