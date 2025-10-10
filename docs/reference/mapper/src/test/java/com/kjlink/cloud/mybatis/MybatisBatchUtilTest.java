package com.kjlink.cloud.mybatis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;

import com.kjlink.cloud.mybatis.entity.User;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2023-08-02
 */
@SpringBootTest(classes = MyBatisTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MybatisBatchUtilTest {

    @NonNull
    private static Map<String, Object> getStringObjectMap(String id) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(User::getUpdateTime, LocalDateTime.now());
        wrapper.eq(User::getId, id);
        Map<String, Object> param1 = new HashMap<>();
        param1.put(Constants.WRAPPER, wrapper);
        return param1;
    }

    @Test
    void batchExecute() {
        List<Map<String, Object>> paramList = new ArrayList<>();
        paramList.add(getStringObjectMap("1"));
        paramList.add(getStringObjectMap("2"));
        paramList.add(getStringObjectMap("3"));

//        MybatisBatchUtil.batchExecute(User.class, UserMapper.class,
//                "update", paramList);
    }
}