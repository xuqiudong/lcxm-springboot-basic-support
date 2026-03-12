package cn.xuqiudong.basic.core.srpc.serializer.hessian.test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2024-12-10 16:35
 */
public class TestHessianMethodImpl implements TestHessianMethod{


    @Override
    public int add(int a, int b) {
        return a+b;
    }

    @Override
    public LocalDate now(LocalDateTime localDateTime) {
        return LocalDate.now();
    }

}
