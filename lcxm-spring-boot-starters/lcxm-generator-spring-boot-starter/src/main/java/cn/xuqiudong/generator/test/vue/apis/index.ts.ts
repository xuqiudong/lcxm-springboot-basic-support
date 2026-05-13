import type * as TestGenerate from "./type"
import { request } from "@/http/axios"

/** 增改 */
export function save(data: TestGenerate.TestGenerateData) {
    return request({
        url: "test/testGenerate/save",
        method: "post",
        data
    })
}

/** 删 */
export function del(id: string) {
    return request({
        url: `test/testGenerate/delete/${id}`,
        method: "post"
    })
}

/** 查 */
export function queryPage(data: TestGenerate.TestGenerateQueryData) {
    return request<TestGenerate.TestGenerateResponseData>({
        url: "test/testGenerate/page",
        method: "post",
        data
    })
}


/**
* 修改状态
*/
export function updateEnable(id: string, enable: boolean) {
    return request({
        url: `test/testGenerate/updateEnable/${id}`,
        method: "post",
        params: {
            enable
        }
    })
}

