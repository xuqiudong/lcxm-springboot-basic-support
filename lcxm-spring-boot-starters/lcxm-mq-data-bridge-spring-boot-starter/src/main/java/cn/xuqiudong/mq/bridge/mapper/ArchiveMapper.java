package cn.xuqiudong.mq.bridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 描述:
 * 归档处理 mapper
 *
 * @author Vic.xu
 * @since 2026-02-02 14:15
 */
@Mapper
public interface ArchiveMapper {

    /**
     * 判断表是否存在
     */
    int tableExists(@Param("tableName") String tableName);

    /**
     * 创建表： 根据业务表创建
     */
    void createTableLike(@Param("tableName") String tableName,
                         @Param("templateTable") String templateTable);

    /**
     * 归档数据： 根据id 归档 - 业务表插入到归档表
     */
    int archive(@Param("bizTable") String bizTable,
                @Param("archiveTable") String archiveTable,
                @Param("id") Integer id);

    /**
     * 删除数据： 根据id 删除 - 业务表删除
     */
    int delete(@Param("bizTable") String bizTable,
               @Param("id") Integer id);
}
