<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
<#-- see MapperContext-->
package ${mapper.packageName};

<#-- 导包 -->
<#list mapper.imports![] as imp>
import ${imp}
</#list>

/**
* ${table.comments!} Mapper
*
* @author ${author}
* @since ${datetime}
*/
<#--类上的注解-->
<#list mapper.annotations as ann>
${ann}
</#list>
<#--是否有父类 -->
<#if mapper.hasSuperClass>
public interface ${mapper.className} extends ${mapper.superClass} {
<#else>
public interface ${mapper.className} {
</#if>

}
