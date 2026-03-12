package cn.xuqiudong.basic.framework.select.service;

import cn.xuqiudong.basic.core.model.SelectOption;

import java.util.List;

/**
 * 描述:
 *   业务下拉框接口, 理应是动态获取的，因为业务数据随时变动
 *   总体交给实现类自己处理
 * @author Vic.xu
 * @since 2026-01-08 10:11
 */
public interface BusinessSelectProvider {

    /**
     * 业务下拉框标识
     */
    String  selectType();

    /**
     * 获取业务下拉框选项
     */
    List<SelectOption> getSelectOptions();

}
