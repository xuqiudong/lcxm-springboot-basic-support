package com.kjlink.cloud.mybatis;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.kjlink.cloud.mybatis.entity.User;
import com.kjlink.cloud.mybatis.query.OrderBy;
import com.kjlink.cloud.mybatis.query.PageQuery;
import com.kjlink.cloud.mybatis.query.SqlOperation;
import com.kjlink.cloud.mybatis.query.Where;

import static org.assertj.core.api.Assertions.*;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-07-01
 */
@Transactional
@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper em;

    @BeforeEach
    void clearLog() {
        SqlLog.clear();
        User test = newUser("test");
        em.insert(test);
    }

    @Test
    void selectId() {
        List<String> ids1 = em.selectIdAll();
        assertThat(ids1).isNotNull();
        SqlLog.assertContains("SELECT ID FROM t_sys_user");

        List<String> ids2 = em.selectIdByColumn(User::setFullName, "admin");
        assertThat(ids2).isNotNull();
        SqlLog.assertContains("(full_name = ?)");

        List<String> ids3 = em.selectIdByColumn(User::setFullName, "admin", User::setFullEnName, "1");
        assertThat(ids3).isNotNull();
        SqlLog.assertContains("(full_name = ? AND full_en_name = ?)");

        List<String> ids4 =
                em.selectIdByColumn(User::setFullName, "admin", User::setFullEnName, "1", User::setEmail, "222");
        assertThat(ids4).isNotNull();
        SqlLog.assertContains("(full_name = ? AND full_en_name = ? AND email = ?)");

        Where where = Where.equal(User::setFullName, "hello");
        List<String> ids5 = em.selectId(where);
        assertThat(ids5).isNotNull();
        SqlLog.assertContains("(full_name = ?)");
    }

    @Test
    void selectList() {
        List<User> list1 = em.selectAll();
        Assertions.assertThat(list1).isNotNull();
        System.out.println(list1.size());
        SqlLog.assertContains("FROM t_sys_user");

        List<User> list6 = em.selectByFk(User::setFullName, "admin");
        Assertions.assertThat(list6).isNotNull();
        SqlLog.assertContains("name = ?");

        List<User> list2 = em.selectByColumn(User::setFullEnName, "admin");
        Assertions.assertThat(list2).isNotNull();
        SqlLog.assertContains("full_en_name = ?");

        List<User> list3 = em.selectByColumn(User::setFullName, "admin", User::setFullEnName, "1");
        Assertions.assertThat(list3).isNotNull();
        SqlLog.assertContains("name = ? AND full_en_name = ?");

        List<User> list4 =
                em.selectByColumn(User::setFullName, "admin", User::setFullEnName, "1", User::setEmail, "222");
        Assertions.assertThat(list4).isNotNull();
        SqlLog.assertContains("name = ? AND full_en_name = ? AND email = ?");

        Where where = Where.equal(User::setMobile, "1");
        List<User> ids5 = em.selectList(where);
        Assertions.assertThat(ids5).isNotNull();
        SqlLog.assertContains("(mobile = ?)");
    }

    @Test
    void selectListNull() {
        List<User> list2 = em.selectByColumn(User::setFullName, null);
        Assertions.assertThat(list2).isNotNull();
        SqlLog.assertNotContains("where");

        List<User> list3 = em.selectList(Where.equal(User::setFullName, null));
        SqlLog.assertNotContains("where");
    }

    @Test
    void selectOne() {
        User user1 = em.selectOneByFk(User::setFullName, "test123");
        assertThat(user1).isNull();
        SqlLog.assertContains("full_name = ?");

        User list2 = em.selectOneByColumn(User::setFullName, "test456");
        assertThat(list2).isNull();
        SqlLog.assertContains("full_name = ?");

        User list3 = em.selectOneByColumn(User::setFullName, "test789", User::setFullEnName, "1");
        assertThat(list3).isNull();
        SqlLog.assertContains("full_name = ? AND full_en_name = ?");

        User list4 =
                em.selectOneByColumn(User::setFullName, "test123", User::setFullEnName, "1", User::setEmail, "222");
        assertThat(list4).isNull();
        SqlLog.assertContains("full_name = ? AND full_en_name = ? AND email = ?");
    }

    @Test
    void selectOneWhere() {
        Where where = Where.equal(User::setFullName, "test123");
        User ids5 = em.selectOne(where);
        assertThat(ids5).isNull();
        SqlLog.assertContains("(full_name = ?)");
    }

    @Test
    void selectById() {
        User user = em.selectById("1");
        SqlLog.assertContains("WHERE ID=?");

        List<User> list1 = em.selectByIds(new String[]{"a", "b"});
        Assertions.assertThat(list1).isNotNull();
        SqlLog.assertContains("WHERE ID IN ( ? , ? )");

        List<User> list2 = em.selectByIds(Arrays.asList("a", "b", "c"));
        Assertions.assertThat(list2).isNotNull();
        SqlLog.assertContains("WHERE ID IN ( ? , ? , ? )");
    }

    @Test
    void selectOrderBy() {
        OrderBy orderBy = OrderBy.asc(User::setCreateTime);

        List<User> list1 = em.selectAll(orderBy);
        Assertions.assertThat(list1).isNotNull();
        SqlLog.assertContains("FROM t_sys_user ORDER BY create_time ASC");

        List<User> list2 = em.selectByColumnOrderBy(User::setFullName, "admin", orderBy);
        Assertions.assertThat(list2).isNotNull();
        SqlLog.assertContains("(full_name = ?) ORDER BY create_time ASC");

        List<User> list3 = em.selectByColumnOrderBy(User::setFullName, "admin", User::setFullEnName, "1", orderBy);
        Assertions.assertThat(list3).isNotNull();
        SqlLog.assertContains("(full_name = ? AND full_en_name = ?) ORDER BY create_time ASC");

        List<User> list4 =
                em.selectByColumnOrderBy(User::setFullName, "admin", User::setFullEnName, "1", User::setEmail, "222",
                        orderBy);
        Assertions.assertThat(list4).isNotNull();
        SqlLog.assertContains("(full_name = ? AND full_en_name = ? AND email = ?) ORDER BY create_time ASC");

        Where where = Where.equal(User::setMobile, "1");
        List<User> ids5 = em.selectList(where, orderBy);
        Assertions.assertThat(ids5).isNotNull();
        SqlLog.assertContains("(mobile = ?) ORDER BY create_time ASC");
    }

    @Test
    void selectFirst() {
        OrderBy orderBy = OrderBy.asc(User::setCreateTime);
        Where where = Where.equal(User::setFullName, "test123");
        User user = em.selectFirst(where, orderBy);
        SqlLog.assertContains("(full_name = ?) ORDER BY create_time ASC LIMIT ?");

        User list2 = em.selectFirstByColumn(User::setFullEnName, "test123", orderBy);
        assertThat(list2).isNull();
        SqlLog.assertContains("(full_en_name = ?) ORDER BY create_time ASC LIMIT ?");

        User list3 = em.selectFirstByColumn(User::setFullName, "test123", User::setFullEnName, "1", orderBy);
        assertThat(list3).isNull();
        SqlLog.assertContains("(full_name = ? AND full_en_name = ?) ORDER BY create_time ASC LIMIT ?");

        User list4 =
                em.selectFirstByColumn(User::setFullName, "test123", User::setFullEnName, "1", User::setEmail, "222",
                        orderBy);
        assertThat(list4).isNull();
        SqlLog.assertContains("(full_name = ? AND full_en_name = ? AND email = ?) ORDER BY create_time ASC LIMIT ?");
    }

    @Test
    void selectByColumnIn() {
        List<User> list1 = em.selectByColumnIn(User::setFullName, Arrays.asList("a", "b", "c"));
        Assertions.assertThat(list1).isNotNull();
        SqlLog.assertContains("(full_name IN (?,?,?))");

        List<User> list2 = em.selectByColumnIn(User::setFullName, new String[]{"d", "e", "f"});
        Assertions.assertThat(list2).isNotNull();
        SqlLog.assertContains("(full_name IN (?,?,?))");
    }

    @Test
    void selectPage() {
        Page<User> page = em.selectPage(new PageQuery());
        assertThat(page).isNotNull();
        SqlLog.assertContains("SELECT COUNT(*) AS total FROM t_sys_user");

        MyQuery query = new MyQuery();
        query.setUserName("hello");
        List<User> users = em.selectList(query);
        Assertions.assertThat(users).isNotNull();
        SqlLog.assertContains("user_name LIKE ?)");
    }

    @Test
    void selectCount() {
        int c1 = em.selectCount(User::setFullName, "1");
        assertThat(c1).isNotNegative();
        SqlLog.assertContains("SELECT COUNT( * ) AS total FROM t_sys_user WHERE (full_name = ?)");

        int c3 = em.selectCount(Where.equal(User::setUserName, "1"));
        assertThat(c3).isNotNegative();
        SqlLog.assertContains("SELECT COUNT( * ) AS total FROM t_sys_user WHERE (user_name = ?)");
    }

    @Test
    void exists() {
        boolean c1 = em.exists(User::setFullName, "1");
        SqlLog.assertContains("SELECT COUNT( * ) AS total FROM t_sys_user WHERE (full_name = ?)");

        boolean c3 = em.exists(Where.equal(User::setFullName, "2"));
        SqlLog.assertContains("SELECT COUNT( * ) AS total FROM t_sys_user WHERE (full_name = ?)");
        System.out.println(c1);
        System.out.println(c3);
    }

    @Test
    void latest() {
        em.selectLatestCreate(User::setUserName, "1");
        SqlLog.assertContains("(user_name = ?) ORDER BY CREATE_TIME DESC LIMIT ?");
        em.selectLatestUpdate(User::setUserName, "1");
        SqlLog.assertContains("(user_name = ?) ORDER BY UPDATE_TIME DESC LIMIT ?");
    }

    @Rollback
    @Test
    void insert() {
        User user = newUser("name");
        em.insert(user);
        assertThat(user.getId()).isNotNull();
        SqlLog.assertContains("INSERT INTO t_sys_user");
    }

    @Test
    void insertBatch() {
        User user1 = newUser("test1");
        User user2 = newUser("test2");

        int i = em.insertBatch(Arrays.asList(user1, user2));
        assertThat(i).isEqualTo(2);
        SqlLog.assertContains(") VALUES (");
    }

    @Test
    void updateById() {
        User update = newUser("hello");
        em.updateAll(update);
        SqlLog.assertContains("employee_no=?");

        em.updatePartial(update);
        SqlLog.assertNotContains("employee_no=?");

        em.updateAll(update);
        SqlLog.assertContains("employee_no=?");
    }

    @Test
    void updateRecord() {
        User user = DBUtil.updateWrap(User.class);
        user.setId("123");
        user.setFullName("name");
        user.setEmail(null);
        em.update(user);
        SqlLog.assertContains("UPDATE t_sys_user SET full_name=?,email=? WHERE (id = ?)");

        em.update(user, Where.equ(User::setMobile, "123"));
        SqlLog.assertContains("UPDATE t_sys_user SET full_name=?,email=? WHERE (id = ? AND mobile = ?)");
    }

    @Test
    void updateByIds() {
        em.updateColumnById("1", User::setFullName, "hello");
        em.updateColumnById("1", User::setFullName, "hello", User::setMobile, "123");
        em.updateColumnById("1", User::setFullName, "hello", User::setMobile, "123", User::setEmail, "t@tt" + ".com");

        em.updateColumnByIds(Arrays.asList("1", "2"), User::setFullName, "hello");
        em.updateColumnByIds(Arrays.asList("1", "2"), User::setFullName, "hello", User::setMobile, "123");
        em.updateColumnByIds(Arrays.asList("1", "2"), User::setFullName, "hello", User::setMobile, "123",
                User::setEmail, "t@tt" + ".com");

        em.updateColumnByIds(new String[]{"1", "2"}, User::setFullName, "hello");
        em.updateColumnByIds(new String[]{"1", "2"}, User::setFullName, "hello", User::setMobile, "123");
        em.updateColumnByIds(new String[]{"1", "2"}, User::setFullName, "hello", User::setMobile, "123", User::setEmail,
                "t@tt" + ".com");
    }

    @Test
    void updateByFK() {
        em.updateColumnByFk(User::setMobile, "123", User::setFullName, "test");
        em.updateColumnByFk(User::setMobile, "123", User::setEmail, "t@tt" + ".com", User::setFullName, "test");
        em.updateColumnByFk(User::setMobile, "123", User::setEmail, "t@tt" + ".com", User::setLegalOrgId, "0",
                User::setFullName, "test");
    }

    @Test
    void updateColumn() {
        em.updateColumn(User::setFullName, "test", Where.equal(User::setMobile, "1"));
        em.updateColumn(User::setFullName, "test", User::setMobile, "123", Where.equal(User::setMobile, "1"));
        em.updateColumn(User::setFullName, "test", User::setMobile, "123", User::setEmail, "t@tt" + ".com",
                Where.equal(User::setMobile, "1"));
    }

    @Test
    void save() {
        User u = newUser("who");
        em.save(u);
        assertThat(u.getId()).isNotNull();
        em.save(u);
    }

    @Test
    void saveBatch() {
        User u = newUser("user1");
        User u2 = newUser("user2");

        int ret = em.saveBatch(Arrays.asList(u, u2));
        assertThat(ret).isEqualTo(2);
    }

    @Test
    void saveDetail() {
        User u0 = newUser("user1");
        em.save(u0);
        User u = newUser("user2");
        User u2 = newUser("user3");
        List<User> users = Arrays.asList(u, u2);

        int ret = em.saveDetails(users, User::setLegalOrgId, "2");
        assertThat(ret).isGreaterThanOrEqualTo(2);
    }

    private User newUser(String username) {
        User u0 = new User();
        u0.setUserName(username);
        u0.setFullName("中文" + username);
        u0.setFullName("en" + username);
        u0.setMobile("13012347890");
        u0.setEmail(username + "@qq.com");
        u0.setIsOrganManager(false);
        u0.setIsLocked(false);
        u0.setLegalOrgId("1");
        return u0;
    }

    @Test
    void deleteById() {
        int ret = em.deleteById("0");
        assertThat(ret).isGreaterThanOrEqualTo(0);

        User u = new User();
        u.setId("1");
        int ret2 = em.deleteById(u);
        assertThat(ret2).isGreaterThanOrEqualTo(0);

        int ret3 = em.deleteByIds(new String[]{"1", "2"});
        assertThat(ret3).isGreaterThanOrEqualTo(0);

        int ret4 = em.deleteByIds(CollUtil.newHashSet("1", "2"));
        assertThat(ret4).isGreaterThanOrEqualTo(0);
    }

    @Test
    void deleteByColumn() {
        em.deleteByColumn(User::setFullName, "abc");
        em.deleteByColumn(User::setFullName, "abc", User::setEmail, "t@t.tt");
        em.deleteByColumn(User::setFullName, "abc", User::setEmail, "t@t.tt", User::setMobile, "110");

        em.deleteByColumnIn(User::setFullName, new String[]{"A", "B"});
        em.deleteByColumnIn(User::setFullName, Arrays.asList("A", "B"));
    }

    @Test
    void deleteWhere() {
        Where where = Where.create();
        em.delete(where.like(User::setFullName, "ABC"));
    }

    @Test
    void distinct() {
        List<String> d1 = em.selectDistinct(User::setMobile);
        List<String> d2 = em.selectDistinct(User::setMobile, Where.equal(User::setFullName, "name"));

//        Optional<Long> max1 = em.selectMax(User::setLegalOrgId);
//        Optional<Long> max2 = em.selectMax(User::setLegalOrgId, Where.equal(User::setFullName, "name"));
//
//        Optional<Long> min1 = em.selectMin(User::setLegalOrgId);
//        Optional<Long> min2 = em.selectMin(User::setLegalOrgId, Where.equal(User::setFullName, "name"));
    }

    @Test
    void sum() {
//        Optional<BigDecimal> s1 = em.selectSum(User::setLegalOrgId);
//        Optional<BigDecimal> s2 = em.selectSum(User::setLegalOrgId, Where.all());
//        Optional<BigDecimal[]> s3 = em.selectSum(User::setLegalOrgId, User::setOrgId, Where.all());
////        Optional<BigDecimal[]> s4 =
////                em.selectSum(User::setIndexNumber, User::setRetryTimes, User::setI18n, Where.all());
//        Map<String, BigDecimal> s5 = em.selectSumGroupBy(User::setLegalOrgId, Where.all(), User::setGender);
//        System.out.println(JSONUtil.toJsonPrettyStr(s5));
    }

    @Test
    void selectColumn() {
        List<String> list = em.selectColumn(User::setLegalOrgId, Where.create());
        Assertions.assertThat(list).isNotNull();
    }

    @Test
    void batch() {
        List<User> users = Arrays.asList(newUser("test1"), newUser("test2"));
        em.insertBatch(users);
        em.updateBatch(users);
        int ret = em.deleteBatch(users);
        assertThat(ret).isEqualTo(2);
    }

    @Test
    void selectWithBlob() {
        List<String> ids = em.selectIdAll();
        em.selectById(ids.get(0));
        SqlLog.assertNotContains("memo");
        em.selectByIdWithLob(ids.get(0));
        SqlLog.assertContains("memo");
    }

    @Test
    void ilike() {
        Where where = Where.create();
        where.andOrs(new String[]{"user_name", "full_name"}, SqlOperation.ILIKE, "1");
        em.selectList(where);
    }

    @Test
    void merge() {
        List<User> all = em.selectList(Where.create());
        int ret = em.mergeList(all, all, new MergeHandler<>() {

            @Override
            public boolean match(@NonNull User entity, @NonNull User record) {
                return StrUtil.equals(entity.getId(), record.getId());
            }

            @Override
            public boolean update(@NonNull User entity, @NonNull User record) {
                return false;
            }

            @Override
            public User create(@NonNull User record) {
                return record;
            }
        });
        assertThat(ret).isEqualTo(0);
    }
}
