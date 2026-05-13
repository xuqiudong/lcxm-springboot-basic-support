<!-- 测试生成 -->
<script lang="ts" setup>
    import type { FormInstance, FormRules } from "element-plus"
    import type { TestGenerateData } from "./apis/type"
    import { validateUniqueField } from "@@/composables/useFormValidate"
    import { useFullscreen } from "@@/composables/useFullscreen"
    import { usePagination } from "@@/composables/usePagination"
    import { useTableSort } from "@@/composables/useTableSort"
    import { checkPermission } from "@@/utils/permission"
    import { CirclePlus, Close, Refresh, RefreshRight, Search } from "@element-plus/icons-vue"
    import { cloneDeep } from "lodash-es"
    import * as TestGenerateApi from "./apis"

    defineOptions({
        // 命名当前组件:  测试生成
        name: "TestGenerate"
    })

    const loading = ref<boolean>(false)
    const { paginationData, handleCurrentChange, handleSizeChange } = usePagination()
    const { orders, handleSortChange } = useTableSort({})
    // 编辑弹窗是否全屏
    const { isFullscreen, fullscreenSvgName, toggleFullscreen } = useFullscreen()
    // #region 增
    const DEFAULT_FORM_DATA: TestGenerateData = {
        // 名称
        name: '';
        // 生日
        birthday: '';
        // 备注
        note: '';
        // 类型
        type: '';
        // 版本号
        version: 0;
        // 文章
        article: '';
        // 打开时间
        openTime: '';
        // 开始时间
        startTime: '';
        // tag名称
        tagName: '';
    }

    const dialogVisible = ref<boolean>(false)
    const formRef = ref<FormInstance | null>(null)
    const formData = ref<TestGenerateData>(cloneDeep(DEFAULT_FORM_DATA))

    const formRules: FormRules<TestGenerateData> = reactive<FormRules>({
        //name: [{ required: true, trigger: "blur", message: "请输入nam" }, { validator: validateUniqueField("`test/testGenerate/check", "name", "名称", formData), trigger: "blur" }],
    })

    function handleCreateOrUpdate() {
        formRef.value?.validate((valid) => {
            if (!valid) {
                ElMessage.error("表单校验不通过")
                return
            }
            loading.value = true
            TestGenerateApi.save(formData.value).then(() => {
                ElMessage.success("操作成功")
                dialogVisible.value = false
                getTableData()
            }).finally(() => {
                loading.value = false
            })
        })
    }
    function resetForm() {
        formRef.value?.clearValidate()
        formData.value = cloneDeep(DEFAULT_FORM_DATA)
    }
    // #endregion

    // #region 删
    function handleDelete(row: TestGenerateData) {
        if (!row.id) return
        ElMessageBox.confirm(`正在删除XX：${row.id}，确认删除？`, "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(() => {
            TestGenerateApi.del(row.id!).then(() => {
                ElMessage.success("删除成功")
                getTableData()
            })
        })
    }
    // #endregion

    // #region 改
    function handleUpdate(row: TestGenerateData) {
        dialogVisible.value = true
        formData.value = cloneDeep(row)
    }
    // #endregion

    // #region 查
    const tableData = ref<TestGenerateData[]>([])
    const searchFormRef = ref<FormInstance | null>(null)
    const searchData = reactive({
        // 名称
        name: '';
        // 生日
        birthday: '';
        // 备注
        note: '';
        // 类型
        type: '';
        // 版本号
        version: 0;
        // 文章
        article: '';
        // 打开时间
        openTime: '';
        // 开始时间
        startTime: '';
        // tag名称
        tagName: '';
    })
    function getTableData() {
        loading.value = true
        TestGenerateApi.queryPage({
            page: paginationData.currentPage,
            size: paginationData.pageSize,
            orders: orders.value,
            ...searchData
        }).then(({ data }) => {
            paginationData.total = data.total
            tableData.value = data.datas
            console.log(tableData.value)
        }).catch(() => {
            tableData.value = []
        }).finally(() => {
            loading.value = false
        })
    }

    function handleSearch() {
        paginationData.currentPage === 1 ? getTableData() : (paginationData.currentPage = 1)
    }
    function resetSearch() {
        searchFormRef.value?.resetFields()
        handleSearch()
    }
    // #endregion

    // #region 更新状态
    function updateenabled(row: TestGenerateData) {
        const id = row.id
        if (!id) return
        const enbale = row.enabled
        const text = enbale ? "禁用" : "启用"
        ElMessageBox.confirm(`确认${text}xx吗`, "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(() => {
            TestGenerateApi.updateEnable(id, !enbale).then(() => {
                ElMessage.success("修改成功")
                getTableData()
            })
        })
    }
    // #endregion

    // 监听分页参数的变化
    watch([
        () => paginationData.currentPage,
        () => paginationData.pageSize,
        () => orders.value,
    ], getTableData, { immediate: true })
</script>

<template>
    <div class="app-container">
        <el-card v-loading="loading" shadow="never" class="search-wrapper">
            <el-form ref="searchFormRef" :inline="true" :model="searchData">
                <el-form-item prop="name" label="名称">
                    <el-input v-model="searchData.name" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="birthday" label="生日">
                    <el-input v-model="searchData.birthday" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="note" label="备注">
                    <el-input v-model="searchData.note" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="type" label="类型">
                    <el-input v-model="searchData.type" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="version" label="版本号">
                    <el-input v-model="searchData.version" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="article" label="文章">
                    <el-input v-model="searchData.article" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="openTime" label="打开时间">
                    <el-input v-model="searchData.openTime" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="startTime" label="开始时间">
                    <el-input v-model="searchData.startTime" placeholder="请输入" />
                </el-form-item>
                <el-form-item prop="tagName" label="tag名称">
                    <el-input v-model="searchData.tagName" placeholder="请输入" />
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" :icon="Search" @click="handleSearch">
                        查询
                    </el-button>
                    <el-button :icon="Refresh" @click="resetSearch">
                        重置
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card v-loading="loading" shadow="never">
            <div class="toolbar-wrapper">
                <div>
                    <el-button type="primary" :icon="CirclePlus" @click="dialogVisible = true">
                        新增
                    </el-button>
                    <!-- <el-button type="danger" :icon="Delete">
                      批量删除
                    </el-button> -->
                </div>
                <div>
                    <!-- <el-tooltip content="下载">
                      <el-button type="primary" :icon="Download" circle />
                    </el-tooltip> -->
                    <el-tooltip content="刷新当前页">
                        <el-button type="primary" :icon="RefreshRight" circle @click="getTableData" />
                    </el-tooltip>
                </div>
            </div>
            <div class="table-wrapper">
                <el-table :data="tableData" size="small" @sort-change="handleSortChange" stripe>
                    <el-table-column type="selection" width="50" align="center" />

                    <el-table-column prop="name" label="名称" align="center" sortable="custom" />
                    <el-table-column prop="birthday" label="生日" align="center" sortable="custom" />
                    <el-table-column prop="note" label="备注" align="center" sortable="custom" />
                    <el-table-column prop="type" label="类型" align="center" sortable="custom" />
                    <el-table-column prop="version" label="版本号" align="center" sortable="custom" />
                    <el-table-column prop="article" label="文章" align="center" sortable="custom" />
                    <el-table-column prop="openTime" label="打开时间" align="center" sortable="custom" />
                    <el-table-column prop="startTime" label="开始时间" align="center" sortable="custom" />
                    <el-table-column prop="tagName" label="tag名称" align="center" sortable="custom" />
                    <el-table-column prop="enabled" label="状态" align="center" sortable="custom">
                        <template #default="scope">
                            <el-tag v-if="scope.row.enabled" type="success" effect="plain" disable-transitions>
                                启用
                            </el-tag>
                            <el-tag v-else type="danger" effect="plain" disable-transitions>
                                禁用
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" align="center" />
                    <el-table-column fixed="right" label="操作" width="280" align="center">
                        <template #default="scope">
                            <el-button-group>
                                <el-button
                                        :type="scope.row.enabled ? 'danger' : 'success'" text bg size="small"
                                        v-if="checkPermission(['admin'])" @click="updateenabled(scope.row)"
                                >
                                    {{ scope.row.enabled ? '禁用' : '启用'
                                    }}
                                </el-button>
                                <el-button type="primary" text bg size="small" @click="handleUpdate(scope.row)">
                                    修改
                                </el-button>
                                <el-button type="danger" text bg size="small" @click="handleDelete(scope.row)">
                                    删除
                                </el-button>
                            </el-button-group>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <div class="pager-wrapper">
                <el-pagination
                        background :layout="paginationData.layout" :page-sizes="paginationData.pageSizes"
                        :total="paginationData.total" :page-size="paginationData.pageSize" :current-page="paginationData.currentPage"
                        @size-change="handleSizeChange" @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <!-- 新增 编辑 start  -->
        <el-dialog v-model="dialogVisible" @closed="resetForm" width="50%" :fullscreen="isFullscreen" :show-close="false">
            <!-- 自定义标题样式 -->
            <template #header>
                <div style="display: flex; align-items: center; justify-content: space-between;">
                    <div style="display: flex; align-items: center;">
                        <el-icon style="margin-right: 8px;">
                            <Edit />
                        </el-icon>
                        <span style="font-size: 18px; font-weight: bold;"> {{ formData.id === undefined ? '新增' : '修改' }}</span>
                    </div>
                    <!-- 右侧按钮组 -->
                    <div style="display: flex; align-items: center;">
                        <!-- 全屏按钮 -->
                        <el-button type="text" @click="toggleFullscreen">
                            <SvgIcon :name="fullscreenSvgName" class="svg-icon" />
                        </el-button>
                        <!-- 自定义关闭按钮 -->
                        <el-button type="text" @click="dialogVisible = false" :icon="Close" />
                    </div>
                </div>
            </template>
            <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px" label-position="right">
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item prop="name" label="名称" label-width="100px">
                                    <el-input v-model="formData.name" placeholder="请输入名称" />
                                </el-form-item>
                            </el-col>

                                <el-col :span="12">
                                    <el-form-item prop="birthday" label="生日" label-width="100px">
                                        <el-input v-model="formData.birthday" placeholder="请输入生日" />
                                    </el-form-item>
                                </el-col>
                        </el-row>
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item prop="note" label="备注" label-width="100px">
                                    <el-input v-model="formData.note" placeholder="请输入备注" />
                                </el-form-item>
                            </el-col>

                                <el-col :span="12">
                                    <el-form-item prop="type" label="类型" label-width="100px">
                                        <el-input v-model="formData.type" placeholder="请输入类型" />
                                    </el-form-item>
                                </el-col>
                        </el-row>
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item prop="version" label="版本号" label-width="100px">
                                    <el-input v-model="formData.version" placeholder="请输入版本号" />
                                </el-form-item>
                            </el-col>

                                <el-col :span="12">
                                    <el-form-item prop="article" label="文章" label-width="100px">
                                        <el-input v-model="formData.article" placeholder="请输入文章" />
                                    </el-form-item>
                                </el-col>
                        </el-row>
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item prop="openTime" label="打开时间" label-width="100px">
                                    <el-input v-model="formData.openTime" placeholder="请输入打开时间" />
                                </el-form-item>
                            </el-col>

                                <el-col :span="12">
                                    <el-form-item prop="startTime" label="开始时间" label-width="100px">
                                        <el-input v-model="formData.startTime" placeholder="请输入开始时间" />
                                    </el-form-item>
                                </el-col>
                        </el-row>
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item prop="tagName" label="tag名称" label-width="100px">
                                    <el-input v-model="formData.tagName" placeholder="请输入tag名称" />
                                </el-form-item>
                            </el-col>

                        </el-row>
            </el-form>

            <!-- 底部按钮 -->
            <template #footer>
                <el-button @click="dialogVisible = false">
                    取消
                </el-button>
                <el-button type="primary" @click="handleCreateOrUpdate" :loading="loading">
                    确认
                </el-button>
            </template>
        </el-dialog>
        <!-- 新增 编辑 end -->
    </div>
</template>

<style lang="scss" scoped>

</style>
