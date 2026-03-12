package cn.xuqiudong.basic.core.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * 描述:
 * 扩展 mybatis-plus 的 方法注入
 * link https://baomidou.com/guides/sql-injector/
 *
 * @author Vic.xu
 * @since 2025-10-27 13:52
 */
public class LcxmDefaultSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        // 查询全部字段
        methodList.add(new SelectByIdWithLob());
        return methodList;
    }
}
