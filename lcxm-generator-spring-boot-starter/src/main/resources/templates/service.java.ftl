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
public interface ${service.className} extends ${service.superClass} {
<#else>
public class ${service.className} {
</#if>

}
