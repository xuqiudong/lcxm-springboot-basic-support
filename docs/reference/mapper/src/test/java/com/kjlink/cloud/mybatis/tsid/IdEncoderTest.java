package com.kjlink.cloud.mybatis.tsid;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 功能描述
 *
 * @author Fulai
 * @since 2025-03-26
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class IdEncoderTest {

    @Test
    void encode() {
        long idLong = (long) TsIdentifierGenerator.INSTANCE.nextId(null);
        String tsid = TsIdentifierGenerator.INSTANCE.nextUUID(null);
        System.out.println(idLong);
        System.out.println(tsid);

        System.out.println(IdEncoder.explain(idLong));
        System.out.println(IdEncoder.explain(tsid));

        long decode = IdEncoder.decode(tsid);
        System.out.println(decode);
    }
}