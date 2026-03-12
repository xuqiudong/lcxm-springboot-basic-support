package cn.xuqiudong.basic.trial.kjlink.warning.util;

import cn.xuqiudong.basic.trial.kjlink.warning.enums.WarningItemEnum;
import cn.xuqiudong.basic.trial.kjlink.warning.enums.WarningRuleEnum;
import cn.xuqiudong.basic.trial.kjlink.warning.vo.WarningItemVo;
import cn.xuqiudong.basic.trial.kjlink.warning.vo.WarningRuleVo;
import cn.xuqiudong.basic.core.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 预警VO装配工具类：将枚举转换为WarningItemVo列表
 *
 * @author Vic.xu
 * @since 2025-12-05 15:08
 */
public class WarningVoAssembler {

    private static List<WarningItemVo> CACHE = null;

    /**
     * 将枚举转换为基础的WarningItemVo列表（仅含枚举默认值，无数据库覆盖）
     */
    public static List<WarningItemVo> assembleFromEnum() {
        if (CACHE != null) {
            return CACHE;
        }
        // 1. 第一步：获取所有预警事项枚举，转换为基础事项VO
        CACHE = Arrays.stream(WarningItemEnum.values())
                .map(itemEnum -> {
                    WarningItemVo itemVo = new WarningItemVo();
                    // 事项标识：枚举名
                    itemVo.setItemKey(itemEnum.name());
                    // 事项名称：枚举的名称（如“维修费用异常”）
                    itemVo.setItemName(itemEnum.getText());
                    // 预警分类：关联的分类名称（如“超损预警”）
                    itemVo.setCategoryName(itemEnum.getCategory().getName());
                    // 2. 第二步：为当前事项匹配所有关联的规则枚举，转换为规则VO
                    List<WarningRuleVo> ruleVoList = assembleRuleVoByItem(itemEnum);
                    itemVo.setRuleList(ruleVoList);
                    return itemVo;
                })
                .collect(Collectors.toList());

        return CACHE;
    }

    /**
     * 根据事项枚举，匹配所有关联的规则枚举并转换为RuleVo
     */
    private static List<WarningRuleVo> assembleRuleVoByItem(WarningItemEnum itemEnum) {
        // 过滤出当前事项下的所有规则枚举
        List<WarningRuleEnum> ruleEnums = Arrays.stream(WarningRuleEnum.values())
                .filter(ruleEnum -> ruleEnum.getItem() == itemEnum)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(ruleEnums)) {
            return Collections.emptyList();
        }

        // 转换为RuleVo
        return ruleEnums.stream()
                .map(ruleEnum -> {
                    WarningRuleVo ruleVo = new WarningRuleVo();
                    // 规则标识：枚举名
                    ruleVo.setRuleKey(ruleEnum.name());
                    // 预警级别名称（如“轻微/中等/严重”）
                    ruleVo.setWarningLevelName(ruleEnum.getWarningLevel().getText());
                    // 阈值数量（枚举固定值）
                    ruleVo.setThresholdCount(ruleEnum.getThresholdCount());
                    // 默认阈值（枚举的defaultThreshold）
                    ruleVo.setThresholdStr(ruleEnum.getDefaultThreshold());
                    // 规则描述（替换占位符后的完整描述）
                    ruleVo.setRuleDesc(WarningUtil.getWarningRuleText(ruleEnum));
                    // 预警内容
                    ruleVo.setWarningContent(ruleEnum.getWarningContent());
                    // 规则启用状态：默认1（启用），后续数据库覆盖
                    ruleVo.setIsEnabled(1);
                    // 配置人/更新时间：初始为空，后续数据库覆盖
                    ruleVo.setOperator("");
                    ruleVo.setUpdateTime(null);
                    return ruleVo;
                }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<WarningItemVo> warningItemVoList = assembleFromEnum();
        warningItemVoList.forEach(JsonUtil::printJson);
    }

    /*
        CREATE TABLE `warning_rule_config` (
      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
      `rule_key` varchar(64) NOT NULL COMMENT '规则枚举名',
      `item_key` varchar(64) NOT NULL COMMENT '事项枚举名（冗余）',
      `threshold_str` varchar(128) DEFAULT '' COMMENT '自定义阈值（逗号分隔）',
      `is_enabled` tinyint NOT NULL DEFAULT '1' COMMENT '启用状态：1=启用，0=禁用',
      `operator` varchar(32) DEFAULT '' COMMENT '配置人',
      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_rule_key` (`rule_key`),
      KEY `idx_item_key` (`item_key`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则配置表（启用状态+阈值）';
     */
}