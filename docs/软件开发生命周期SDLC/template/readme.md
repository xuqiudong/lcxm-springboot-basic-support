## 目录结构
my-personal-project/
├── README.md                 # 【门户】项目介绍、快速启动、技术栈
├── CHANGELOG.md              # 【导航】更新日志 + 各版本文档链接索引
├── TECH_DEBT.md              # 【账本】技术债务清单 (跨版本累积)
├── ROADMAP.md                # 【规划】未来功能规划池
│
└── docs/                     # 【档案室】所有详细文档
│
├── templates/            # 【模板库】(只存一份，新建版本时复制)
│   ├── 01_Requirements_Template.md
│   ├── 02_Architecture_Template.md
│   └── 03_Plan_Template.md
│
├── v1.0_mvp/             # 【版本包】MVP 初始版本 (已完成)
│   ├── 01_Requirements.md    # MVP 需求列表
│   ├── 02_Architecture.md    # MVP 架构设计 (ER 图、架构图)
│   ├── 03_Plan.md            # MVP 开发计划 (已完成，可留作复盘)
│   └── diagrams/             # 该版本专属图表源文件
│       ├── er_v1.drawio
│       └── arch_v1.png
│
├── v1.1_search/          # 【版本包】搜索与导出功能 (示例)
│   ├── 01_Requirements.md    # ⚡ 增量需求 (只写新增/修改的部分)
│   ├── 02_Architecture.md    # ⚡ 架构变更 (只写改了哪、为什么改)
│   ├── 03_Plan.md            # ⚡ 迭代计划 (影响分析、测试重点)
│   └── diagrams/
│       ├── er_diff_v1.1.drawio # 差异 ER 图
│       └── search_flow.png
│
├── v1.2_report/          # 【版本包】报表功能 (未来)
│   ├── ... (同上结构)
│
└── 00_Ops/               # 【运维】不随版本变的通用运维文档
├── deployment.md     # 部署手册 (Docker/K8s)
├── backup.md         # 备份策略
└── troubleshooting.md# 故障排查