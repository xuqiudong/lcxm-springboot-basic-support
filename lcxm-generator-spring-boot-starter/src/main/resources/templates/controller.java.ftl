<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
<#-- see MapperContext-->
package ${controller.packageName};

<#-- 导包 -->
<#list controller.imports![] as imp>
import ${imp}
</#list>
import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.common.base.model.PageInfo;
import cn.xuqiudong.common.base.request.CheckNotRepeatRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
* ${table.comments!} Controller
*
* @author ${author}
* @since ${datetime}
*/
@Tag(name = "${entity.className}", description = "${table.comments!}")
<#--类上的注解-->
<#list controller.annotations as ann>
${ann}
</#list>
<#--是否有父类 -->
<#if controller.hasSuperClass>
public interface ${controller.className} extends ${controller.superClass} {
<#else>
public class ${controller.className} {
</#if>
    @Autowired
    private ${service.className} service;

    @Operation(summary = "分页查询")
    @PostMapping(value = "/page")
    public BaseResponse<PageInfo<${entity.className }>> page(@RequestBody ${query.className} query) {
        PageInfo<${entity.className}> page = service.page(query);
        return BaseResponse.success(page);
    }


    @Operation(summary = "详情", description = "根据id查询详情")
    @GetMapping(value = "/detail/{id}")
    public BaseResponse<${entity.className}> detail(@PathVariable ${entity.pkTypeName} id) {
        ${entity.className} entity = service.selectById(id);
        return entity == null ? BaseResponse.error("不存在的实体") : BaseResponse.success(entity);
    }

    @Operation(summary = "保存", description = "id空则新增， id非空则修改")
    @PostMapping(value = "/save")
    public BaseResponse<${entity.className}> save(@RequestBody ${entity.className} entity) {
        int num = service.save(entity);
        if (num <= 0) {
            return BaseResponse.error("保存失败");
        }
        return BaseResponse.success(service.selectById(entity.getId()));
    }

    @Operation(summary = "修改状态", description = "修改状态")
    @PostMapping(value = "/updateEnable/{id}")
    public BaseResponse<Boolean> updateEnable(@PathVariable ${entity.pkTypeName} id, Boolean enable) {
        int updated = service.updateEnable(id, enable);
        return BaseResponse.success(updated == 1);
    }

    @Operation(summary = "删除", description = "根据id删除")
    @PostMapping(value = "/delete/{id}")
    public BaseResponse<Integer> delete(@PathVariable ${entity.pkTypeName} id) {
        return BaseResponse.success(service.delete(id));
    }


    @Operation(summary = "检测字段是否可用", description = "检测字段是否可用")
    @PostMapping(value = "/check")
    public BaseResponse<?> checkAvailable(@RequestBody CheckNotRepeatRequest<${entity.pkTypeName}> repeatRequest) {
        boolean ok = service.isValueAvailable(repeatRequest.getId(), repeatRequest.getValue(), repeatRequest.getColumn());
        return BaseResponse.success(ok);
    }
}