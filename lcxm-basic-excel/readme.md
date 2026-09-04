# lcxm-basic-excel

Excel 读取、写入工具模块。

`lcxm-basic-excel` 面向需要处理 Excel 的项目，提供 FastExcel 工具、POI 导入导出、大文件读取、注解式导出等能力。

## 主包 Tree

当前 Java 包仍沿用历史路径 `cn.xuqiudong.basic.core.util`：

```text
cn.xuqiudong.basic.core.util
|-- excel
|   `-- FastExcelUtil                    # FastExcel 工具入口
`-- poi
    |-- LargeExcelImportFacade           # 基于 POI SAX 的大文件读取门面
    |-- LargeExcelImportFacade_back      # 历史备份实现，不建议新代码使用
    |-- export
    |   `-- ExportExcel                  # 旧版注解式导出工具
    `-- excel
        |-- enmus                        # Excel 类型、数据类型、异常类型
        |-- exception                    # Excel 导出异常
        |-- export
        |   |-- ExcelExportUtil          # POI 导出入口
        |   |-- annotation               # 导出字段注解
        |   |-- model                    # 导出参数、字段模型、多级表头
        |   `-- util                     # 单元格、合并、反射等辅助
        `-- util                         # POI 通用工具
```

## 主要能力

| 能力 | 说明 |
| --- | --- |
| FastExcel 工具 | `FastExcelUtil` 提供基于 FastExcel 的基础封装。 |
| 大文件读取 | `LargeExcelImportFacade` 使用 POI SAX 方式按行读取，适合较大 xlsx。 |
| 注解式导出 | `@ExportField` 描述导出列，`ExcelExportUtil` 负责生成工作簿。 |
| 多级表头 | `ExportParam`、`MultiHeader` 支持导出表头组织。 |
| HTTP 输出 | 部分导出工具支持写入 `HttpServletResponse`。 |

## Maven

```xml
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-basic-excel</artifactId>
    <version>4.0.0</version>
</dependency>
```

## 使用入口

常用入口：

| 场景 | 入口 |
| --- | --- |
| FastExcel 简单处理 | `FastExcelUtil` |
| 大 xlsx 流式读取 | `LargeExcelImportFacade` |
| 基于注解导出 | `ExcelExportUtil` + `@ExportField` + `ExportParam` |
| 旧版导出 | `ExportExcel` |

## 依赖边界

适合放入本模块：

- Excel 读取、写入、导入、导出工具。
- Excel 注解、导出模型、表头模型。
- POI / FastExcel 相关通用封装。

不适合放入本模块：

- 具体业务导入模板。
- 业务字段校验和业务落库逻辑。
- 与 Excel 无关的文件存储、附件、OSS 能力。

## 注意点

- 当前包名保留历史结构，后续如迁移到 `cn.xuqiudong.basic.excel`，需要作为兼容性变更处理。
- `LargeExcelImportFacade_back` 是备份类，新代码优先使用 `LargeExcelImportFacade`。
- 大文件导出优先使用 `SXSSFWorkbook` 或 FastExcel，避免一次性加载过多数据。
- 导入导出的业务校验建议放在业务项目，不放在本模块。
