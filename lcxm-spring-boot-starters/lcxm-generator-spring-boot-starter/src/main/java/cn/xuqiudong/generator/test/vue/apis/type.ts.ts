/**
*  测试生成 查询数据格式
*/
export interface TestGenerateQueryData extends ApiBasePageQuery {
    // 名称
    name?: string;
    // 生日
    birthday?: string;
    // 备注
    note?: string;
    // 类型
    type?: string;
    // 版本号
    version?: number;
    // 文章
    article?: string;
    // 打开时间
    openTime?: string;
    // 开始时间
    startTime?: string;
    // tag名称
    tagName?: string;
}
/**
*  测试生成 列表 数据格式
*/
export interface TestGenerateData extends ApiBaseEnity{
    // 名称
    name: string;
    // 生日
    birthday: string;
    // 备注
    note: string;
    // 类型
    type: string;
    // 版本号
    version: number;
    // 文章
    article: string;
    // 打开时间
    openTime: string;
    // 开始时间
    startTime: string;
    // tag名称
    tagName: string;
}

/**
*  测试生成 响应分页数据格式
*/
export type TestGenerateResponseData = ApiResponseData<ApiPageInfo<TestGenerateData>>