package com.kjlink.cloud.mybatis.query;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kjlink.cloud.mybatis.entity.User;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-22
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WhereMapperTest {
    @Autowired
    private WhereMapper whereMapper;

    @Test
    void selectByWhere() {
        Where where = Where.create();
        where.eq(User::setFullName, "eq1");
        where.ne(User::setFullName, "ne1");
        where.like(User::setFullName, "like1");
        where.likeLeft(User::setFullName, "left1");
        where.likeRight(User::setFullName, "right1");
        where.notLike(User::setFullName, "notLike1");
        where.lt(User::setFullName, "lt1");
        where.gt(User::setFullName, "gt1");
        where.le(User::setFullName, "le1");
        where.ge(User::setFullName, "ge1");
        where.between(User::setFullName, "bet1", "bet2");
        where.in(User::setMobile, Arrays.asList("in1", "in2", "in3"));
        where.notIn(User::setMobile, Arrays.asList("in1", "in2", "in3"));
        where.isNull(User::setEmail);
        where.isNotNull(User::setEmail);
        where.andOrs(new String[]{"user_name", "mobile"}, SqlOperation.EQ, "1");
        //order by fullname asc, mobile desc
        OrderBy orderBy = OrderBy.asc(User::setFullName).thenDesc(User::setMobile);
        List<User> users = whereMapper.selectByWhere(where, orderBy);
        System.out.println(users);
    }

    @Test
    void where2() {
        Where where = Where.equ(User::setUserName, "11");
        Where w2 = Where.equ(User::setMobile, "22");
        whereMapper.selectByWhere2(where, w2);
    }
}