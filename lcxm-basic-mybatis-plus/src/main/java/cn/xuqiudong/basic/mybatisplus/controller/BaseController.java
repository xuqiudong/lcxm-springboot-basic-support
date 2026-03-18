package cn.xuqiudong.basic.mybatisplus.controller;

import cn.xuqiudong.basic.core.model.BaseEntity;
import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.mybatisplus.service.BaseService;
import cn.xuqiudong.basic.core.util.HibernateValidatorUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 说明 :  controller 的基类
 * @author  Vic.xu
 * @since  2019年11月13日 下午2:40:41
 */
public abstract class BaseController<S extends BaseService<?, T>, T extends BaseEntity> {

    @Autowired
    protected S service;

    @Resource
    protected HttpServletRequest request;


    /**
     * 检验参数
     * @param model
     */
    protected void validateModel(Object model) {
        HibernateValidatorUtils.validate(model);
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping(value = "list")
    public BaseResponse<?> list(T lookup) {
        PageInfo<T> list = service.page(lookup);
        return BaseResponse.success(list);
    }

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    @PostMapping(value = "/save")
    public BaseResponse<?> save(T entity) {
        service.save(entity);
        return BaseResponse.success(entity);
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/detail")
    public BaseResponse<?> detail(Integer id) {
        T entity = service.findById(id);
        return BaseResponse.success(entity);
    }

    @PostMapping(value = "/delete")
    public BaseResponse<?> delete(int id) {
        service.delete(id);
        return BaseResponse.success();
    }

    @PostMapping(value = "/batchDelete")
    public BaseResponse<?> delete(@RequestParam("ids[]") int[] ids) {
        service.delete(ids);
        return BaseResponse.success();
    }

    /**
     * 检测"name"是否未重复
     *
     * @param id id may  null 如果不传 则判断表里的全部项,如果传了id,则排除当前id所对应的列
     * @param column  table  column  列名称
     * @param value  value 需要判断是否重复的列的值
     * @return
     */
    @PostMapping(value = "/check")
    public BaseResponse<?> check(Integer id, @RequestParam(defaultValue = "name") String column, String value) {
        boolean ok = service.checkNotRepeat(id, value, column);
        return BaseResponse.success(ok);
    }
}
