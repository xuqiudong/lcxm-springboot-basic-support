package cn.xuqiudong.generator.demo.service;

import cn.xuqiudong.generator.demo.entity.Generate;
import cn.xuqiudong.generator.demo.mapper.GenerateMapper;
import org.springframework.stereotype.Service;
import cn.xuqiudong.common.base.model.PageInfo;
import cn.xuqiudong.common.convert.PageConvert;
import cn.xuqiudong.common.query.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
* 测试生成 Service
*
* @author Vic.xu
* @since 2025-11-03 15:16
*/
@Service
public class GenerateService {

    @Autowired
    private GenerateMapper mapper;



    /**
    * 根据id查询全部字段
    */
    public Generate selectById(String id) {
        return mapper.selectByIdWithLob(id);
    }

    /**
    * 分页查询
    */
    public PageInfo<Generate> page(PageQuery query) {
        Assert.notNull(query, "query can not be null");
        Page<Generate> page = mapper.selectPage(query);
        return PageConvert.convert(page);
    }

    /**
    * 删除
    */
    public int delete(String id) {
        return mapper.deleteById(id);
    }

    /**
    * 批量删除
    */
    public int delete(String[] ids) {
        return mapper.deleteByIds(ids);
    }


    /**
    * 判断字段可用， 非重复
    */
    public boolean isValueAvailable(String id, Object value, String column) {
        return mapper.isValueAvailable(id, value, column);
    }

    /**
    * 修改 enable 状态
    */
    public int updateEnable(String id, Boolean enable) {
        return mapper.updateEnable(id, enable);
    }

    /**
    * 批量保存
    */
    public int saveBatch(List<Generate> entityList) {
        return mapper.saveBatch(entityList);
    }

    /**
    * 保存
    */
    public int save(Generate entity) {
        Assert.notNull(entity, "entity can not be null");
        return mapper.save(entity);
    }

}
