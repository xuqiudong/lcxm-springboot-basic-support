package cn.xuqiudong.basic.mybatisplus.convert;

import cn.xuqiudong.basic.core.model.PageInfo;

import java.util.List;


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
    public static <T> PageInfo<T> convert(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        PageInfo<T> pageInfo = new PageInfo<>();
        if (page != null) {
            pageInfo.setTotal(page.getTotal());
            pageInfo.setSize((int) page.getSize());
            pageInfo.setPage((int) page.getCurrent());
            pageInfo.setPages((int) page.getPages());
            pageInfo.setCurSize(page.getRecords().size());
            pageInfo.setDatas(page.getRecords());
        }
        return pageInfo;
    }

    /**
     * pagehelper Page转为 PageInfo
     */
    public static <T> PageInfo<T> convert(com.github.pagehelper.Page<T> page) {
        PageInfo<T> pageInfo = new PageInfo<>();
        if (page != null) {
            pageInfo.setTotal(page.getTotal());
            pageInfo.setSize(page.getPageSize());
            pageInfo.setPage(page.getPageNum());
            pageInfo.setPages(page.getPages());
            pageInfo.setCurSize(page.size());
            pageInfo.setDatas(page.getResult());
        }
        return pageInfo;
    }

    /**
     * 列表转为 PageInfo 必须是pagehelper的Page
     */
    public static <T> PageInfo<T> convert(List<T> datas) {
        if (datas instanceof com.github.pagehelper.Page<T> page) {
            return convert(page);
        }

        return new PageInfo<>();
    }
}
