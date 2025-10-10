package com.kjlink.cloud.mybatis.injector;

import java.util.List;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.Delete;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;
import com.baomidou.mybatisplus.core.injector.methods.DeleteByIds;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.baomidou.mybatisplus.core.injector.methods.SelectByIds;
import com.baomidou.mybatisplus.core.injector.methods.SelectCount;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.baomidou.mybatisplus.core.injector.methods.SelectMaps;
import com.baomidou.mybatisplus.core.injector.methods.SelectObjs;
import com.baomidou.mybatisplus.core.injector.methods.Update;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.session.Configuration;

import static java.util.stream.Collectors.toList;

/**
 * 参考 DefaultSqlInjector
 * 增加了UpdateSelectiveById, UpdateWholeById
 *
 * @author kj
 * @since 2023-01-31
 */
public class DefaultSqlInjectorPlus extends AbstractSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        GlobalConfig.DbConfig dbConfig = GlobalConfigUtils.getDbConfig(configuration);
        Stream.Builder<AbstractMethod> builder = Stream.<AbstractMethod>builder()
                .add(new Insert(dbConfig.isInsertIgnoreAutoIncrementColumn()))
                .add(new Delete())
                .add(new Update())
                .add(new SelectCount())
                .add(new SelectMaps())
                .add(new SelectObjs())
                .add(new SelectList());
        if (tableInfo.havePK()) {
            builder.add(new DeleteById())
                    .add(new DeleteByIds())
                    .add(new UpdateById())
                    .add(new SelectById())
                    .add(new SelectByIdWithLob())
                    .add(new SelectByIds())
                    .add(new UpdatePartial())
                    .add(new UpdateAll());
        } else {
            logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.",
                    tableInfo.getEntityType()));
        }
        return builder.build().collect(toList());
    }
}
