package cn.xuqiudong.basic.srpc.serializer.hessian.test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2024-12-10 16:35
 */
public interface TestHessianMethod {


    int add(int a, int b);

    LocalDate now(LocalDateTime localDateTime);

}
