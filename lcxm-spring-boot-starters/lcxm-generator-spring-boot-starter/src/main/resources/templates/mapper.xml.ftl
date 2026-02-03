<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapper.fullName}">

    <sql id="tableName">${table.tableName}</sql>

    <!-- 列表数据列 -->
    <sql id="base_list">
        <#--主键-->
        a.${table.pk.columnNameSafe}<#if table.listFields?has_content>,</#if>
        <#list table.listFields as field>
        a.${field.columnNameSafe}<#if field_has_next>,</#if>
        </#list>
    </sql>

    <!-- 详情数据列 -->
    <sql id="detail_list">
        <include refid="base_list"/><#if table.lobFields?has_content>,</#if>
        <#list table.lobFields as field>
        a.${field.columnNameSafe}<#if field_has_next>,</#if>
        </#list>
    </sql>

    <!-- 只包含主键 -->
    <resultMap id="BaseResultMap" type="${entity.fullName}">
        <id column="${table.pk.columnName}" property="${table.pk.fieldName}"/>
    </resultMap>

    <!-- 通用查询映射结果: 去掉lob 字段 -->
    <resultMap id="ListResultMap" type="${entity.fullName}" extends="BaseResultMap">
        <#list table.listFields as field>
        <result column="${field.columnName}" property="${field.fieldName}"/>
        </#list>
    </resultMap>

    <!-- 全字段映射结果 -->
    <resultMap id="DetailResultMap" type="${entity.fullName}" extends="ListResultMap">
        <#list table.lobFields as field>
        <result column="${field.columnName}" property="${field.fieldName}"/>
        </#list>
    </resultMap>
</mapper>
