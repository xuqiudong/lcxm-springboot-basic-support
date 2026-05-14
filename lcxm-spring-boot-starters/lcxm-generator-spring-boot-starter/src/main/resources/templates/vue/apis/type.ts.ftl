<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
<#-- 定义ts 数据格式 -->
/**
*  ${table.comments!} 查询数据格式
*/
export interface ${entity.className}QueryData extends ApiBasePageQuery {
<#-- 遍历字段-->
<#list entity.fields as field>
    ${field.fieldName}?: ${field.tsType} | null; // ${field.comments}
</#list>
}
/**
*  ${table.comments!} 列表 数据格式
*/
export interface ${entity.className}Data extends ApiBaseEnity{
<#-- 遍历字段-->
<#list entity.fields as field>
    ${field.fieldName}: ${field.tsType};// ${field.comments}
</#list>
}

/**
*  ${table.comments!} 响应分页数据格式
*/
export type ${entity.className}PageResponseData = ApiResponseData<ApiPageInfo<${entity.className}Data>>

