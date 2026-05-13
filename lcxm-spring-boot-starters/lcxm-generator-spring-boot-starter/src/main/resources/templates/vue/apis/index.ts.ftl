<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
<#-- 定义ts 接口API -->
import type * as ${entity.className} from "./type"
import { request } from "@/http/axios"

/** 增改 */
export function save(data: ${entity.className}.${entity.className}Data) {
    return request({
        url: "${module}/${entity.className4Field}/save",
        method: "post",
        data
    })
}

/** 删 */
export function del(id: string) {
    return request({
        url: `${module}/${entity.className4Field}/delete/${r'${id}'}`,
        method: "post"
    })
}

/** 查 */
export function queryPage(data: ${entity.className}.${entity.className}QueryData) {
    return request<${entity.className}.${entity.className}ResponseData>({
        url: "${module}/${entity.className4Field}/page",
        method: "post",
        data
    })
}


/**
* 修改状态
*/
export function updateEnable(id: string, enable: boolean) {
    return request({
        url: `${module}/${entity.className4Field}/updateEnable/${r'${id}'}`,
        method: "post",
        params: {
            enable
        }
    })
}

