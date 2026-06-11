<#-- IDEA formatter control, only for template editing -->
<#if false>
    <!-- @formatter:off -->
</#if>
<#-- vue 页面 -->
<!-- ${table.comments!} -->
<script lang="ts" setup>
    import type { TableInstance, FormInstance, FormRules } from "element-plus"
    import type { ${entity.className}Data } from "./apis/type"
    import { validateUniqueField } from "@@/composables/useFormValidate"
    import { useFullscreen } from "@@/composables/useFullscreen"
    import { usePagination } from "@@/composables/usePagination"
    import { useTableSort } from "@@/composables/useTableSort"
    import { checkPermission } from "@@/utils/permission"
    // import { validateUniqueField } from "@@/composables/useFormValidate"
    import { CirclePlus, Close, Refresh, RefreshRight, Search } from "@element-plus/icons-vue"
    import { cloneDeep } from "lodash-es"
    import * as ${entity.className}Api from "./apis"

    defineOptions({
        // 命名当前组件:  ${table.comments!}
        name: "${entity.className}"
    })

    const loading = ref<boolean>(false)
    const { paginationData, handleCurrentChange, handleSizeChange } = usePagination()
    const { orders, handleSortChange } = useTableSort({})
    const tableRef = ref<TableInstance>()
    // 编辑弹窗是否全屏
    const { isFullscreen, fullscreenSvgName, toggleFullscreen } = useFullscreen()
    // #region 增
    const DEFAULT_FORM_DATA: ${entity.className}Data = {
        <#list entity.fields as field>
        ${field.fieldName}: ${field.tsDefault}, // ${field.comments}
        </#list>
    }

    const dialogVisible = ref<boolean>(false)
    const formRef = ref<FormInstance | null>(null)
    const formData = ref<${entity.className}Data>(cloneDeep(DEFAULT_FORM_DATA))

    const formRules: FormRules<${entity.className}Data> = reactive<FormRules>({
        //name: [{ required: true, trigger: "blur", message: "请输入name" }, { validator: validateUniqueField("${controller.requestMapping}/check", "name", "名称", formData), trigger: "blur" }],
    })

    function handleCreateOrUpdate() {
        formRef.value?.validate((valid) => {
            if (!valid) {
                ElMessage.error("表单校验不通过")
                return
            }
            loading.value = true
            ${entity.className}Api.save(formData.value).then(() => {
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
    function handleDelete(row: ${entity.className}Data) {
        if (!row.id) return
        ElMessageBox.confirm(`正在删除XX：${r'${row.id}'}，确认删除？`, "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(() => {
            ${entity.className}Api.del(row.id!).then(() => {
                ElMessage.success("删除成功")
                getTableData()
            })
        })
    }
    // #endregion

    // #region 批量删除
    const multipleSelection = ref<${entity.className}Data[]>([])
    function handleSelectionChange(val: ${entity.className}Data[]) {
        multipleSelection.value = val
    }

    // 点击行 选中 / 取消选中
    function handleRowClick(row: ${entity.className}Data) {
        if (tableRef.value) {
            const isSelected = multipleSelection.value.includes(row)
            // 切换行的选中状态
            tableRef.value.toggleRowSelection(row, !isSelected)
        }
    }

    function handleBatchDelete(){
        ElMessageBox.confirm(
            `确认批量删除选中的${r'${multipleSelection.value.length}'}条记录吗？`,
            "提示",
            {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(() => {
            loading.value = true
            const ids: string[] = multipleSelection.value.map(item => item.id!)
            ${entity.className}Api.batchDel(ids).then(() => {
                ElMessage.success("删除成功")
                getTableData()
            })
        }).finally(() => {
                loading.value = false
                multipleSelection.value = []
            }
        )
    }
    //#endregion

    // #region 改
    function handleUpdate(row: ${entity.className}Data) {
        ${entity.className}Api.detail(row.id!).then(({ data }) => {
            formData.value = data
            //  formData.value = cloneDeep(row)
            dialogVisible.value = true
        })

    }
    // #endregion

    // #region 查
    const tableData = ref<${entity.className}Data[]>([])
    const searchFormRef = ref<FormInstance | null>(null)
    const searchData = reactive({
        <#list entity.fields as field>
        ${field.fieldName}: null, // ${field.comments}
        </#list>
    })
    function getTableData() {
        loading.value = true
        ${entity.className}Api.queryPage({
            page: paginationData.currentPage,
            size: paginationData.pageSize,
            orders: orders.value,
            ...searchData
        }).then(({ data }) => {
            paginationData.total = data.total
            tableData.value = data.datas
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
    function updateEnabled(row: ${entity.className}Data) {
        const id = row.id
        if (!id) return
        const enbale = row.enabled
        const text = enbale ? "禁用" : "启用"
        ElMessageBox.confirm(`确认${r'${text}'}xx吗`, "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(() => {
            ${entity.className}Api.updateEnable(id, !enbale).then(() => {
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
            <el-form ref="searchFormRef" :inline="true" :model="searchData" size="small">
                <#list entity.fields as field>
                <el-form-item prop="${field.fieldName}" label="${field.comments}">
                    <el-input v-model="searchData.${field.fieldName}" placeholder="请输入" />
                </el-form-item>
                </#list>

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
                    <el-button type="danger" :icon="Delete" :disabled="multipleSelection.length === 0" @click="handleBatchDelete">
                        批量删除
                    </el-button>
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
                <el-table :data="tableData" ref="tableRef" size="small" @sort-change="handleSortChange" stripe
                          @row-click="handleRowClick" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="50" align="center" />

                    <#list entity.fields as field>
                    <el-table-column prop="${field.fieldName}" label="${field.comments}" width="150" align="center"  sortable="custom" show-overflow-tooltip />
                    </#list>
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
                                        v-if="checkPermission(['admin'])" @click="updateEnabled(scope.row)"
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
                <#list entity.fields as field>
                <#-- 判断当前索引是否为偶数（0, 2, 4...），如果是，则开启一个新的 el-row -->
                    <#if field_index % 2 == 0>
                        <el-row :gutter="20">
                            <#-- 输出当前字段（左边的 el-col） -->
                            <el-col :span="12">
                                <el-form-item prop="${field.fieldName}" label="${field.comments}" label-width="100px">
                                    <el-input v-model="formData.${field.fieldName}" placeholder="请输入${field.comments}" />
                                </el-form-item>
                            </el-col>

                            <#-- 判断当前字段后面是否还有字段（has_next），如果有，则输出右边的 el-col -->
                            <#if field_has_next>
                                <#assign nextField = entity.fields[field_index + 1]>
                                <el-col :span="12">
                                    <el-form-item prop="${nextField.fieldName}" label="${nextField.comments}" label-width="100px">
                                        <el-input v-model="formData.${nextField.fieldName}" placeholder="请输入${nextField.comments}" />
                                    </el-form-item>
                                </el-col>
                            </#if>
                        </el-row>
                    </#if>
                </#list>
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
