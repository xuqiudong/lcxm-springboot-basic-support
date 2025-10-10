<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
package ${entity.packageName};

<#-- 导包 -->
<#list entity.imports![] as imp>
import ${imp}
</#list>

/**
* ${table.comments!} 实体类
*
* @author ${author}
* @since ${datetime}
*/
<#--类上的注解-->
<#list entity.annotations as ann>
${ann}
</#list>
<#--是否有父类 -->
<#if entity.hasSuperClass>
public class ${entity.className} extends ${entity.superClass} {
<#else>
public class ${entity.className} {
</#if>

<#-- 遍历字段-->
<#list entity.fields as field>
    /**
    *  ${field.comments}
    */
    <#list field.annotations as ann>
    ${ann}
    </#list>
    private ${field.dataTypeName} ${field.fieldName};

</#list>

<#-- 如果没有启动lombok, 则需要手动添加getter和setter方法 -->
<#if !lombok>
<#list entity.fields as field>
    /**
    *  get: ${field.comments}
    */
    public ${field.dataTypeName} get${field.methodFieldName}() {
        return ${field.fieldName};
    }

    /**
    *  set: ${field.comments}
    */
    public void set${field.methodFieldName}(${field.dataTypeName} ${field.fieldName}) {
        this.${field.fieldName} = ${field.fieldName};
    }

</#list>
</#if>
}
