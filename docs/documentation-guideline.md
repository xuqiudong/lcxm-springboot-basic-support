# 文档编写规范

## 目标

让本项目的文档像一个可复用产品，而不是零散项目笔记。

文档需要回答三个问题：

- 这是什么。
- 我该如何选择和接入。
- 变更、边界、兼容性在哪里查。

## 文档分层

```text
/
|-- readme.md              # 项目总入口
|-- CHANGELOG.md           # 项目级变更记录
|-- LICENSE                # 开源协议
`-- docs/                  # 项目级文档

/lcxm-xxx
|-- readme.md              # 模块入口
`-- docs/                  # 模块级文档
```

## 根目录文档

### readme.md

根 `readme.md` 面向第一次接触项目的人，只写项目级信息。

应包含：

- 项目定位：一句话说明这个项目解决什么问题。
- Features：列核心能力，不展开实现细节。
- Modules：列模块表格，说明每个模块的职责。
- Quick Start：给最小 Maven 依赖示例。
- Documentation：链接到项目级文档和模块文档。
- Release：说明版本、CHANGELOG、tag、发布入口。
- License：说明协议。

不应包含：

- 某个模块的详细接入步骤。
- 大段历史背景。
- 临时讨论结论。
- 过多示例代码。

### CHANGELOG.md

根 `CHANGELOG.md` 记录项目级发布变更。

建议格式：

```md
## [x.y.z] - yyyy-MM-dd

### Added
- lcxm-basic-third: 新增第三方入站/出站对接基础模块。

### Changed
- lcxm-basic-framework: 调整 xxx。

### Fixed
- lcxm-basic-core: 修复 xxx。

### Removed
- lcxm-xxx: 移除 xxx。
```

要求：

- 只记录对使用者有意义的变化。
- 按模块名前缀归类，方便定位影响范围。
- 不记录“格式化代码”“调整注释”这类无行为影响内容，除非对使用者有影响。

## docs 目录

根 `docs/` 放项目级文档。

推荐文件：

```text
docs/
|-- architecture.md              # 整体架构、模块边界、依赖方向
|-- dependency.md                # JDK/Spring/第三方依赖版本策略
|-- release.md                   # 发版流程
|-- documentation-guideline.md   # 文档编写规范
|-- roadmap.md                   # 后续规划
`-- notes/                       # 临时笔记，成熟后合并到正式文档
```

## 模块文档

每个模块保留自己的 `readme.md`。

模块 `readme.md` 应包含：

- 模块定位。
- 主包 Tree。
- 接入摘要。
- 关键配置。
- 依赖边界。
- 模块 docs 链接。

模块 `docs/` 放模块级专题文档，例如：

```text
lcxm-basic-third/docs/
|-- design.md
|-- 开发流程-接入我方.md
|-- 开发流程-调用他方.md
|-- 第三方入站接口文档.md
`-- spring-version-adapter.md
```

模块文档要求：

- `design.md` 写设计边界、关键结论、流程。
- `开发流程-xxx.md` 写给开发人员，强调接入步骤。
- `接口文档.md` 写给调用方，避免暴露内部实现细节。
- 兼容性、迁移说明单独成文，不塞进 README。

## 内容原则

- README 是入口，不是大全。
- 设计文档写“为什么这样设计”和“边界在哪里”。
- 开发流程写“按什么步骤接入”。
- 接口文档写“调用方如何调用”。
- CHANGELOG 写“使用者需要知道的变化”。

## 写法要求

- 标题简短，优先使用二级标题。
- 表格用于模块列表、职责边界、版本兼容矩阵。
- Tree 用于表达代码结构和文档结构。
- 示例代码只保留最小必要片段。
- 不写大段历史说明；历史结论放到 `docs/notes/`，成熟后再合并。
- 避免同一内容在多个文档中重复展开；可以在入口文档里链接到详细文档。

## 推荐阅读入口

根 README 推荐放置如下导航：

```md
## Documentation

| 你要做什么 | 文档 |
| --- | --- |
| 了解项目整体结构 | docs/architecture.md |
| 查看模块说明 | 各模块 readme.md |
| 查看版本变化 | CHANGELOG.md |
| 查看发版流程 | docs/release.md |
| 查看文档规范 | docs/documentation-guideline.md |
```

## 发版文档约定

一次正式发布通常更新：

- 根 `CHANGELOG.md`
- 根 `readme.md` 中的版本、模块、文档链接，若有变化
- 受影响模块的 `readme.md`
- 受影响模块的专题文档
- Git tag / Release Notes

发布说明优先从 `CHANGELOG.md` 当前版本复制生成。
