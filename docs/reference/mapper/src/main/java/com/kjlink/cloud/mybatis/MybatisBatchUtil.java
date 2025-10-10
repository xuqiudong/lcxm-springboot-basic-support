package com.kjlink.cloud.mybatis;

import java.util.Collection;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 批量新增、修改工具类
 * 以BATCH模式调用Mapper接口上的方法
 *
 * @author Fulai
 * @since 2023-08-02
 */
public class MybatisBatchUtil {
    private static final int DEFAULT_BATCH_SIZE = 100;
    private static final Logger LOG = LoggerFactory.getLogger(MybatisBatchUtil.class);

    /**
     * 批量调用Mapper接口上的方法。
     * 注意这个方法内部会先提交当前SqlSession事务，然后单独开启新事务
     *
     * @param mapperClass Mapper类
     * @param methodName  在Mapper中定义的方法
     * @param params      要调用方法的参数
     * @param <T>         实体类
     * @param <M>         Mapper类
     * @param <O>         参数对象，应当为Vo或Map。如果是数组，xml里面用array[0],array[1]取参数
     */
    public static <T, M, O> void batchExecute(Class<M> mapperClass, String methodName, Collection<O> params) {
        if (CollectionUtil.isEmpty(params)) {
            return;
        }
        String statement = mapperClass.getName() + "." + methodName;
        Log log = LogFactory.getLog(mapperClass);
        SqlSessionFactory sqlSessionFactory = SpringUtil.getBean(SqlSessionFactory.class);
        SqlHelper.executeBatch(sqlSessionFactory, log, sqlSession -> {
            int size = params.size();
            int idxLimit = Math.min(DEFAULT_BATCH_SIZE, size);
            int i = 1;
            for (O param : params) {
                int update = sqlSession.update(statement, param);
                if (update < 1) {
                    LOG.warn("update result {}", update);
                }

                if (i == idxLimit) {
                    List<BatchResult> results = sqlSession.flushStatements();
                    LOG.debug("flush statements: {}", results.size());
                    idxLimit = Math.min(idxLimit + DEFAULT_BATCH_SIZE, size);
                }
                i++;
            }
        });
    }
}
