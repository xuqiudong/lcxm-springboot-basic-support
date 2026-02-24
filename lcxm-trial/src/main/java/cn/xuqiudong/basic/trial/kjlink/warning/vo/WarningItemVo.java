package cn.xuqiudong.basic.trial.kjlink.warning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 描述:
 *  预警事项 vo
 * @author Vic.xu
 * @since 2025-12-05 11:05
 */
@Schema(description = "预警事项VO")
@Data
public class WarningItemVo {

    @Schema(description = "事项标识（枚举名，如MAINTENANCE_ABNORMAL）")
    private String itemKey;

    @Schema(description = "事项名称（如维修费用异常）")
    private String itemName;

    @Schema(description = "预警分类（如超损预警）")
    private String categoryName;

    @Schema(description = "该事项下的所有预警规则")
    private List<WarningRuleVo> ruleList;

}
