package com.kjlink.cloud.mybatis.tsid;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Sequence;

/**
 * TSID生成器
 * 雪花ID+CrockfordBase32编码
 *
 * @author Fulai
 * @since 2025-03-24
 */
public class TsIdentifierGenerator implements IdentifierGenerator {
    private final Sequence sequence;
    public static final TsIdentifierGenerator INSTANCE = new TsIdentifierGenerator();

    private TsIdentifierGenerator() {
        //基于mac地址
        long dataCenterId = IdUtil.getDataCenterId(31);
        //基于进程PID
        long workerId = IdUtil.getWorkerId(dataCenterId, 31);
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public TsIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    @Override
    public Number nextId(Object entity) {
        return sequence.nextId();
    }

    @Override
    public String nextUUID(Object entity) {
        return IdEncoder.encode(sequence.nextId());
    }
}
