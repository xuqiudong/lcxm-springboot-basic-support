<template>
  <el-container style="height: 100vh; overflow: hidden;">
    <!-- 可折叠左侧配置区 -->
    <el-aside 
      :width="sidebarCollapsed ? '60px' : '500px'" 
      style="background-color: #f5f7fa; border-right: 1px solid #eee; transition: width 0.3s ease; overflow: hidden;"
    >
      <div style="height: 100%; display: flex; flex-direction: column;">
        <!-- 折叠/展开按钮（区分状态） -->
        <div style="height: 50px; display: flex; align-items: center; justify-content: flex-end; padding-right: 15px; border-bottom: 1px solid #eee;">
          <el-button 
            :icon="sidebarCollapsed ? Expand : Fold" 
            size="small" 
            circle 
            @click="sidebarCollapsed = !sidebarCollapsed"
            :title="sidebarCollapsed ? '展开配置' : '折叠配置'"
          />
        </div>
        
        <!-- 配置内容（仅在展开时显示） -->
        <div v-if="!sidebarCollapsed" style="flex: 1; overflow-y: auto; padding: 20px;">
          <!-- 全局配置区域 -->
          <div style="margin-bottom: 20px;">
            <h3 style="margin-top: 0; margin-bottom: 15px; font-size: 16px; color: #1890ff;">全局配置</h3>
            <div style="color: #666; margin-bottom: 15px; font-size: 13px;">
              <el-icon size="14"><InfoFilled /></el-icon>
              <span>基础配置为所有表的默认设置，单表导出时可单独覆盖</span>
            </div>
            
            <el-form :model="globalConfig" label-width="100px" size="small">
              <el-form-item label="基础包名" required>
                <el-input 
                  v-model="globalConfig.basePackage" 
                  placeholder="例如：cn.xuqiudong.generator"
                  size="small"
                />
              </el-form-item>

              <el-form-item label="模块名称" required>
                <el-input 
                  v-model="globalConfig.module" 
                  placeholder="作为子包和请求路径"
                  size="small"
                />
              </el-form-item>

              <el-form-item label="作者">
                <el-input v-model="globalConfig.author" placeholder="默认系统用户名" size="small" />
              </el-form-item>

              <el-form-item label="输出目录">
                <el-input v-model="globalConfig.outputDir" placeholder="默认项目src/main/java" size="small" />
              </el-form-item>

              <el-form-item label="注解配置">
                <el-checkbox-group v-model="globalConfig.annotations" size="small">
                  <el-checkbox label="lombok" name="annotation">Lombok</el-checkbox>
                  <el-checkbox label="springdoc" name="annotation">SpringDoc</el-checkbox>
                  <el-checkbox label="plus" name="annotation">Plus</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="其他">
                <el-checkbox v-model="globalConfig.open" label="生成后打开目录" size="small" />
              </el-form-item>
            </el-form>
          </div>
          
          <el-divider style="margin: 15px 0;" />
          
          <!-- 模板生成策略（与全局配置风格统一） -->
          <div>
            <h3 style="margin-top: 0; margin-bottom: 15px; font-size: 16px; color: #1890ff;">模板生成策略</h3>
            
            <!-- 与全局配置保持一致的表单样式 -->
            <el-form :model="templateStrategy" label-width="100px" size="small">
              <el-form-item label="实体(Entity)">
                <el-checkbox v-model="templateStrategy.entity.enable" label="启用" size="small" />
                <el-input 
                  v-model="templateStrategy.entity.supperType" 
                  placeholder="父类（如：BaseMpEntity）"
                  size="small"
                  style="margin-top: 5px;"
                />
                <el-checkbox 
                  v-model="templateStrategy.entity.supperTypeWithGeneric" 
                  label="父类带泛型"
                  size="small"
                  style="margin-top: 5px;"
                />
              </el-form-item>

              <el-form-item label="Mapper接口">
                <el-checkbox v-model="templateStrategy.mapper.enable" label="启用" size="small" />
                <el-input 
                  v-model="templateStrategy.mapper.supperType" 
                  placeholder="父类（如：BaseMapper）"
                  size="small"
                  style="margin-top: 5px;"
                />
              </el-form-item>

              <el-form-item label="Service">
                <el-checkbox v-model="templateStrategy.service.enable" label="启用" size="small" />
                <el-input 
                  v-model="templateStrategy.service.supperType" 
                  placeholder="父类（如：BaseService）"
                  size="small"
                  style="margin-top: 5px;"
                />
              </el-form-item>

              <el-form-item label="Controller">
                <el-checkbox v-model="templateStrategy.controller.enable" label="启用" size="small" />
                <el-input 
                  v-model="templateStrategy.controller.supperType" 
                  placeholder="父类（如：BaseController）"
                  size="small"
                  style="margin-top: 5px;"
                />
              </el-form-item>

              <el-form-item label="MapperXML">
                <el-checkbox v-model="templateStrategy.xml.enable" label="启用" size="small" />
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <!-- 折叠状态下仅显示两个核心图标 -->
        <div v-if="sidebarCollapsed" style="flex: 1; display: flex; flex-direction: column; align-items: center; padding-top: 20px; gap: 20px;">
          <el-icon size="24" title="全局配置"><Setting /></el-icon>
          <el-icon size="24" title="模板策略"><Document /></el-icon>
        </div>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部标题栏 -->
      <el-header style="height: 50px; border-bottom: 1px solid #eee; padding: 0 20px; display: flex; align-items: center; justify-content: space-between;">
        <div style="display: flex; align-items: center;">
          <h1 style="font-size: 18px; margin: 0;">代码生成器</h1>
        </div>
      </el-header>

      <!-- 表格内容区 -->
      <el-main style="padding: 20px; overflow: auto;">
        <!-- 首次使用提示 -->
        <el-alert 
          v-if="!hasConfigured" 
          title="请先完成左侧全局配置，再进行表操作" 
          type="info" 
          closable
          @close="handleCloseAlert"
          style="margin-bottom: 15px;"
        />

        <!-- 搜索栏与批量导出按钮在同一行（表格上方） -->
        <div style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px; flex-wrap: wrap;">
          <el-input 
            v-model="tableNameSearch" 
            placeholder="输入表名搜索" 
            style="width: 200px;"
            @keyup.enter="handleSearch"
            size="small"
          />
          
          <el-input 
            v-model="tableCommentSearch" 
            placeholder="输入备注搜索" 
            style="width: 200px;"
            @keyup.enter="handleSearch"
            size="small"
          />
          
          <el-button @click="handleSearch" size="small">搜索</el-button>
          
          <!-- 批量导出按钮放在搜索栏右侧 -->
          <el-button 
            type="primary" 
            icon="Download"
            @click="showBatchExportDialog = true"
            :disabled="selectedTableIds.length === 0"
            size="small"
            style="margin-left: auto;"
          >
            批量导出（{{ selectedTableIds.length }}）
          </el-button>
        </div>

        <!-- 表列表（表头上方就是搜索和批量导出区域） -->
        <div style="border: 1px solid #eee; border-radius: 4px; overflow: hidden;">
          <el-table
            :data="tableList"
            border
            style="width: 100%;"
            @selection-change="handleTableSelect"
            size="small"
          >
            <!-- 全选复选框列 -->
            <el-table-column type="selection" width="55" />
            <el-table-column label="序号" type="index" width="60" />
            <el-table-column label="表名" prop="name" sortable />
            <el-table-column label="表注释" prop="comment" />
            <el-table-column label="字段数量" prop="fieldCount" />
            <el-table-column label="创建时间" prop="createTime" sortable />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="text" @click="showSingleExportDialog(scope.row)" size="small">导出</el-button>
                <el-button type="text" @click="showCodePreviewDialog(scope.row)" size="small">代码片段</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页控件 -->
          <div style="margin-top: 15px; display: flex; justify-content: flex-end; padding-right: 15px; padding-bottom: 15px;">
            <el-pagination
              v-model:current-page="page"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handlePageSizeChange"
              size="small"
            />
          </div>
        </div>
      </el-main>
    </el-container>

    <!-- 批量导出弹窗 -->
    <el-dialog
      v-model="showBatchExportDialog"
      title="批量导出配置"
      width="400px"
      size="small"
    >
      <el-form :model="batchExportConfig" label-width="80px" size="small">
        <el-form-item label="基础包名">
          <el-input v-model="batchExportConfig.basePackage" size="small" />
        </el-form-item>
        <el-form-item label="模块名称">
          <el-input v-model="batchExportConfig.module" size="small" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBatchExportDialog = false" size="small">取消</el-button>
        <el-button type="primary" @click="handleBatchExport" size="small">确认导出</el-button>
      </template>
    </el-dialog>

    <!-- 单表导出弹窗 -->
    <el-dialog
      v-model="showSingleExportDialogVisible"
      title="单表导出配置"
      width="600px"
      size="small"
    >
      <div v-if="currentTable">
        <el-form :model="singleExportConfig" label-width="80px" size="small">
          <el-form-item label="表名">
            <el-input v-model="currentTable.name" disabled size="small" />
          </el-form-item>
          <el-form-item label="基础包名">
            <el-input v-model="singleExportConfig.basePackage" size="small" />
          </el-form-item>
          <el-form-item label="模块名称">
            <el-input v-model="singleExportConfig.module" size="small" />
          </el-form-item>
        </el-form>

        <el-divider content-position="left">字段配置</el-divider>
        <el-table
          :data="currentTable.fields"
          border
          style="width: 100%;"
          size="small"
        >
          <el-table-column label="字段名" prop="name" />
          <el-table-column label="类型" prop="type" />
          <el-table-column label="查询方式">
            <template #default="scope">
              <el-select v-model="scope.row.queryType" size="small">
                <el-option label="精确" value="exact" />
                <el-option label="模糊" value="fuzzy" />
                <el-option label="不查询" value="none" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="列表显示">
            <template #default="scope">
              <el-switch v-model="scope.row.showInList" size="small" />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="showSingleExportDialogVisible = false" size="small">取消</el-button>
        <el-button type="primary" @click="handleSingleExport" size="small">确认导出</el-button>
      </template>
    </el-dialog>

    <!-- 代码片段预览弹窗 -->
    <el-dialog
      v-model="showCodePreviewDialogVisible"
      title="代码片段预览"
      width="800px"
      size="small"
    >
      <el-tabs v-model="activePreviewTab" size="small">
        <el-tab-pane label="Entity" name="entity">
          <pre v-if="codePreview.entity" class="code-block">{{ codePreview.entity }}</pre>
        </el-tab-pane>
        <el-tab-pane label="Mapper.xml" name="mapper">
          <pre v-if="codePreview.mapper" class="code-block">{{ codePreview.mapper }}</pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { 
  Download, 
  Setting, 
  InfoFilled, 
  Expand, 
  Fold,
  Document 
} from '@element-plus/icons-vue'

// 定义接口类型
interface GlobalConfig {
  basePackage: string
  module: string
  author: string
  outputDir: string
  annotations: string[]
  open: boolean
}

interface TemplateStrategy {
  entity: {
    enable: boolean
    supperClass: string
    supperTypeWithGeneric: boolean
  }
  mapper: {
    enable: boolean
    supperClass: string
  }
  service: {
    enable: boolean
    supperClass: string
  }
  controller: {
    enable: boolean
    supperClass: string
  }
  xml: {
    enable: boolean
  }
}

interface TableField {
  name: string
  type: string
  queryType: 'exact' | 'fuzzy' | 'none'
  showInList: boolean
}

interface TableItem {
  id: number
  name: string
  comment: string
  fieldCount: number
  createTime: string
  fields: TableField[]
}

interface ExportConfig {
  basePackage: string
  module: string
}

interface CodePreview {
  entity: string
  mapper: string
}

// 左侧侧边栏折叠状态
const sidebarCollapsed = ref(false)
const hasConfigured = ref(false)

// 全局配置
const globalConfig: GlobalConfig = reactive({
  basePackage: 'cn.xuqiudong.generator',
  module: 'module',
  author: '',
  outputDir: '',
  annotations: ['lombok', 'springdoc', 'plus'],
  open: true
})

// 模板生成策略
const templateStrategy: TemplateStrategy = reactive({
  entity: {
    enable: true,
    supperClass: 'BaseMpEntity',
    supperTypeWithGeneric: true
  },
  mapper: {
    enable: true,
    supperClass: 'BaseMapper'
  },
  service: {
    enable: true,
    supperClass: 'BaseService'
  },
  controller: {
    enable: true,
    supperClass: 'BaseController'
  },
  xml: {
    enable: true
  }
})

// 表列表相关
const tableList = ref<TableItem[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const tableNameSearch = ref('')
const tableCommentSearch = ref('')
const selectedTableIds = ref<number[]>([])

// 批量导出相关
const showBatchExportDialog = ref(false)
const batchExportConfig: ExportConfig = reactive({
  basePackage: '',
  module: ''
})

// 单表导出相关
const showSingleExportDialogVisible = ref(false)
const currentTable = ref<TableItem | null>(null)
const singleExportConfig: ExportConfig = reactive({
  basePackage: '',
  module: ''
})

// 代码预览相关
const showCodePreviewDialogVisible = ref(false)
const activePreviewTab = ref('entity')
const codePreview: CodePreview = reactive({
  entity: '',
  mapper: ''
})

// 初始化
onMounted(() => {
  fetchTableList()
  globalConfig.author = 'Vic.xu'
  
  const configState = localStorage.getItem('hasConfigured')
  if (configState === 'true') {
    hasConfigured.value = true
  }
})

// 关闭提示时记录状态
const handleCloseAlert = () => {
  hasConfigured.value = true
  localStorage.setItem('hasConfigured', 'true')
}

// 搜索处理函数
const handleSearch = () => {
  fetchTableList()
}

// 表格数据加载
const fetchTableList = () => {
  // 模拟数据
  const data: TableItem[] = [
    {
      id: 1,
      name: 'test_generate',
      comment: '测试生成表',
      fieldCount: 8,
      createTime: '2024-01-01',
      fields: [
        { name: 'id', type: 'bigint', queryType: 'exact', showInList: true },
        { name: 'name', type: 'varchar', queryType: 'fuzzy', showInList: true }
      ]
    },
    {
      id: 2,
      name: 'sys_user',
      comment: '系统用户表',
      fieldCount: 12,
      createTime: '2024-01-02',
      fields: []
    },
    {
      id: 3,
      name: 'sys_role',
      comment: '系统角色表',
      fieldCount: 5,
      createTime: '2024-01-03',
      fields: []
    },
    {
      id: 4,
      name: 'sys_menu',
      comment: '系统菜单表',
      fieldCount: 10,
      createTime: '2024-01-04',
      fields: []
    },
    {
      id: 5,
      name: 'sys_dept',
      comment: '系统部门表',
      fieldCount: 7,
      createTime: '2024-01-05',
      fields: []
    }
  ]
  
  // 应用搜索过滤
  let filteredData = [...data]
  if (tableNameSearch.value) {
    filteredData = filteredData.filter(item => 
      item.name.includes(tableNameSearch.value)
    )
  }
  if (tableCommentSearch.value) {
    filteredData = filteredData.filter(item => 
      item.comment.includes(tableCommentSearch.value)
    )
  }
  
  tableList.value = filteredData
  total.value = filteredData.length
}

// 表格选择事件
const handleTableSelect = (selection: TableItem[]) => {
  selectedTableIds.value = selection.map(item => item.id)
}

// 分页页码变更
const handlePageChange = (val: number) => {
  page.value = val
}

// 分页大小变更
const handlePageSizeChange = (val: number) => {
  pageSize.value = val
  page.value = 1
}

// 单表导出弹窗
const showSingleExportDialog = (table: TableItem) => {
  currentTable.value = table
  singleExportConfig.basePackage = globalConfig.basePackage
  singleExportConfig.module = globalConfig.module
  showSingleExportDialogVisible.value = true
}

// 代码预览弹窗
const showCodePreviewDialog = (table: TableItem) => {
  currentTable.value = table
  codePreview.entity = `package ${globalConfig.basePackage}.${globalConfig.module}.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ${table.name.charAt(0).toUpperCase() + table.name.slice(1)} {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 其他字段...
}`
  codePreview.mapper = `<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${globalConfig.basePackage}.${globalConfig.module}.mapper.${table.name.charAt(0).toUpperCase() + table.name.slice(1)}Mapper">
    <!-- 基础CRUD语句 -->
</mapper>`
  showCodePreviewDialogVisible.value = true
}

// 批量导出处理
const handleBatchExport = () => {
  showBatchExportDialog.value = false
}

// 单表导出处理
const handleSingleExport = () => {
  showSingleExportDialogVisible.value = false
}
</script>

<style scoped>
:deep(.el-form-item) {
  margin-bottom: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-table .cell) {
  text-align: left !important;
}

.code-block {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  overflow: auto;
  max-height: 400px;
  font-size: 14px;
  line-height: 1.5;
}

:deep(.el-aside) {
  transition: width 0.3s ease;
}

:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

:deep(.el-pagination) {
  margin: 0;
}

:deep(.el-alert) {
  background-color: #f0faff;
  border-color: #91d5ff;
}
</style>
    