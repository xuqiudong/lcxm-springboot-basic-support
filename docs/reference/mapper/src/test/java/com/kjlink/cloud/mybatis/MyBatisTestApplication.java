package com.kjlink.cloud.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 分析数据库，确定哪些表和视图需要生成，每个表的父类是什么
 * 需要手动执行
 *
 * @author Fulai
 * @since 2023-07-11
 */
@MapperScan(annotationClass = Mapper.class)
@SpringBootApplication
public class MyBatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBatisTestApplication.class);
    }
}
