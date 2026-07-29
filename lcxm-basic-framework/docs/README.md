# 项目模块说明

该目录用于记录 `lcxm-basic-framework` 中各个模块的设计目标、使用方式和维护注意事项。

## 文档索引

- [Design](design.md)：framework 设计边界、自动配置和主要流程。
- [Code2Text](code2text.md)：字段 code 到 text 的自动转换能力。
- [License](license.md)：轻量级运行时授权校验能力。

## 组织约定

当前采用一模块一文档的扁平结构：

```text
docs/
  README.md
  design.md
  code2text.md
  license.md
```

当某个模块文档变多时，再拆成子目录：

```text
docs/
  license/
    overview.md
    issuer.md
    runtime.md
```
