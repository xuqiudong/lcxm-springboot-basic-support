package cn.xuqiudong.common.convert;

import cn.xuqiudong.common.base.model.PageInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 描述:
 * 其他Page转为 PageInfo
 *
 * @author Vic.xu
 * @since 2025-10-31 17:50
 */
public class PageConvert {

    /**
     * 其他mybatis-plus Page转为 PageInfo
     */
    public static <T> PageInfo<T> convert(Page<T> page) {
        PageInfo<T> pageInfo = new PageInfo<>();
        if (page != null) {
            pageInfo.setTotal((int) page.getTotal());
            pageInfo.setSize((int) page.getSize());
            pageInfo.setPage((int) page.getCurrent());
            pageInfo.setPages((int) page.getPages());
            pageInfo.setCurSize(page.getRecords().size());
            pageInfo.setDatas(page.getRecords());
        }
        return pageInfo;
    }
}
