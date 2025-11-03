<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
<#-- see MapperContext-->
package ${service.packageName};

<#-- 导包 -->
<#list service.imports![] as imp>
import ${imp}
</#list>
import cn.xuqiudong.common.base.model.PageInfo;
import cn.xuqiudong.common.convert.PageConvert;
import cn.xuqiudong.common.query.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
* ${table.comments!} Service
*
* @author ${author}
* @since ${datetime}
*/
<#--类上的注解-->
<#list service.annotations as ann>
${ann}
</#list>
<#--是否有父类 -->
<#if service.hasSuperClass>
public class ${service.className} extends ${service.superClass} {
<#else>
public class ${service.className} {
</#if>

    @Autowired
    private ${mapper.className} mapper;



    /**
    * 根据id查询全部字段
    */
    public ${entity.className} selectById(${entity.pkTypeName} id) {
        return mapper.selectByIdWithLob(id);
    }

    /**
    * 分页查询
    */
    public PageInfo<${entity.className}> page(PageQuery query) {
        Assert.notNull(query, "query can not be null");
        Page<${entity.className}> page = mapper.selectPage(query);
        return PageConvert.convert(page);
    }

    /**
    * 删除
    */
    public int delete(${entity.pkTypeName} id) {
        return mapper.deleteById(id);
    }

    /**
    * 批量删除
    */
    public int delete(${entity.pkTypeName}[] ids) {
        return mapper.deleteByIds(ids);
    }


    /**
    * 判断字段可用， 非重复
    */
    public boolean isValueAvailable(${entity.pkTypeName} id, Object value, String column) {
        return mapper.isValueAvailable(id, value, column);
    }

    /**
    * 修改 enable 状态
    */
    public int updateEnable(${entity.pkTypeName} id, Boolean enable) {
        return mapper.updateEnable(id, enable);
    }

    /**
    * 批量保存
    */
    public int saveBatch(List<${entity.className}> entityList) {
        return mapper.saveBatch(entityList);
    }

    /**
    * 保存
    */
    public int save(${entity.className} entity) {
        Assert.notNull(entity, "entity can not be null");
        return mapper.save(entity);
    }

}
