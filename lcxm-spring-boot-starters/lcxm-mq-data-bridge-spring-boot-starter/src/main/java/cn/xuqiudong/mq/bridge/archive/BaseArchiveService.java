package cn.xuqiudong.mq.bridge.archive;

import cn.xuqiudong.mq.bridge.autoconfigure.DataBridgeProperties;
import cn.xuqiudong.mq.bridge.enums.ArchivePeriodEnum;
import cn.xuqiudong.mq.bridge.mapper.ArchiveMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述:
 * 归档处理基类
 *
 * @author Vic.xu
 * @since 2026-02-02 14:14
 */
public abstract class BaseArchiveService {

    protected static final Logger logger = LoggerFactory.getLogger(BaseArchiveService.class);

    @Resource
    private ArchiveMapper archiveMapper;

    @Resource
    private DataBridgeProperties dataBridgeProperties;

    // 缓存：后缀 -> 表名  每个归档业务一个 tableCache
    private final ConcurrentMap<String, String> tableCache = new ConcurrentHashMap<>();


    /**
     * 归档入口
     */
    @Transactional(rollbackFor = Exception.class)
    public void archive(Integer id) {
        if (!dataBridgeProperties.isArchiveEnabled()) {
            logger.trace("归档已关闭");
            return;
        }
        LocalDate now = LocalDate.now();
        String suffix = getArchivePeriod().format(now);
        // 获取或创建归档表
        String archiveTable = getOrCreateArchiveTable(suffix);

        // 归档
        int insert = archiveMapper.archive(getTableName(), archiveTable, id);
        if (insert != 1) {
            throw new IllegalStateException("归档失败，id=" + id);
        }
        // 删除业务表
        int delete = archiveMapper.delete(getTableName(), id);
        if (delete != 1) {
            throw new IllegalStateException("删除失败，id=" + id);
        }
        logger.info("归档成功，id= {}", id);
    }

    private ArchivePeriodEnum getArchivePeriod() {
        ArchivePeriodEnum archiveType = dataBridgeProperties.getArchiveType();
        return archiveType == null ? ArchivePeriodEnum.YEAR : archiveType;
    }


    /**
     * 获取或创建归档表
     */
    protected String getOrCreateArchiveTable(String suffix) {
        return tableCache.computeIfAbsent(suffix, s -> {
            String tableName = getTableName() + "_" + s;
            if (archiveMapper.tableExists(tableName) == 0) {
                archiveMapper.createTableLike(tableName, getTableName());
            }
            return tableName;
        });
    }

    /**
     * 子类只需返回业务表名 / 历史模板表名统一
     */
    protected abstract String getTableName();

}
