package com.kjlink.cloud.mybatis.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * 简化泛型序列化
 *
 * @author Fulai
 * @since 2024-09-13
 */
public class SnapshotRecord extends LinkedHashMap<String, Object> {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static String serialize(List<SnapshotRecord> recordList) throws JsonProcessingException {
        if (CollUtil.isEmpty(recordList)) {
            return null;
        }
        if (recordList.size() == 1) {
            return objectMapper.writeValueAsString(recordList.get(0));
        }
        //转成数组，不需要类型信息
        SnapshotRecord[] array = recordList.toArray(new SnapshotRecord[0]);
        return objectMapper.writeValueAsString(array);
    }

    public static List<SnapshotRecord> deserialize(String json) throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);
        if (rootNode.isArray()) {
            List<SnapshotRecord> resultList = new ArrayList<>();
            ArrayNode arrayNode = (ArrayNode) rootNode;

            for (JsonNode node : arrayNode) {
                SnapshotRecord record = objectMapper.treeToValue(node, SnapshotRecord.class);
                resultList.add(record);
            }
            return resultList;
        } else {
            SnapshotRecord record = objectMapper.treeToValue(rootNode, SnapshotRecord.class);
            return Collections.singletonList(record);
        }
    }
}
