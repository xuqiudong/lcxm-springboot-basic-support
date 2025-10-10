package com.kjlink.cloud.mybatis.interceptor;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 当配置了回收站表名称时，激活配置
 */
public class TrashPluginTableNameCondition extends SpringBootCondition {

    private static final String PROPERTY_NAME = "mybatis-plus.trash-plugin.table";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        String value = env.getProperty(PROPERTY_NAME);

        ConditionMessage.Builder message = ConditionMessage.forCondition("TrashPluginTableCondition");

        if (value == null || value.isBlank()) {
            return ConditionOutcome.noMatch(message.because("property is empty"));
        }

        String lower = value.trim().toLowerCase();
        if ("none".equals(lower) || "disable".equals(lower)) {
            return ConditionOutcome.noMatch(message.because("property is '" + value + "'"));
        }

        return ConditionOutcome.match(message.because("property is valid: '" + value + "'"));
    }
}
