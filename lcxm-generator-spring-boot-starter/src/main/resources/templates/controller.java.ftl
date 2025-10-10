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

/**
* ${table.comments!} Controller
*
* @author ${author}
* @since ${datetime}
*/
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

}
