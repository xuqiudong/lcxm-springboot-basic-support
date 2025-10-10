package com.kjlink.cloud.mybatis.query;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2024-01-22
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ColumnUtilTest {

    @Test
    void safeColumn() {
        String safe = ColumnUtil.safeColumn("a");
        assertThat(safe).isEqualTo("a");

        String safe2 = ColumnUtil.safeColumn("aBc");
        assertThat(safe2).isEqualTo("a_bc");

        String safe3 = ColumnUtil.safeColumn("a.b.c");
        assertThat(safe3).isEqualTo("a.b.c");

        String safe4 = ColumnUtil.safeColumn("a.bC.d");
        assertThat(safe4).isEqualTo("a.b_C.d");

        Assertions.assertThatCode(() -> {
            ColumnUtil.safeColumn(" a");
        }).isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatCode(() -> {
            ColumnUtil.safeColumn("--drop");
        }).isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatCode(() -> {
            ColumnUtil.safeColumn("drop table");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}