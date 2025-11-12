<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
package ${entity.parentPackage}.customizer;
<#list entity.imports![] as imp>
import ${imp}
</#list>
<#-- 导包 -->
import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
* ${table.comments!} 测试自定义模板， 理论上主要是复用Entity 的相关字段
*
* @author ${author}
* @since ${datetime}
*/
<#--类上的注解-->

@Data
@Schema(name="${entity.className}Customizer", description = "${table.comments!}Customizer")
public class ${entity.className}Customizer {

<#-- 遍历字段-->
<#list entity.fields as field>
    /**
    *  ${field.comments}
    */
    @Schema(description = "${field.comments}")
    @NotEmpty
    private ${field.dataTypeName} ${field.fieldName};

</#list>

}
